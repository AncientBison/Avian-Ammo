package avianammo;

import java.awt.Graphics2D;

public abstract class Entity {

    protected Movement movement;

    Entity(Movement movement) {
        this.movement = movement;
    }

    public abstract void render(Graphics2D graphics);
}
