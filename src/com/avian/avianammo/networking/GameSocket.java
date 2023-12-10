package avianammo.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import avianammo.Direction;
import avianammo.Poop;
import avianammo.Position;
import avianammo.RemoteMovement;
import avianammo.Seagull;

public class GameSocket {

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread packetListenerThread;
    private GameState gameState;
    private NetworkInformationData latestInformationData;

    private Map<CountDownLatch, GameState> gameStateChangeLatches = new HashMap<>();
    private Map<CountDownLatch, GameState> gameStateChangeFromLatches = new HashMap<>();

    public GameSocket(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        setGameState(GameState.WAITING_FOR_CONNECTION);
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
            setGameState(GameState.COUNTING_DOWN);
        } else {
            setGameState(GameState.WAITING_FOR_OPPONENT_READY);
        }
    }
    
    private static final Set<GameState> VALID_STATES_FOR_RECEIVING_READY = new HashSet<>(Arrays.asList(GameState.WAITING_FOR_CONNECTION, GameState.WAITING_AFTER_WIN, GameState.WAITING_AFTER_LOSS, GameState.WAITING_FOR_OPPONENT_READY));
    private void receivedReady() {
        if (!VALID_STATES_FOR_RECEIVING_READY.contains(gameState)) {
            throw new IllegalStateException(gameState.name() + " is not a valid state to receive ready in.");
        }

        if (gameState == GameState.WAITING_FOR_OPPONENT_READY) {
            setGameState(GameState.COUNTING_DOWN);
        } else {
            setGameState(GameState.WAITING_FOR_PLAYER_READY);
        }
    }

    public void sendWin() throws IOException {
        outputStream.write(WON);
        if (gameState == GameState.WAITING_AFTER_LOSS) {
            setGameState(GameState.WAITING_AFTER_TIE);
        } else if (gameState != GameState.PLAYING) {
            throw new IllegalStateException("Sending a win message at an unexpected time: " + gameState.name());
        } else {
            setGameState(GameState.WAITING_AFTER_WIN);
        }
    }

    private void receivedWon() {
        if (gameState == GameState.WAITING_AFTER_WIN) {
            setGameState(GameState.WAITING_AFTER_TIE);
        } else if (gameState != GameState.PLAYING) {
            throw new IllegalStateException("Received a win message at an unexpected time: " + gameState.name());
        } else {
            setGameState(GameState.WAITING_AFTER_LOSS);
        }
    }

    public record NetworkInformationData(boolean opponentFlapping, Direction opponentAnimationDirection, double opponentX, double opponentY, byte opponentHealth, Map<Integer, Poop> poops) {}

    private void receivedInformation(byte[] data) throws IOException {
        int offset = 0;
        byte flags = data[0];
        boolean opponentFlapping = (flags & 1) == 1;
        Direction opponentAnimationDirection = ((flags & 0xff) & (1 << 1)) != 0 ? Direction.LEFT : Direction.RIGHT;
        offset += 1;
        byte opponentHealth = data[offset];
        offset += 1;
        double opponentX = ByteConversion.bytesToDouble(data, offset);
        offset += 8;
        double opponentY = ByteConversion.bytesToDouble(data, offset);
        offset += 8;
        int numPoops = ByteConversion.bytesToInt(data, offset);
        Map<Integer, Poop> poops = new HashMap<>();
        offset += 4;
        for (int i = 0; i < numPoops; i++) {
            int id = ByteConversion.bytesToInt(data, offset);
            offset += 4;
            double poopX = ByteConversion.bytesToDouble(data, offset);
            offset += 8;
            double poopY = ByteConversion.bytesToDouble(data, offset);
            offset += 8;

            poops.put(id, Poop.createRemotePoop(new RemoteMovement(new Position(poopX, poopY), Direction.CENTER), id));
        }

        latestInformationData = new NetworkInformationData(opponentFlapping, opponentAnimationDirection, opponentX, opponentY, opponentHealth, poops);
    }

    public void sendInformation(Seagull seagull, byte opponentHealth) throws IOException {
        outputStream.write(INFORMATION);

        int dataSize = 0;
        dataSize += 1; // Flags
        dataSize += 1; // Opponent health
        dataSize += 16; // Seagull x/y
        dataSize += 4; // Number of poops
        dataSize += (8 + 8 + 4) * seagull.getPoops().size(); // Poops time conceived, x/y
        outputStream.write(ByteConversion.intToBytes((dataSize)));

        byte flags = 0;
        if (seagull.isFlapping()) {
            flags |= 1;
        }

        if (seagull.getAnimationDirection() == Direction.LEFT) {
            flags |= (1 << 1);
        }

        outputStream.write(flags);

        outputStream.write(opponentHealth);

        outputStream.write(ByteConversion.doubleToBytes(seagull.getPosition().x()));
        outputStream.write(ByteConversion.doubleToBytes(seagull.getPosition().y()));
        outputStream.write(ByteConversion.intToBytes(seagull.getPoops().size()));
        for (Poop poop : seagull.getPoops().values()) {
            outputStream.write(ByteConversion.intToBytes(poop.getId()));
            outputStream.write(ByteConversion.doubleToBytes(poop.getPosition().x()));
            outputStream.write(ByteConversion.doubleToBytes(poop.getPosition().y()));
        }
    }

    private static final byte READY = 0x01;
    private static final byte INFORMATION = 0x02;
    private static final byte WON = 0x03;

    private void sendReadyPacket() throws IOException {
        outputStream.write(READY);
    }

    public void listenForPackets() {
        packetListenerThread = new Thread(() -> {
            while (true) {
                try {
                    byte packetType = inputStream.readNBytes(1)[0];
                    switch (packetType) {
                    case READY:
                        receivedReady();
                        break;
                    case INFORMATION:
                        int numBytes = ByteConversion.bytesToInt(inputStream.readNBytes(4), 0);
                        receivedInformation(inputStream.readNBytes(numBytes));
                        break;
                    case WON:
                        receivedWon();
                        break;
                    default:
                        throw new IllegalArgumentException("Packet type not valid " + packetType);
                        
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

    public void startPlay() {
        setGameState(GameState.PLAYING);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateAwaitGameStateConditions();
    }

    public void awaitGameState(GameState target) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        gameStateChangeLatches.put(latch, target);
        latch.await();
    }

    public void awaitGameStateChangeFrom(GameState from) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        gameStateChangeFromLatches.put(latch, from);
        latch.await();
    }

    private void updateAwaitGameStateConditions() {
        for (Map.Entry<CountDownLatch, GameState> latchAndTargetState : gameStateChangeLatches.entrySet()) {
            if (latchAndTargetState.getValue() == gameState) {
                latchAndTargetState.getKey().countDown();
            }
        }

        for (Map.Entry<CountDownLatch, GameState> latchAndFromState : gameStateChangeFromLatches.entrySet()) {
            if (latchAndFromState.getValue() != gameState) {
                latchAndFromState.getKey().countDown();
            }
        }
    }
}
