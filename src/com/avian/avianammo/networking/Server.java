package avianammo.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public GameSocket listen(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server waiting for client on port " + serverSocket.getLocalPort());
            Socket socket = serverSocket.accept();
            System.out.println("Connection established");
            return new GameSocket(socket);
        }
    }
}
