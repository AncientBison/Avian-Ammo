package avianammo;

import java.awt.Graphics2D;
import java.io.IOException;

import avianammo.PhysicsMovement.SpeedLimits;

public class Dropping extends Entity {

    private final DroppingRenderer renderer;
    private final int id;

    private Dropping(Movement movement) throws IOException {
        super(movement);

        id = (int)System.currentTimeMillis();
        
        renderer = new DroppingRenderer(movement);
    }

    private Dropping(Movement movement, int id) throws IOException {
        super(movement);

        this.id = id;
        
        renderer = new DroppingRenderer(movement);
    }

    public static Dropping createPhysicsDropping(Position initialPosition) throws IOException {
        return new Dropping(new PhysicsMovement(initialPosition, new SpeedLimits(0, 2, 0), 0.4, PhysicsConstants.DROPPING_SIZE));
    }

    public static Dropping createRemoteDropping(RemoteMovement remoteMovement, int id) throws IOException {
        return new Dropping(remoteMovement, id);
    }

    public void tick(double deltaTime) {
        movement.tick(deltaTime);
    }

    public boolean shouldBeRemoved() {
        return getPosition().y() >= (PhysicsConstants.MAX_Y - PhysicsConstants.DROPPING_SIZE);
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
        if (!(obj instanceof Dropping)) {
            return false;
        }

        return this.id == ((Dropping)obj).id;
    }

    public int getId() {
        return id;
    }
}