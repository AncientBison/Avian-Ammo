package avianammo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class AbstractPage extends JPanel {
    protected BufferedImage background;

    public AbstractPage(BufferedImage background) {
        this.background = background;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.drawImage(background, 0, 0, null);

        drawComponents();
    }

    protected abstract void drawComponents();
}
