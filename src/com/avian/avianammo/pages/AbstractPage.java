package avianammo.pages;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class AbstractPage extends JPanel {
    protected transient BufferedImage backgroundImage;

    protected AbstractPage(BufferedImage backgroundImage) throws IOException {
        this.backgroundImage = backgroundImage;
        drawComponents();
    }

        protected AbstractPage(BufferedImage backgroundImage, boolean drawComponents) throws IOException {
        this.backgroundImage = backgroundImage;
        if (drawComponents) {
            drawComponents();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            Graphics2D graphics = (Graphics2D) g;
            graphics.drawImage(backgroundImage, 0, 0, null);
        }
    }

    protected abstract void drawComponents() throws IOException;
}