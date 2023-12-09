package avianammo;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHelpers {
    public static BufferedImage toCompatibleImage(BufferedImage image) {
        // obtain the current system graphical settings
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfxConfig.getColorModel()))
            return image;

        // image is not optimized, so create a new image that is
        BufferedImage newImage = gfxConfig.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D graphics2d = (Graphics2D) newImage.getGraphics();

        // actually draw the image and dispose of context no longer needed
        graphics2d.drawImage(image, 0, 0, null);
        graphics2d.dispose();

        return newImage;
    }
}
