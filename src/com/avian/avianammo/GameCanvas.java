package avianammo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static avianammo.ImageHelpers.toCompatibleImage;

public class GameCanvas extends JPanel {
    private final transient List<Entity> entities;
    private final BufferedImage background;

    public GameCanvas(List<Entity> entities) throws IOException {
        setMinimumSize(new Dimension(500, 500));
        this.entities = entities;

        background = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/game-background.png")));
    }

    @Override
    public void paint(Graphics g) {
        if (entities == null) {
            return;
        }
        
        Graphics2D graphics = (Graphics2D) g;
        graphics.setBackground(Color.decode("#87CEEB"));
        graphics.clearRect(0, 0, getParent().getWidth(), getParent().getHeight());
        graphics.drawImage(background, 0, 0, null);

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Entity entity : entities) {
            entity.render(graphics);

            if (entity instanceof Seagull) {
                ((Seagull)entity).renderPoops(graphics);
            }
        }
        Toolkit.getDefaultToolkit().sync();
    }
}