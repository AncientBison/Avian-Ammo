package avianammo.pages;

import avianammo.Controls;
import avianammo.ImageHelpers;
import avianammo.PhysicsConstants;
import avianammo.networking.GameSocket.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameResultsPage extends AbstractPage {

    private GameState gameState;
    private transient Controls controls = new Controls();
    
    public GameResultsPage(GameState gameState) throws IOException {
        super(ImageHelpers.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/home-background.png"))), false);
        this.gameState = gameState;
        drawComponents();
    }

    @Override
    protected void drawComponents() {
        JLabel label = new JLabel(switch(gameState) {
            case WAITING_AFTER_LOSS -> "You Lost! D:";
            case WAITING_AFTER_TIE -> "You tied! :( That's very unlikely";
            default -> "YOU WON!!! :D";
        });

        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
        label.setBorder(BorderFactory.createEmptyBorder(PhysicsConstants.MAX_Y / 2, 0,0, 0));

        add(label, BorderLayout.CENTER);

        JLabel continueLabel = new JLabel("Press <Space> to play again");
        continueLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        continueLabel.setBorder(BorderFactory.createEmptyBorder(30, 0,0, 0));

        add(continueLabel, BorderLayout.CENTER);
    }
}
