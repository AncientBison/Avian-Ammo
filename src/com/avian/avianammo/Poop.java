package avianammo;

import java.awt.Graphics2D;
import java.io.IOException;

import avianammo.PhysicsMovement.SpeedLimits;

public class Poop extends Entity {

    private final PoopRenderer renderer;

    private Poop(Movement movement) throws IOException {
        super(movement);
        
        renderer = new PoopRenderer(movement);
    }

    public static Poop createPhysicsPoop(Position initialPosition) throws IOException {
        return new Poop(new PhysicsMovement(initialPosition, new SpeedLimits(0, 2, 0), 0.4));
    }

    public static Poop createRemotePoop(RemoteMovement remoteMovement) throws IOException {
        return new Poop(remoteMovement);
    }

    public void tick(double deltaTime) {
        movement.tick(deltaTime);
    }

    public boolean shouldBeRemoved() {
        return getPosition().y() >= PhysicsConstants.MAX_Y;
    }

    public void render(Graphics2D graphics) {
        renderer.render(graphics);
    }

    public Position getPosition() {
        return movement.getPosition();
    }
}