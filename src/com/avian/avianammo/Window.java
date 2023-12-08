package avianammo;

import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;

import avianammo.networking.Client;
import avianammo.networking.GameSocket;
import avianammo.networking.Server;
import avianammo.networking.GameSocket.GameState;

public class Window extends JFrame {

    public Window() throws IOException {
        super("Avian Ammo");

        String serverOrClient;

        try (Scanner consoleInput = new Scanner(System.in)) {

            System.out.println("[(S)erver/(c)lient]");

            serverOrClient = consoleInput.nextLine().toLowerCase().trim();
        }

        GameSocket gameSocket;

        System.out.println(serverOrClient);

        Position initialPosition;
        Direction initialDirection;

        if (serverOrClient.equals("s")) {
            Server server = new Server();
            gameSocket = server.listen(4000);
            initialPosition = Seagull.DEFAULT_POSITION;
            initialDirection = Seagull.DEFAULT_DIRECTION;
        } else if (serverOrClient.equals("c")) {
            Client client = new Client();
            gameSocket = client.connect(4000);
            initialPosition = Seagull.OPPONENT_DEFAULT_POSITION;
            initialDirection = Seagull.OPPONENT_DEFAULT_DIRECTION;
        } else {
            throw new IllegalArgumentException(serverOrClient + " is not a valid option.");
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

        this.setSize(500, 500);
        this.setLocationRelativeTo(null);

        Game game = new Game(this, gameSocket, initialPosition, initialDirection);
        game.start();

        this.setVisible(true); // Repaints with new elements
    }
}
