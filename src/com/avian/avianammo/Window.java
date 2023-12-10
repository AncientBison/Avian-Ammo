package avianammo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import avianammo.networking.Client;
import avianammo.networking.GameSocket;
import avianammo.networking.Server;
import avianammo.networking.GameSocket.GameState;
import avianammo.pages.GameResultsPage;
import avianammo.pages.HomePage;
import avianammo.pages.WaitingPage;
import avianammo.pages.TimerPage;

public class Window extends JFrame {

    private HomePage home;
    private boolean running = true;

    public Window() throws IOException, InterruptedException {
        super("Avian Ammo");

        setSize(1024, 1024);
        setLocationRelativeTo(null);
        setResizable(false);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
            }
        });

        while (running) {
            CountDownLatch gameRoleLatch = new CountDownLatch(1);
            home = new HomePage(gameRoleLatch);
            add(home);

            SwingUtilities.updateComponentTreeUI(this);

            gameRoleLatch.await();

            remove(home);
            loadAndPlayGame(home.awaitGameRoleChoice());
        }
    }

    public void loadAndPlayGame(GameRole role) throws IOException, InterruptedException {
        WaitingPage waitingPage = new WaitingPage();
        add(waitingPage);

        SwingUtilities.updateComponentTreeUI(this);

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

        // Game socket now connected
        try (gameSocket) {
            gameSocket.listenForPackets();

            gameSocket.sendReady();

            gameSocket.awaitGameState(GameState.COUNTING_DOWN);

            remove(waitingPage);

            play(initialPosition, initialDirection, gameSocket);
        }

        GameResultsPage resultsPage = new GameResultsPage(gameSocket.getGameState(), gameSocket);
        add(resultsPage);

        SwingUtilities.updateComponentTreeUI(this);

        gameSocket.awaitGameState(GameState.WAITING_FOR_CONNECTION);

        remove(resultsPage);
    }

    private void play(Position initialPosition, Direction initialDirection, GameSocket gameSocket) throws IOException, InterruptedException {
        TimerPage timerPage = new TimerPage(3);
        add(timerPage);

        SwingUtilities.updateComponentTreeUI(this);

        for(int i = 3; i > 0; i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { //NOSONAR
                // Intentionally ignore
            }
            timerPage.countOneSecond();

            SwingUtilities.updateComponentTreeUI(this);
        }

        remove(timerPage);

        Game game = new Game(this, gameSocket, initialPosition, initialDirection);
        game.start();

        gameSocket.startPlay();

        gameSocket.awaitGameStateChangeFrom(GameState.PLAYING);

        game.stop();

        remove(game.getCanvas());
        
        // Wait for all extra packets to come through
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) { //NOSONAR
            // Intentionally ignore
        }
    }
}