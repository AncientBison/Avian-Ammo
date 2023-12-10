package avianammo.pages;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import avianammo.GameRole;
import avianammo.ImageHelpers;
import avianammo.PhysicsConstants;

public class HomePage extends AbstractPage {

    private GameRole role = GameRole.NONE;
    private transient CountDownLatch gameRoleLatch;

    public HomePage(CountDownLatch gameRoleLatch) throws IOException {
        super(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/home-background.png"))));

        this.gameRoleLatch = gameRoleLatch;
    }

    @Override
    protected void drawComponents() throws IOException {
        ImageIcon joinIcon = new ImageIcon(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/join-button.png"))));
        ImageIcon hostIcon = new ImageIcon(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/host-button.png"))));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 44, 0));

        Insets emptyInsets = new Insets(0, 0, 0, 0);
        Dimension buttonSize = new Dimension(128, 56);
        Border emptyBorder = BorderFactory.createEmptyBorder();

        JButton joinButton = new JButton(joinIcon);
        joinButton.setMargin(emptyInsets);
        joinButton.setPreferredSize(buttonSize);
        joinButton.setBorder(emptyBorder);
        joinButton.addActionListener(e -> {
            role = GameRole.CLIENT;
            gameRoleLatch.countDown();
        });
        buttonPanel.add(joinButton);

        JButton hostButton = new JButton(hostIcon);
        hostButton.setMargin(emptyInsets);
        hostButton.setPreferredSize(buttonSize);
        hostButton.setBorder(emptyBorder);
        hostButton.addActionListener(e -> {
            role = GameRole.HOST;
            gameRoleLatch.countDown();
        });
        buttonPanel.add(hostButton);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(PhysicsConstants.MAX_Y / 2, 0, 0, 0));
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        add(buttonPanel);
    }

    public GameRole awaitGameRoleChoice() {
        if (role == null) {
            throw new NullPointerException("Acessed role before selected");
        }

        return role;
    }
}