package avianammo;

import java.io.IOException;

import javax.swing.JFrame;

import avianammo.networking.Client;
import avianammo.networking.GameSocket;
import avianammo.networking.Server;
import avianammo.networking.GameSocket.GameState;
import avianammo.pages.GameResultsPage;
import avianammo.pages.HomePage;
import avianammo.pages.WaitingPage;
import avianammo.pages.TimerPage;

public class Window extends JFrame {

    private final HomePage home;

    public Window() throws IOException, InterruptedException {
        super("Avian Ammo");

        this.setSize(1024, 1024);
        this.setLocationRelativeTo(null);

        setVisible(false);

        home = new HomePage();
        add(home);

        home.awaitComponentsLoad();

        setVisible(true);

        loadGame(home.awaitGameRoleChoice());
    }

    public void loadGame(GameRole role) throws IOException {
        remove(home);

        WaitingPage waitingPage = new WaitingPage();
        add(waitingPage);

        waitingPage.awaitComponentsLoad();

        setVisible(true);

        GameSocket gameSocket;

        Position initialPosition;
        Direction initialDirection;

        if (role == GameRole.HOST) {
            Server server = new Server();
            gameSocket = server.listen(home.getPort());
            initialPosition = Seagull.DEFAULT_POSITION;
            initialDirection = Seagull.DEFAULT_DIRECTION;
        } else {
            Client client = new Client();
            gameSocket = client.connect(home.getIp(), home.getPort());
            initialPosition = Seagull.OPPONENT_DEFAULT_POSITION;
            initialDirection = Seagull.OPPONENT_DEFAULT_DIRECTION;
        }

        gameSocket.listenForPackets();

        gameSocket.sendReady();

        remove(waitingPage);

        while (gameSocket.getGameState() != GameState.COUNTING_DOWN) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TimerPage timerPage = new TimerPage(3);
        add(timerPage);

        timerPage.awaitComponentsLoad();

        setVisible(true);

        for(int i = 3; i > 0; i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            timerPage.countOneSecond();
            setVisible(true);
        }

        remove(timerPage);

        gameSocket.startPlay();

        Game game = new Game(this, gameSocket, initialPosition, initialDirection);
        game.start();

        setVisible(true); // Repaints with new elements

        while (gameSocket.getGameState() != GameState.PLAYING) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (gameSocket.getGameState() == GameState.PLAYING) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait for all extra packets to come through
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        remove(game.getCanvas());

        GameResultsPage resultsPage = new GameResultsPage(gameSocket.getGameState());
        add(resultsPage);

        resultsPage.awaitComponentsLoad();

        setVisible(true);
    }
}