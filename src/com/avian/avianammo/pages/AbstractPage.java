package avianammo.pages;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class AbstractPage extends JPanel {
    protected BufferedImage background;
    private boolean loaded = false;

    public AbstractPage(BufferedImage background) {
        this.background = background;
        loadComponentsInBackground();
    }

    private void loadComponentsInBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws IOException {
                drawComponents();
                return null;
            }

            @Override
            protected void done() {
                loaded = true;
                repaint();
            }
        };

        worker.execute();
    }

    public void awaitComponentsLoad() {
        while (!loaded) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            Graphics2D graphics = (Graphics2D) g;
            graphics.drawImage(background, 0, 0, null);
        }
    }

    protected abstract void drawComponents() throws IOException;
}