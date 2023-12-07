package avianammo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import avianammo.Position;

public class GameSocket {

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread packetListenerThread;
    private GameState gameState;
    private NetworkInformationData latestInformationData;

    public GameSocket(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        gameState = GameState.WAITING_FOR_CONNECTION;
    }

    public enum GameState {
        WAITING_FOR_CONNECTION,  // Waiting for the first game
        WAITING_AFTER_WIN,  // Waiting after this player won
        WAITING_AFTER_LOSS,  // Waiting after opponent won
        WAITING_AFTER_TIE,  // Waiting after both users issued win packets
        WAITING_FOR_OPPONENT_READY,  // Got this player ready, waiting for opponent ready
        WAITING_FOR_PLAYER_READY,  // Got opponent ready, waiting for this player to be ready
        COUNTING_DOWN,  // Both are ready, 
        PLAYING
    }

    public GameState getGameState() {
        return gameState;
    }

    private static final Set<GameState> VALID_STATES_FOR_SENDING_READY = new HashSet<>(Arrays.asList(GameState.WAITING_FOR_CONNECTION, GameState.WAITING_AFTER_WIN, GameState.WAITING_AFTER_LOSS, GameState.WAITING_FOR_PLAYER_READY));
    public void sendReady() throws IllegalStateException, IOException {
        if (!VALID_STATES_FOR_SENDING_READY.contains(gameState)) {
            throw new IllegalStateException(gameState.name() + " is not a valid state to send ready in.");
        }
        
        sendReadyPacket();
        if (gameState == GameState.WAITING_FOR_PLAYER_READY) {
            gameState = GameState.COUNTING_DOWN;
        } else {
            gameState = GameState.WAITING_FOR_OPPONENT_READY;
        }
    }
    
    private static final Set<GameState> VALID_STATES_FOR_RECEIVING_READY = new HashSet<>(Arrays.asList(GameState.WAITING_FOR_CONNECTION, GameState.WAITING_AFTER_WIN, GameState.WAITING_AFTER_LOSS, GameState.WAITING_FOR_OPPONENT_READY));
    private void receivedReady() {
        if (!VALID_STATES_FOR_RECEIVING_READY.contains(gameState)) {
            throw new IllegalStateException(gameState.name() + " is not a valid state to receive ready in.");
        }

        if (gameState == GameState.WAITING_FOR_OPPONENT_READY) {
            gameState = GameState.COUNTING_DOWN;
        } else {
            gameState = GameState.WAITING_FOR_PLAYER_READY;
        }
    }

    public void sendWin() throws IOException {
        outputStream.write(READY);
        if (gameState == GameState.WAITING_AFTER_LOSS) {
            gameState = GameState.WAITING_AFTER_TIE;
        } else if (gameState != GameState.PLAYING) {
            throw new IllegalStateException("Sending a win message at an unexpected time: " + gameState.name());
        } else {
            gameState = GameState.WAITING_AFTER_WIN;
        }
    }

    private void receivedWon() {
        if (gameState == GameState.WAITING_AFTER_WIN) {
            gameState = GameState.WAITING_AFTER_TIE;
        } else if (gameState != GameState.PLAYING) {
            throw new IllegalStateException("Received a win message at an unexpected time: " + gameState.name());
        } else {
            gameState = GameState.WAITING_AFTER_LOSS;
        }
    }

    public record NetworkInformationData(boolean opponentFlapping, double opponentX, double opponentY, List<Position> poopPositions) {}

    private void receivedInformation(byte[] data) {
        int offset = 0;
        byte flags = data[0];
        boolean opponentFlapping = (flags & 1) == 1;
        offset += 1;
        double opponentX = ByteConversion.bytesToDouble(data, offset);
        offset += 8;
        double opponentY = ByteConversion.bytesToDouble(data, offset);
        offset += 8;
        int numPoops = ByteConversion.bytesToInt(data, offset);
        List<Position> poopPositions = new ArrayList<>();
        for (int i = 0; i < numPoops; i++) {
            double poopX = ByteConversion.bytesToDouble(data, offset);
            offset += 8;
            double poopY = ByteConversion.bytesToDouble(data, offset);
            offset += 8;

            poopPositions.add(new Position(poopX, poopY));
        }

        latestInformationData = new NetworkInformationData(opponentFlapping, opponentX, opponentY, poopPositions);
    }

    private static final byte READY = 0x01;
    private static final byte INFORMATION = 0x02;
    private static final byte WON = 0x03;

    private void sendReadyPacket() throws IOException {
        System.out.println("trickS?");
        outputStream.write(READY);
    }

    public void listenForPackets() {
        packetListenerThread = new Thread(() -> {
            while (true) {
                try {
                    byte packetType = inputStream.readNBytes(1)[0];
                    switch (packetType) {
                    case READY:
                        System.out.println("fef");
                        receivedReady();
                        break;
                    case INFORMATION:
                        System.out.println("fefINFO");
                        byte numBytes = inputStream.readNBytes(1)[0];
                        receivedInformation(inputStream.readNBytes(numBytes));
                        break;
                    case WON:
                        System.out.println("fefWWWW");
                        receivedWon();
                        break;
                    default:
                        throw new IllegalArgumentException("Packet type not valid");
                        
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Socket is closed", e);
                }
            }
        });

        packetListenerThread.start();
    }

    public NetworkInformationData getLatestInformationData() {
        return latestInformationData;
    }

    public void close() throws IOException {
        packetListenerThread.interrupt();
        socket.close();
    }
}
