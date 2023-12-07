package avianammo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class PoopRenderer extends Renderer {

    private Random random = new Random();

    private BufferedImage currentImage;
    private Movement movement;

    public void render(Graphics2D graphics) {
        Position position = movement.getPosition();
        updateCurrentAnimation();
        graphics.drawImage(currentImage, (int) position.x(), (int) position.y(), null);
    }

    public PoopRenderer(Movement movement) throws IOException {
        this.movement = movement;
        currentImage = toCompatibleImage(getRandomPoopImage());
    }

    private BufferedImage getRandomPoopImage() throws IOException {
        int choice = random.nextInt(3); 
        return toCompatibleImage(ImageIO.read(switch (choice) {
            case 0 -> new File("src/com/avian/avianammo/res/images/poop.png");
            case 1 -> new File("src/com/avian/avianammo/res/images/poop2.png");
            case 2 -> new File("src/com/avian/avianammo/res/images/poop3.png");
            default -> null;
        }));
    }

    protected void updateCurrentAnimation() {
        // Image does not update
    }
    
}
