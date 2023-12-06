package avianammo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Seagull extends Entity {
    private static final double SEAGULL_FLAP_TIME = 0.1;
    private static final Position POOP_OFFSET = new Position(32, 48);
     
    private BufferedImage seagullPreflapLeft;
    private BufferedImage seagullPreflapRight;
    
    private BufferedImage seagullPostflapLeft;
    private BufferedImage seagullPostflapRight;

    private Direction lastDirection = Direction.RIGHT;

    private List<Poop> poops = new ArrayList<>();

    private double flapDuration = -1;

    public Seagull(Position initialPosition) throws IOException {
        super(initialPosition,
                new PhysicsObject.SpeedLimits(
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_HORIZONTAL,
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_VERTICAL_UPWARDS,
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_VERTICAL_DOWNWARDS),
                PhysicsConstants.SEAGULL_MASS);

        seagullPreflapLeft = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_left.png")));

        seagullPreflapRight = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_right.png")));

        seagullPostflapLeft = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_flap_left.png")));

        seagullPostflapRight = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/seagull_flap_right.png")));

        currentImage = seagullPreflapRight;
    }

    protected void updateCurrentAnimation() {
        boolean flapping = flapDuration >= 0;
        if (getDirection() == Direction.RIGHT) {
            currentImage = flapping ? seagullPostflapRight : seagullPreflapRight;
            lastDirection = Direction.RIGHT;
        } else if (getDirection() == Direction.LEFT) {
            currentImage = flapping ? seagullPostflapLeft : seagullPreflapLeft;
            lastDirection = Direction.LEFT;
        // Seagull is not moving left or right
        } else if (lastDirection == Direction.RIGHT) {
            currentImage = flapping ? seagullPostflapRight : seagullPreflapRight;
        } else {
            currentImage = flapping ? seagullPostflapLeft : seagullPreflapLeft;
        }
    }

    protected void updateFlapDuration(double deltaTime) {
        if (flapDuration > SEAGULL_FLAP_TIME) {
            flapDuration = -1;
            return;
        }
        
        if (flapDuration >= 0) {
            flapDuration += deltaTime;
        }
    }

    public void flapAnimation() {
        flapDuration = 0;
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);
        for (Poop poop : poops) {
            poop.tick(deltaTime);
        }

        updateFlapDuration(deltaTime);
    }

    public void renderPoops(Graphics2D graphics) {
        for (Poop poop : poops) {
            poop.render(graphics);
        }
    }

    public void createPoop() throws IOException {
        poops.add(new Poop(
            new Position(getPosition().x() + POOP_OFFSET.x(), getPosition().y() + POOP_OFFSET.y())));
    }
}