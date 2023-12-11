package avianammo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import static avianammo.ImageTools.toCompatibleImage;

public class DroppingRenderer extends Renderer {

    private final Random random = new Random();

    private final Movement movement;

    public void render(Graphics2D graphics) {
        Position position = movement.getPosition();
        updateCurrentAnimation();
        graphics.drawImage(currentImage, (int) position.x() - currentImage.getWidth()/2, (int) position.y() - currentImage.getHeight()/2, null);
    }

    public DroppingRenderer(Movement movement) throws IOException {
        this.movement = movement;
        currentImage = toCompatibleImage(getRandomDroppingImage());
    }

    private BufferedImage getRandomDroppingImage() throws IOException {
        int choice = random.nextInt(3); 
        return toCompatibleImage(ImageIO.read(switch (choice) {
            case 0 -> new File("src/com/avian/avianammo/res/images/dropping1.png");
            case 1 -> new File("src/com/avian/avianammo/res/images/dropping2.png");
            case 2 -> new File("src/com/avian/avianammo/res/images/dropping3.png");
            default -> null;
        }));
    }

    protected void updateCurrentAnimation() {
        // Image does not update
    }
}