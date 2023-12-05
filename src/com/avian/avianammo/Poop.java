package avianammo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Poop extends Entity {

    BufferedImage poopImage;

    Poop(Position initialPosition) throws IOException {
        super(initialPosition, new SpeedLimits(0, 0, 10), 0.4);

        poopImage = toCompatibleImage(ImageIO.read(new File("src/com/avian/avianammo/res/images/poop.png")));
    }
    
    protected void updateCurrentAnimation() {
        currentImage = poopImage;
    }
}