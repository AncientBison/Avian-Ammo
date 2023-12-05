package avianammo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class GameCanvas extends JPanel {
    private transient Seagull seagull;

    public GameCanvas(Seagull seagull) {
        setMinimumSize(new Dimension(500, 500));
        this.seagull = seagull;
    }

    @Override
    public void paint(Graphics g) {
        if (seagull == null) {
            return;
        }
        
        Graphics2D graphics = (Graphics2D) g;
        graphics.setBackground(Color.decode("#87CEEB"));
        graphics.clearRect(0, 0, getParent().getWidth(), getParent().getHeight());

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        seagull.render(graphics);
        Toolkit.getDefaultToolkit().sync();
    }
}