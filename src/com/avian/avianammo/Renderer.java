package avianammo;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public abstract class Renderer {
    protected BufferedImage currentImage;

    protected abstract void updateCurrentAnimation();
}
