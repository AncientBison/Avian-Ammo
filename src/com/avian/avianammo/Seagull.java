package avianammo;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Seagull extends Entity {
    private static final Position POOP_OFFSET = new Position(0, 16);
    private static final double TIME_BETWEEN_POOPS = 4;

    public static final double POOP_COLLISION_RADIUS_LEFT = 20;
    public static final double POOP_COLLISION_RADIUS_RIGHT = 20;
    public static final double POOP_COLLISION_RADIUS_UP = 10;
    public static final double POOP_COLLISION_RADIUS_DOWN = 10;

    public static final Position DEFAULT_POSITION = new Position(50, 350);
    public static final Direction DEFAULT_DIRECTION = Direction.RIGHT;
    public static final Position OPPONENT_DEFAULT_POSITION = new Position(PhysicsConstants.MAX_X - 50, 350);
    public static final Direction OPPONENT_DEFAULT_DIRECTION = Direction.LEFT;

    public static final double HEAT_START_HEIGHT = 275;
    public static final double HEAT_MAX_TIME = 5;
    public static final double HEAT_DISABLE_TIME = 4;

    public static final int MAX_HEALTH = 3;

    private Map<Integer, Poop> poops = new HashMap<>();
    private double timeUntilPoopAllowed = 0;

    private double timeInHeat = 0;

    private int health = MAX_HEALTH;

    private final SeagullRenderer renderer;

    private boolean sendHitOpponentMessage = false;

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
        for (Poop poop : poops.values()) {
            poop.tick(deltaTime);
            if (poop.shouldBeRemoved()) {
                poopsToRemove.add(poop);
            }
        }

        for (Poop poop : poopsToRemove) {
            poops.remove(poop.getId());
        }

        renderer.tick(deltaTime);
        updateTimeUntilNextPoopAllowed(deltaTime);

        updateTimeInHeat(deltaTime);
    }

    public void render(Graphics2D graphics) {
        renderer.render(graphics);
        renderPoops(graphics);
    }

    public void renderPoops(Graphics2D graphics) {
        for (Poop poop : poops.values()) {
            poop.render(graphics);
        }
    }

    public boolean canPoop() {
        return timeUntilPoopAllowed == 0;
    }

    public void createPoop() throws IOException {
        Poop poop = Poop.createPhysicsPoop(
            new Position(movement.getPosition().x() + POOP_OFFSET.x(), movement.getPosition().y() + POOP_OFFSET.y()));
        poops.put(poop.getId(), poop);
        timeUntilPoopAllowed = TIME_BETWEEN_POOPS;
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

    public Map<Integer, Poop> getPoops() {
        return poops;
    }

    public void setPoops(Map<Integer, Poop> poops) {
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

    public void takeDamage(int damage) {
        this.health = Math.max(health - damage, 0);
    }

    public int getHealth() {
        return health;
    }

    public Direction getAnimationDirection() {
        return renderer.getAnimationDirection();
    }

    public Poop getFirstIntersectingPoop(Collection<Poop> poops) {
        
        for (Poop poop : poops) {

            if (getPosition().x() > poop.getPosition().x() - POOP_COLLISION_RADIUS_LEFT && 
                getPosition().x() < poop.getPosition().x() + POOP_COLLISION_RADIUS_RIGHT &&
                getPosition().y() > poop.getPosition().y() - POOP_COLLISION_RADIUS_UP &&
                getPosition().y() < poop.getPosition().y() + POOP_COLLISION_RADIUS_DOWN
            ) {
                return poop;
            }
        }

        return null;
    }

    public boolean shouldSendHitOpponentMessage() {
        return sendHitOpponentMessage;
    }

    public void setShouldSendHitOpponentMessage(boolean shouldSend) {
        this.sendHitOpponentMessage = shouldSend;
    }
}