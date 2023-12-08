package avianammo;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Seagull extends Entity {
    private static final Position POOP_OFFSET = new Position(32, 48);
    private static final double TIME_BETWEEN_POOPS = 4;

    private List<Poop> poops = new ArrayList<>();
    private double timeUntilPoopAllowed = 0;

    private final SeagullRenderer renderer;

    private Seagull(Movement movement) throws IOException {
        super(movement);

        renderer = new SeagullRenderer(movement);
    }

    public static Seagull createPhysicsSeagull(Position initialPosition) throws IOException {
        return new Seagull(new PhysicsMovement(initialPosition,
                new PhysicsMovement.SpeedLimits(
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_HORIZONTAL,
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_VERTICAL_UPWARDS,
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_VERTICAL_DOWNWARDS),
                PhysicsConstants.SEAGULL_MASS));
    }

    public static Seagull createRemoteSeagull(RemoteMovement movement) throws IOException {
        return new Seagull(movement);
    }

    private void updateTimeUntilNextPoopAllowed(double deltaTime) {
        if (timeUntilPoopAllowed > 0) {
            timeUntilPoopAllowed = Math.max(0, timeUntilPoopAllowed - deltaTime);
        }
    }

    public void flapAnimation() {
        renderer.flapAnimation();
    }

    public void tick(double deltaTime) {
        movement.tick(deltaTime);
        List<Poop> poopsToRemove = new ArrayList<>();
        for (Poop poop : poops) {
            poop.tick(deltaTime);
            if (poop.shouldBeRemoved()) {
                poopsToRemove.add(poop);
            }
        }

        for (Poop poop : poopsToRemove) {
            poops.remove(poop);
        }

        renderer.tick(deltaTime);
        updateTimeUntilNextPoopAllowed(deltaTime);
    }

    public void render(Graphics2D graphics) {
        renderer.render(graphics);
        renderPoops(graphics);
    }

    public void renderPoops(Graphics2D graphics) {
        for (Poop poop : poops) {
            poop.render(graphics);
        }
    }

    public boolean canPoop() {
        return timeUntilPoopAllowed == 0;
    }

    public void createPoop() throws IOException {
        poops.add(Poop.createPhysicsPoop(
            new Position(movement.getPosition().x() + POOP_OFFSET.x(), movement.getPosition().y() + POOP_OFFSET.y())));
        timeUntilPoopAllowed = TIME_BETWEEN_POOPS;
    }

    public Movement getMovement() {
        return movement;
    }

    public List<Poop> getPoops() {
        return poops;
    }

    public void setPoops(List<Poop> poops) {
        this.poops = poops;
    }

    public Position getPosition() {
        return movement.getPosition();
    }

    public boolean isFlapping() {
        return renderer.isFlapping();
    }

    public void stopFlap() {
        renderer.stopFlap();
    }

    public Direction getAnimationDirection() {
        return renderer.getAnimationDirection();
    }
}