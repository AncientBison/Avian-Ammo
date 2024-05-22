package avianammo.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public GameSocket listen(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server waiting for client on port " + serverSocket.getLocalPort());
            Socket socket = serverSocket.accept();
            LOGGER.info("Connection established");
            return new GameSocket(socket);
        }
    }
}
