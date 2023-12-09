package avianammo;

import java.io.IOException;

import javax.swing.JFrame;

import avianammo.networking.Client;
import avianammo.networking.GameSocket;
import avianammo.networking.Server;
import avianammo.networking.GameSocket.GameState;
import avianammo.pages.HomePage;
import avianammo.pages.WaitingPage;

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

        while (gameSocket.getGameState() != GameState.COUNTING_DOWN) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        remove(waitingPage);

        Game game = new Game(this, gameSocket, initialPosition, initialDirection);
        game.start();

        setVisible(true); // Repaints with new elements
    }
}
