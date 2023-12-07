package avianammo;

public interface Movement {
    Position getPosition();
    Direction getDirection();
    void tick(double deltaTime);
}
