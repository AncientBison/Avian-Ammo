package avianammo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import static avianammo.ImageTools.toCompatibleImage;

public class SeagullRenderer extends Renderer {

    private static final double SEAGULL_FLAP_TIME = 0.1;

    private final BufferedImage seagullPreflapLeft;
    private final BufferedImage seagullPreflapRight;
    
    private final BufferedImage seagullPostflapLeft;
    private final BufferedImage seagullPostflapRight;

    private final BufferedImage seagullSwimLeft;
    private final BufferedImage seagullSwimRight;

    private final BufferedImage heartRed;
    private final BufferedImage heartBlue;

    private final Movement movement;
    private double flapDuration = -1;
    private Direction lastDirection = Direction.RIGHT;
    private final Seagull seagull;

    public SeagullRenderer(Movement movement, Seagull seagull) throws IOException {
        this.movement = movement;
        this.seagull = seagull;

        seagullPreflapLeft = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_left.png")));

        seagullPreflapRight = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_right.png")));

        seagullPostflapLeft = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_flap_left.png")));

        seagullPostflapRight = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_flap_right.png")));

        seagullSwimLeft = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_swim_left.png")));

        seagullSwimRight = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_swim_right.png")));

        heartRed = ImageTools.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/heart.png")));
        heartBlue = ImageTools.toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/heart_blue.png")));

        currentImage = seagullPreflapRight;
    }

    public void render(Graphics2D graphics) {
        Position position = movement.getPosition();
        updateCurrentAnimation();
    
        BufferedImage heart = (movement instanceof PhysicsMovement) ? heartRed : heartBlue;

        for (int i = 0; i < seagull.getHealth(); i++) {
            graphics.drawImage(heart, ((int) position.x() - heart.getWidth() / 2) + ((i - 1) * 30), ((int) position.y() - heart.getHeight() / 2 ) - 40, null);
        }

        graphics.drawImage(currentImage, (int) position.x() - currentImage.getWidth()/2, (int) position.y() - currentImage.getHeight()/2, null);

        if (seagull.getMovement() instanceof PhysicsMovement) {
            updateScreenDarkness(graphics);
        }
    }

    private void updateScreenDarkness(Graphics2D graphics) {
        double darkness = seagull.getTimeInHeat() / Seagull.HEAT_MAX_TIME;

        if (seagull.getNoControlTime() > 0) {
            darkness = (seagull.getNoControlTime() / Seagull.HEAT_DISABLE_TIME);
        }

        darkness = Math.clamp(darkness, 0, 0.9);

        graphics.setColor(new Color(0, 0, 0, (int) (darkness * 255)));
        graphics.fillRect(0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);
    }

    @Override
    protected void updateCurrentAnimation() {
        boolean flapping = flapDuration >= 0;

        boolean swimming = movement.getPosition().y() >= PhysicsConstants.MAX_Y - PhysicsConstants.SEAGULL_SIZE;
        if (movement.getDirection() == Direction.RIGHT) {
            currentImage = swimming ? seagullSwimRight : (flapping ? seagullPostflapRight : seagullPreflapRight);
            lastDirection = Direction.RIGHT;
        } else if (movement.getDirection() == Direction.LEFT) {
            currentImage = swimming ? seagullSwimLeft : (flapping ? seagullPostflapLeft : seagullPreflapLeft);
            lastDirection = Direction.LEFT;
        // Seagull is not moving left or right
        } else if (lastDirection == Direction.RIGHT) {
            currentImage = swimming ? seagullSwimRight : (flapping ? seagullPostflapRight : seagullPreflapRight);
        } else {
            currentImage = swimming ? seagullSwimLeft : (flapping ? seagullPostflapLeft : seagullPreflapLeft);
        }
    }

    public void tick(double deltaTime) {
        updateFlapDuration(deltaTime);
    }

    public void flapAnimation() {
        flapDuration = 0;
    }

    private void updateFlapDuration(double deltaTime) {
        if (flapDuration > SEAGULL_FLAP_TIME) {
            flapDuration = -1;
            return;
        }
        
        if (flapDuration >= 0) {
            flapDuration += deltaTime;
        }
    }

    public boolean isFlapping() {
        return flapDuration >= 0;
    }

    public void stopFlap() {
        flapDuration = -1;
    }

    public Direction getAnimationDirection() {
        return lastDirection;
    }
}
