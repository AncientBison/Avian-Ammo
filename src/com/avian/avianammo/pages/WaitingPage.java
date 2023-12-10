package avianammo.pages;

import avianammo.ImageHelpers;
import avianammo.PhysicsConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WaitingPage extends AbstractPage {
    public WaitingPage() throws IOException {
        super(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/home-background.png"))));
    }

    @Override
    protected void drawComponents() {
        JLabel label = new JLabel("Waiting for Opponent..");

        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
        label.setBorder(BorderFactory.createEmptyBorder(PhysicsConstants.MAX_Y / 2, 0,0, 0));

        add(label, BorderLayout.CENTER);
    }
}
