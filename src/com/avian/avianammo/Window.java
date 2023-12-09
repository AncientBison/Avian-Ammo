package avianammo;

import java.io.IOException;

import javax.swing.JFrame;

import avianammo.networking.Client;
import avianammo.networking.GameSocket;
import avianammo.networking.Server;
import avianammo.networking.GameSocket.GameState;
import avianammo.pages.AbstractPage;
import avianammo.pages.HomePage;
import avianammo.pages.WaitingPage;

public class Window extends JFrame {

    private final HomePage home;

    public Window() throws IOException, InterruptedException {
        super("Avian Ammo");

        this.setSize(500, 500);
        this.setLocationRelativeTo(null);

        home = new HomePage();
        add(home);

        setVisible(true);

        loadGame(home.awaitGameRoleChoice());
    }

    public void loadGame(GameRole role) throws IOException {
        remove(home);

        AbstractPage waitingPage = new HomePage();
        add(waitingPage);

        waitingPage.awaitComponentsLoad();

        repaint();
//        setVisible(true);

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

        this.setVisible(true); // Repaints with new elements
    }
}
