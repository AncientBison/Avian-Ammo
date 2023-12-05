package avianammo;

import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Timer;

import javax.swing.JFrame;

public class Window extends JFrame {

    private transient Timer gameLoop;
    private transient RenderLoop<Window> renderLoop;

    private transient Seagull seagull;

    private GameCanvas canvas;

    private transient Controls controls = new Controls();

    public Window() throws IOException {
        super("Avian Ammo");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                renderLoop.halt();
                gameLoop.cancel();
            }
        });
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        seagull = new Seagull(new Position(50, 50));
        canvas = new GameCanvas(seagull);
        add(canvas);
        canvas.setDoubleBuffered(true);

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(e -> {
            controls.handleKeyEvent(e);

            return false;
        });

        gameLoop = new Timer();
        gameLoop.scheduleAtFixedRate(new GameLoop(seagull, controls), 0, (int) (1000.0 / GameLoop.TICKS_PER_SECOND));

        renderLoop = new RenderLoop<>(this);
        renderLoop.start();
    }
}
