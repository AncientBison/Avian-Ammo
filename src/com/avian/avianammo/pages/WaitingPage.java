package avianammo.pages;

import avianammo.GameRole;
import avianammo.ImageHelpers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static avianammo.ImageHelpers.toCompatibleImage;

public class WaitingPage extends AbstractPage {
    public WaitingPage() {
        super(null);
    }

    @Override
    protected void drawComponents() {
        JLabel label = new JLabel("Waiting for Opponent..");

        add(label, BorderLayout.CENTER);
    }
}
