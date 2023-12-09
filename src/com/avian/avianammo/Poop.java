package avianammo;

import java.awt.Graphics2D;
import java.io.IOException;

import avianammo.PhysicsMovement.SpeedLimits;

public class Poop extends Entity {

    private final PoopRenderer renderer;
    private final int id;

    private Poop(Movement movement) throws IOException {
        super(movement);

        id = (int)System.currentTimeMillis();
        
        renderer = new PoopRenderer(movement);
    }

    private Poop(Movement movement, int id) throws IOException {
        super(movement);

        this.id = id;
        
        renderer = new PoopRenderer(movement);
    }

    public static Poop createPhysicsPoop(Position initialPosition) throws IOException {
        return new Poop(new PhysicsMovement(initialPosition, new SpeedLimits(0, 2, 0), 0.4, PhysicsConstants.POOP_SIZE));
    }

    public static Poop createRemotePoop(RemoteMovement remoteMovement, int id) throws IOException {
        return new Poop(remoteMovement, id);
    }

    public void tick(double deltaTime) {
        movement.tick(deltaTime);
    }

    public boolean shouldBeRemoved() {
        return getPosition().y() >= (PhysicsConstants.MAX_Y - PhysicsConstants.POOP_SIZE);
    }

    public void render(Graphics2D graphics) {
        renderer.render(graphics);
    }

    public Position getPosition() {
        return movement.getPosition();
    }

    public Movement getMovement() {
        return movement;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Poop)) {
            return false;
        }

        return this.id == ((Poop)obj).id;
    }

    public int getId() {
        return id;
    }
}