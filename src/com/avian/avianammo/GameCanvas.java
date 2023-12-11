package avianammo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static avianammo.ImageTools.toCompatibleImage;

public class GameCanvas extends JPanel {
    private final transient List<Entity> entities;
    private final transient BufferedImage backgroundImage;
  
    public GameCanvas(List<Entity> entities) throws IOException {

        this.entities = entities;

        backgroundImage = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/game-background.png")));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (entities == null) {
            return;
        }
        
        Graphics2D graphics = (Graphics2D) g;
        graphics.setBackground(Color.decode("#87CEEB"));
        graphics.clearRect(0, 0, getParent().getWidth(), getParent().getHeight());
        graphics.drawImage(backgroundImage, 0, 0, null);

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Entity entity : entities) {
            entity.render(graphics);

            if (entity instanceof Seagull) {
                ((Seagull)entity).renderDroppings(graphics);
            }
        }
        Toolkit.getDefaultToolkit().sync(); // Increases framerate
    }
}