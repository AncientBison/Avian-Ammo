package avianammo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
            gameSocket = server.listen(4000);
            initialPosition = Seagull.DEFAULT_POSITION;
            initialDirection = Seagull.DEFAULT_DIRECTION;
        } else {
            Client client = new Client();
            gameSocket = client.connect(4000);
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
        
        try (ScheduledExecutorService timer = Executors.newScheduledThreadPool(1)) {

            add(timerPage);

            timerPage.awaitComponentsLoad();

            setVisible(true);

            timer.scheduleAtFixedRate(() -> {
                timerPage.countOneSecond();
                setVisible(true);
            }, 1, 1, TimeUnit.SECONDS);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            timer.shutdown();
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