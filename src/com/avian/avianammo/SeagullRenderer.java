package avianammo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SeagullRenderer extends Renderer {

    private static final double SEAGULL_FLAP_TIME = 0.1;

    private BufferedImage seagullPreflapLeft;
    private BufferedImage seagullPreflapRight;
    
    private BufferedImage seagullPostflapLeft;
    private BufferedImage seagullPostflapRight;

    private Movement movement;
    private BufferedImage currentImage;
    private double flapDuration = -1;
    private Direction lastDirection = Direction.RIGHT;

    public SeagullRenderer(Movement movement) throws IOException {
        this.movement = movement;

        seagullPreflapLeft = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_left.png")));

        seagullPreflapRight = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_right.png")));

        seagullPostflapLeft = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_flap_left.png")));

        seagullPostflapRight = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_flap_right.png")));
    
        currentImage = seagullPreflapRight;
    }

    public void render(Graphics2D graphics) {
        Position position = movement.getPosition();
        updateCurrentAnimation();
        graphics.drawImage(currentImage, (int) position.x(), (int) position.y(), null);
    }

    protected void updateCurrentAnimation() {
        boolean flapping = flapDuration >= 0;
        if (movement.getDirection() == Direction.RIGHT) {
            currentImage = flapping ? seagullPostflapRight : seagullPreflapRight;
            lastDirection = Direction.RIGHT;
        } else if (movement.getDirection() == Direction.LEFT) {
            currentImage = flapping ? seagullPostflapLeft : seagullPreflapLeft;
            lastDirection = Direction.LEFT;
        // Seagull is not moving left or right
        } else if (lastDirection == Direction.RIGHT) {
            currentImage = flapping ? seagullPostflapRight : seagullPreflapRight;
        } else {
            currentImage = flapping ? seagullPostflapLeft : seagullPreflapLeft;
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
}
