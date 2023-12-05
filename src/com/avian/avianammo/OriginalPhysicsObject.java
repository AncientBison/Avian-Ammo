package avianammo;

public abstract class OriginalPhysicsObject {
    private static final double GRAVITY = 4.2;

    protected double x;
    protected double y;

    private double velocityHorizontal;
    private double velocityVertical;

    private double accelerationHorizontal;
    private double accelerationVertical;

    private double terminalVelocityHorizontal;
    private double terminalVelocityVertical;

    private double mass;

    OriginalPhysicsObject(double x, double y, double terminalVelocityHorizontal, double terminalVelocityVertical, double mass) {
        this.x = x;
        this.y = y;
        this.terminalVelocityHorizontal = terminalVelocityHorizontal;
        this.terminalVelocityVertical = terminalVelocityVertical;
        this.mass = mass;
    }
    
    public void tick(double deltaTime) {
        resetAcceleration();
        
        applyGravity(deltaTime);
        applyAcceleration();
        applyVelocity();
    }

    private void resetAcceleration() {
        accelerationVertical = 0;
        accelerationHorizontal = 0;
    }

    private void applyGravity(double deltaTime) {
        accelerationVertical += GRAVITY  * deltaTime;
    }

    private void applyAcceleration() {
        velocityHorizontal += accelerationHorizontal;
        velocityVertical += accelerationVertical;

        velocityHorizontal = Math.clamp(velocityHorizontal, terminalVelocityHorizontal * -1, terminalVelocityHorizontal);
        velocityVertical = Math.clamp(velocityVertical, terminalVelocityVertical * -1, terminalVelocityVertical);
    }

    private void applyVelocity() {
        x += velocityHorizontal;
        y += velocityVertical;
    }

    public void addHorizontalForce(double force, double deltaTime) {
        accelerationHorizontal += (force / mass) * deltaTime;
    }

    public void addVerticalForce(double force, double deltaTime) {
        accelerationVertical += (force / mass) * deltaTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
