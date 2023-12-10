package avianammo.pages;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class AbstractPage extends JPanel {
    protected transient BufferedImage background;

    protected AbstractPage(BufferedImage background) throws IOException {
        this.background = background;
        drawComponents();
    }

        protected AbstractPage(BufferedImage background, boolean drawComponents) throws IOException {
        this.background = background;
        if (drawComponents) {
            drawComponents();
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