package avianammo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Poop extends Entity {

    Poop(Position initialPosition) throws IOException {
        super(initialPosition, new SpeedLimits(0, 2, 0), 0.4);

        currentImage = toCompatibleImage(getRandomPoopImage());
    }

    protected BufferedImage getRandomPoopImage() throws IOException {
        Random random = new Random();
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