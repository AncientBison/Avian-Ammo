package avianammo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class GameCanvas extends JPanel {
    private transient List<Entity> entities = new ArrayList<>();

    public GameCanvas(List<Entity> entities) {
        this.entities = entities;
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