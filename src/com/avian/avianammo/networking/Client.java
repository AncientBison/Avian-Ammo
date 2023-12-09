package avianammo.networking;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public GameSocket connect(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        System.out.println("Connection established");
        return new GameSocket(socket);
    }
}
