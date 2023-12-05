package avianammo;

public class Player {
    private Seagull seagull;

    public Player(Seagull seagull) {
        this.seagull = seagull;
    }

    public void tick(double deltaTime) {
        seagull.tick(deltaTime);
    }
} 