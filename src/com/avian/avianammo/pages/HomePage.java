package avianammo.pages;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import avianammo.GameRole;
import avianammo.ImageHelpers;
import avianammo.PhysicsConstants;

public class HomePage extends AbstractPage {

    private boolean waitingForAction = true;
    private GameRole role = GameRole.NONE;

    private String ip;
    private int port;

    public HomePage() throws IOException {
        super(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/home-background.png"))));
    }

    @Override
    protected void drawComponents() throws IOException {
        ImageIcon joinIcon = new ImageIcon(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/join-button.png"))).getScaledInstance(100, 100, Image.SCALE_FAST));
        ImageIcon hostIcon = new ImageIcon(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/host-button.png"))).getScaledInstance(100, 100, Image.SCALE_FAST));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 0));

        Insets emptyInsets = new Insets(0, 0, 0, 0);
        Dimension buttonSize = new Dimension(100, 40);
        Border emptyBorder = BorderFactory.createEmptyBorder();

        JButton joinButton = new JButton(joinIcon);
        joinButton.setMargin(emptyInsets);
        joinButton.setPreferredSize(buttonSize);
        joinButton.setBorder(emptyBorder);
        joinButton.addActionListener(e -> {
            parseAddress(JOptionPane.showInputDialog(this,"Enter host's address"));
            waitingForAction = false;
            role = GameRole.CLIENT;
        });
        buttonPanel.add(joinButton);

        JButton hostButton = new JButton(hostIcon);
        hostButton.setMargin(emptyInsets);
        hostButton.setPreferredSize(buttonSize);
        hostButton.setBorder(emptyBorder);
        hostButton.addActionListener(e -> {
            port = Integer.parseInt(JOptionPane.showInputDialog(this,"Enter port to listen on"));
            waitingForAction = false;
            role = GameRole.HOST;
        });
        buttonPanel.add(hostButton);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(PhysicsConstants.MAX_Y / 2, 0, 0, 0));
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        add(buttonPanel);
    }

    public GameRole awaitGameRoleChoice() throws InterruptedException {
        while (waitingForAction) {
            Thread.sleep(50);
        }

        return role;
    }

    public void parseAddress(String address) {
        boolean foundColon = false;
        StringBuilder ipInput = new StringBuilder();

        for (int i = 0; i < address.length(); i++) {
            if (address.charAt(i) == ':') {
                foundColon = true;
                continue;
            }

            if (!foundColon) {
                ipInput.append(address.charAt(i));
            } else {
                port = Integer.parseInt(address.substring(i));
                break;
            }
        }

        ip = ipInput.toString();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}