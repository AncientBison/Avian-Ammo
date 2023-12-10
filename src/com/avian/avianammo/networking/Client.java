package avianammo.networking;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public GameSocket connect(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        return new GameSocket(socket);
    }
}
