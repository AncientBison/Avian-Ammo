package avianammo;

import java.awt.Component;

public class RenderLoop<T extends Component> extends Thread  {    
    private T component;
    private boolean running;
    public static final int FRAME_RATE = 60;

    public RenderLoop(T component) {
        this.component = component;
        this.running = true;
    }

    @Override
    public void run() {
        while (true) {
            long timeAtStart = System.currentTimeMillis();
            component.repaint();
            if (!running) {
                break;
            }
            long nextRender = timeAtStart + 1000/FRAME_RATE;
            long timeUntilNextRender = nextRender - System.currentTimeMillis();
            if (timeUntilNextRender > 0) {
                try {
                    Thread.sleep(timeUntilNextRender);
                } catch (InterruptedException unusedEx) { //NOSONAR
                    // Intentionally ignore
                }
            }
        }
    }

    public void halt() {
        running = false;
    }
}