package avianammo.networking;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    public GameSocket connect(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        LOGGER.info("Connection established");
        return new GameSocket(socket);
    }
}
