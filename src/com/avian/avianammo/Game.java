package avianammo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.SwingUtilities;

import avianammo.networking.GameSocket;

import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Game {
    private Controls controls = new Controls();
    
    private Timer gameLoop;
    private final RenderLoop<Window> renderLoop;

    private Seagull seagull;
    private Seagull opponent;
    private GameCanvas canvas;
    private GameSocket socket;

    public Game(Window window, GameSocket socket, Position initialPosition, Direction initialDirection) throws IOException {

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.dispose();
                renderLoop.halt();
                gameLoop.cancel();
            }
        });
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(e -> {
            controls.handleKeyEvent(e);
            return false;
        });

        this.socket = socket;

        renderLoop = new RenderLoop<>(window);
        gameLoop = new Timer();

        seagull = Seagull.createPhysicsSeagull(initialPosition);

        opponent = Seagull.createRemoteSeagull(new RemoteMovement(initialPosition, initialDirection));

        List<Entity> entities = new ArrayList<>();
        entities.add(seagull);
        entities.add(opponent);

        canvas = new GameCanvas(entities);
        canvas.setDoubleBuffered(true);

        window.add(canvas);
        
        SwingUtilities.updateComponentTreeUI(window);
    }

    public void start() {
        gameLoop.scheduleAtFixedRate(new GameLoop(seagull, opponent, controls, socket), 0, (int) (1000.0 / GameLoop.TICKS_PER_SECOND));

        renderLoop.start();
    }

    public void stop() throws InterruptedException {
        renderLoop.halt();
        gameLoop.cancel();
        renderLoop.join();
    }

    public GameCanvas getCanvas() {
        return canvas;
    }
}