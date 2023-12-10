package avianammo.pages;

import avianammo.ImageHelpers;
import avianammo.PhysicsConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TimerPage extends AbstractPage {
    private int secondsLeft;
    private JLabel timeLabel;

    public TimerPage(int startTimeLeft) throws IOException {
        super(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/home-background.png"))), false);

        secondsLeft = startTimeLeft;

        drawComponents();
    }

    @Override
    protected void drawComponents() {
        if (timeLabel != null) {
            remove(timeLabel);
        }
        timeLabel = new JLabel("Starting in " + secondsLeft);

        timeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(PhysicsConstants.MAX_Y / 2, 0,0, 0));

        add(timeLabel, BorderLayout.CENTER);
    }

    public void countOneSecond() {
        secondsLeft = Math.max(secondsLeft - 1, 1);
        drawComponents();
    }
}
