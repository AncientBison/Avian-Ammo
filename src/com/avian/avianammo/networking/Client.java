package avianammo.networking;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public GameSocket connect(int port) throws UnknownHostException, IOException {
        Socket socket = new Socket("localhost", port);
        System.out.println("Connection established");
        return new GameSocket(socket);
    }
}
