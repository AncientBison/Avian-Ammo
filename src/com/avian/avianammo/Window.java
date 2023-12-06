package avianammo;

import java.io.IOException;


import javax.swing.JFrame;

public class Window extends JFrame {

    public Window() throws IOException {
        super("Avian Ammo");

        this.setSize(500, 500);
        this.setLocationRelativeTo(null);

        Game game = new Game(this);
        game.start();

        this.setVisible(true); // Repaints with new elements
    }
}
