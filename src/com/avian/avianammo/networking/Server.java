package avianammo.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
