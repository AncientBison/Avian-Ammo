package avianammo;

import java.awt.image.BufferedImage;

public abstract class Renderer {
    protected BufferedImage currentImage;

    protected abstract void updateCurrentAnimation();
}
