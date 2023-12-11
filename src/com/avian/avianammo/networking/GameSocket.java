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
import avianammo.Dropping;
import avianammo.Position;
import avianammo.RemoteMovement;
import avianammo.Seagull;

public class GameSocket implements AutoCloseable {

    private final Socket socket;
    private final OutputStream outputStream;
    private final InputStream inputStream;
    private Thread packetListenerThread;
    private GameState gameState;
    private NetworkInformationData latestInformationData;

    private final Map<CountDownLatch, GameState> gameStateChangeLatches = new HashMap<>();
    private final Map<CountDownLatch, GameState> gameStateChangeFromLatches = new HashMap<>();

    private boolean listening;

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

    public record NetworkInformationData(boolean opponentFlapping, Direction opponentAnimationDirection, double opponentX, double opponentY, byte seagullHealth, Map<Integer, Dropping> droppings) {}

    private void receivedInformation(byte[] data) throws IOException {
        int offset = 0;
        byte flags = data[0];
        boolean opponentFlapping = (flags & 1) == 1;
        Direction opponentAnimationDirection = ((flags & 0xff) & (1 << 1)) != 0 ? Direction.LEFT : Direction.RIGHT;
        offset += 1;
        byte seagullHealth = data[offset];
        offset += 1;
        double opponentX = ByteConversion.bytesToDouble(data, offset);
        offset += 8;
        double opponentY = ByteConversion.bytesToDouble(data, offset);
        offset += 8;
        int numDroppings = ByteConversion.bytesToInt(data, offset);
        Map<Integer, Dropping> droppings = new HashMap<>();
        offset += 4;
        for (int i = 0; i < numDroppings; i++) {
            int id = ByteConversion.bytesToInt(data, offset);
            offset += 4;
            double droppingX = ByteConversion.bytesToDouble(data, offset);
            offset += 8;
            double droppingY = ByteConversion.bytesToDouble(data, offset);
            offset += 8;

            droppings.put(id, Dropping.createRemoteDropping(new RemoteMovement(new Position(droppingX, droppingY), Direction.CENTER), id));
        }

        latestInformationData = new NetworkInformationData(opponentFlapping, opponentAnimationDirection, opponentX, opponentY, seagullHealth, droppings);
    }

    public void sendInformation(Seagull seagull, byte opponentHealth) throws IOException {
        outputStream.write(INFORMATION);

        int dataSize = 0;
        dataSize += 1; // Flags
        dataSize += 1; // Opponent health
        dataSize += 16; // Seagull x/y
        dataSize += 4; // Number of droppings
        dataSize += (8 + 8 + 4) * seagull.getDroppings().size(); // Droppings time conceived, x/y
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
        outputStream.write(ByteConversion.intToBytes(seagull.getDroppings().size()));
        for (Dropping dropping : seagull.getDroppings().values()) {
            outputStream.write(ByteConversion.intToBytes(dropping.getId()));
            outputStream.write(ByteConversion.doubleToBytes(dropping.getPosition().x()));
            outputStream.write(ByteConversion.doubleToBytes(dropping.getPosition().y()));
        }
    }

    private static final byte READY = 0x01;
    private static final byte INFORMATION = 0x02;
    private static final byte WON = 0x03;

    private void sendReadyPacket() throws IOException {
        outputStream.write(READY);
    }

    public void listenForPackets() {
        listening = true;
        packetListenerThread = new Thread(() -> {
            while (listening) {
                try {
                    byte[] bytesRead = inputStream.readNBytes(1);
                    if (bytesRead.length == 0) {
                        continue;
                    }
                    byte packetType = bytesRead[0];
                    switch (packetType) {
                        case READY -> receivedReady();
                        case INFORMATION -> {
                            int numBytes = ByteConversion.bytesToInt(inputStream.readNBytes(4), 0);
                            receivedInformation(inputStream.readNBytes(numBytes));
                        }
                        case WON -> receivedWon();
                        default ->
                                throw new IllegalArgumentException("Packet type not valid " + packetType);
                    }
                } catch (IOException e) {
                    if (listening) {
                        throw new IllegalStateException("Socket is closed", e);
                    }
                    // Socket closing is expected
                }
            }
        });

        packetListenerThread.start();
    }

    public NetworkInformationData getLatestInformationData() {
        return latestInformationData;
    }

    @Override
    public void close() throws IOException {
        listening = false;
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
