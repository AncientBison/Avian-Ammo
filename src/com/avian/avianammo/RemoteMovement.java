package avianammo;

public class RemoteMovement implements Movement {

    private Position position;
    private Direction direction;

    public RemoteMovement(Position initialPosition, Direction direction) {
        this.position = initialPosition;
        this.direction = direction;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void tick(double deltaTime) {
        // Nothing to do on tick
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
