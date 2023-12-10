package avianammo.pages;

import avianammo.ImageTools;
import avianammo.PhysicsConstants;
import avianammo.networking.GameSocket;
import avianammo.networking.GameSocket.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameResultsPage extends AbstractPage {

    private final GameState gameState;
    private final transient GameSocket socket;
    private static final int RESULTS_VISIBLE_SECONDS = 10;

    private int resultsTimeLeft = RESULTS_VISIBLE_SECONDS;
    private JLabel restartLabel;

    public GameResultsPage(GameState gameState, GameSocket socket) throws IOException {
        super(ImageTools.toCompatibleImage(
                ImageIO.read(new File("src/com/avian/avianammo/res/images/home-background.png"))), false);
        this.gameState = gameState;
        this.socket = socket;
        drawComponents();
    }

    @Override
    protected void drawComponents() {
        JLabel label = new JLabel(switch (gameState) {
            case WAITING_AFTER_LOSS -> "You Lost! D:";
            case WAITING_AFTER_TIE -> "You tied! :( That's very unlikely";
            default -> "YOU WON!!! :D";
        });

        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
        label.setBorder(BorderFactory.createEmptyBorder(PhysicsConstants.MAX_Y / 2, 0, 0, 0));

        add(label, BorderLayout.CENTER);

        paintRestartLabel();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        countOneSecond();
                    }
                },
                1000L);
    }

    private void paintRestartLabel() {
        if (restartLabel != null) {
            remove(restartLabel);
        }

        restartLabel = new JLabel("Restarting in " + Math.max(resultsTimeLeft, 0) + " seconds");
        restartLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        restartLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        add(restartLabel, BorderLayout.CENTER);
    }

    private void countOneSecond() {
        resultsTimeLeft -= 1;

        paintRestartLabel();
        SwingUtilities.updateComponentTreeUI(this);

        if (resultsTimeLeft <= 0) {
            socket.setGameState(GameState.WAITING_FOR_CONNECTION);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            countOneSecond();
                        }
                    },
                    1000L);
        }
    }
}
