package avianammo;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Seagull extends Entity {
    private static final Position DROPPING_OFFSET = new Position(0, 16);
    private static final double TIME_BETWEEN_DROPPINGS = 4;

    public static final double DROPPING_COLLISION_RADIUS_LEFT = 20;
    public static final double DROPPING_COLLISION_RADIUS_RIGHT = 20;
    public static final double DROPPING_COLLISION_RADIUS_UP = 10;
    public static final double DROPPING_COLLISION_RADIUS_DOWN = 10;

    public static final Position DEFAULT_POSITION = new Position(50, 350);
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    public static final Position OPPONENT_DEFAULT_POSITION = new Position(
            PhysicsConstants.MAX_X - (50 + (PhysicsConstants.SEAGULL_SIZE / 2.0)), 350);
    public static final Direction OPPONENT_DEFAULT_DIRECTION = Direction.LEFT;

    public static final double HEAT_START_HEIGHT = 275;
    public static final double HEAT_MAX_TIME = 3.5;
    public static final double HEAT_DISABLE_TIME = 4;

    public static final byte MAX_HEALTH = 3;

    private final Map<Integer, Dropping> droppings = new HashMap<>();
    private double timeUntilDroppingAllowed = 0;

    private double timeInHeat = 0;

    private byte health = MAX_HEALTH;

    private final SeagullRenderer renderer;

    private double noControlTime = 0;

    private Seagull(Movement movement) throws IOException {
        super(movement);

        renderer = new SeagullRenderer(movement, this);
    }

    public static Seagull createPhysicsSeagull(Position initialPosition) throws IOException {
        return new Seagull(new PhysicsMovement(initialPosition,
                new PhysicsMovement.SpeedLimits(
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_HORIZONTAL,
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_VERTICAL_UPWARDS,
                        PhysicsConstants.SEAGULL_TERMINAL_VELOCITY_VERTICAL_DOWNWARDS),
                PhysicsConstants.SEAGULL_MASS, PhysicsConstants.SEAGULL_SIZE));
    }

    public static Seagull createRemoteSeagull(RemoteMovement movement) throws IOException {
        return new Seagull(movement);
    }

    private void updateTimeUntilNextDroppingAllowed(double deltaTime) {
        if (timeUntilDroppingAllowed > 0) {
            timeUntilDroppingAllowed = Math.max(0, timeUntilDroppingAllowed - deltaTime);
        }
    }

    public void flapAnimation() {
        renderer.flapAnimation();
    }

    public void tick(double deltaTime) {
        movement.tick(deltaTime);
        List<Dropping> droppingsToRemove = new ArrayList<>();
        for (Dropping dropping : droppings.values()) {
            dropping.tick(deltaTime);
            if (dropping.shouldBeRemoved()) {
                droppingsToRemove.add(dropping);
            }
        }

        for (Dropping dropping : droppingsToRemove) {
            droppings.remove(dropping.getId());
        }

        renderer.tick(deltaTime);
        updateTimeUntilNextDroppingAllowed(deltaTime);

        updateTimeInHeat(deltaTime);
    }

    public void render(Graphics2D graphics) {
        renderer.render(graphics);
        renderDroppings(graphics);
    }

    public void renderDroppings(Graphics2D graphics) {
        for (Dropping dropping : droppings.values()) {
            dropping.render(graphics);
        }
    }

    public boolean canDropDropping() {
        return timeUntilDroppingAllowed == 0;
    }

    public void createDropping() throws IOException {
        Dropping dropping = Dropping.createPhysicsDropping(
                new Position(movement.getPosition().x() + DROPPING_OFFSET.x(),
                        movement.getPosition().y() + DROPPING_OFFSET.y()));
        droppings.put(dropping.getId(), dropping);
        timeUntilDroppingAllowed = TIME_BETWEEN_DROPPINGS;
    }

    public void updateTimeInHeat(double deltaTime) {
        noControlTime = Math.max(noControlTime - deltaTime, 0);

        if (getPosition().y() <= HEAT_START_HEIGHT) {
            timeInHeat += deltaTime;
            if (timeInHeat >= HEAT_MAX_TIME) {
                noControlTime += HEAT_DISABLE_TIME;
                timeInHeat = 0;
            }
        } else {
            timeInHeat = 0;
        }
    }

    public boolean canMove() {
        return noControlTime == 0;
    }

    public Movement getMovement() {
        return movement;
    }

    public Map<Integer, Dropping> getDroppings() {
        return droppings;
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

    public void takeDamage(byte damage) {
        this.health = (byte) Math.max(health - damage, 0);
    }

    public void setHealth(byte health) {
        this.health = health;
    }

    public byte getHealth() {
        return health;
    }

    public Direction getAnimationDirection() {
        return renderer.getAnimationDirection();
    }

    public Dropping getFirstIntersectingDropping(Collection<Dropping> droppings) {

        for (Dropping dropping : droppings) {

            if (getPosition().x() > dropping.getPosition().x() - DROPPING_COLLISION_RADIUS_LEFT &&
                    getPosition().x() < dropping.getPosition().x() + DROPPING_COLLISION_RADIUS_RIGHT &&
                    getPosition().y() > dropping.getPosition().y() - DROPPING_COLLISION_RADIUS_UP &&
                    getPosition().y() < dropping.getPosition().y() + DROPPING_COLLISION_RADIUS_DOWN) {
                return dropping;
            }
        }

        return null;
    }

    public double getNoControlTime() {
        return noControlTime;
    }

    public double getTimeInHeat() {
        return timeInHeat;
    }
}