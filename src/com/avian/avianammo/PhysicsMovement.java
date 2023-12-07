package avianammo;

public class PhysicsMovement implements Movement {
    private static final double GRAVITY = 4;
    private static final double PIXELS_PER_METER = 5;

    private final SpeedLimits speedLimits;
    private final double mass;

    private Position position;

    private double velocityHorizontal;
    private double velocityVertical;

    private double forceHorizontal;
    private double forceVertical;

    public record SpeedLimits(double maxHorizontalSpeed, double maxVerticalSpeedUp, double maxVerticalSpeedDown) {};

    PhysicsMovement(Position initialPosition, SpeedLimits speedLimits, double mass) {
        this.position = initialPosition;
        this.speedLimits = speedLimits;
        this.mass = mass;
    }

    public void tick(double deltaTime) {
        position = new Position(calculateNewX(deltaTime), calculateNewY(deltaTime));
    }

    private double calculateNewX(double deltaTime) {
        double accelerationHorizontal = (forceHorizontal / mass);
        forceHorizontal = 0;  // Force has been applied
        velocityHorizontal += accelerationHorizontal * deltaTime;
        velocityHorizontal = speedLimits.maxHorizontalSpeed() * Math.signum(velocityHorizontal) * (1 - Math.pow(Math.E, -Math.abs(velocityHorizontal)));
        return Math.clamp(position.x() + velocityHorizontal * PIXELS_PER_METER, 0, PhysicsConstants.MAX_X);
    }
    
    private double calculateNewY(double deltaTime) {
        double accelerationVertical = (forceVertical / mass) + GRAVITY;
        forceVertical = 0;  // Force has been applied
        velocityVertical += accelerationVertical * deltaTime;
        double speedLimit = (velocityVertical > 0 ? speedLimits.maxVerticalSpeedUp() : speedLimits.maxVerticalSpeedDown());
        velocityVertical = speedLimit * Math.signum(velocityVertical) * (1 - Math.pow(Math.E, -Math.abs(velocityVertical)));
        return Math.clamp(position.y() + velocityVertical * PIXELS_PER_METER, 0, PhysicsConstants.MAX_Y);
    }

    public void addHorizontalForce(double force) {
        forceHorizontal += force;
    }

    public void addVerticalForce(double force) {
        forceVertical += force;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return forceHorizontal > 0 ? Direction.RIGHT : forceHorizontal < 0 ? Direction.LEFT : Direction.CENTER;
    }
}
