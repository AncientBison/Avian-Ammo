package avianammo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Game {
    private Controls controls = new Controls();
    
    private Timer gameLoop;
    private final RenderLoop<Window> renderLoop;

    private Seagull seagull;
    private GameCanvas canvas;

    private static final Position DEFAULT_POSITION = new Position(50, 50);

    public Game(Window window) throws IOException {

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

        renderLoop = new RenderLoop<>(window);
        gameLoop = new Timer();

        seagull = Seagull.createPhysicsSeagull(DEFAULT_POSITION);

        List<Entity> entities = new ArrayList<>();
        entities.add(seagull);

        canvas = new GameCanvas(entities);
        canvas.setDoubleBuffered(true);

        window.add(canvas);
    }

    public void start() {
        gameLoop.scheduleAtFixedRate(new GameLoop(seagull, controls), 0, (int) (1000.0 / GameLoop.TICKS_PER_SECOND));

        renderLoop.start();
    }

    public void stop() throws InterruptedException {
        renderLoop.halt();
        gameLoop.cancel();
        renderLoop.join();
    }
}