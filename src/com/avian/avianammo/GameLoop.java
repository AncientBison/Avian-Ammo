package avianammo;

import java.util.TimerTask;

public class GameLoop extends TimerTask  {
    public static final int TICKS_PER_SECOND = 60;
    private final Controls controls;
    
    private Seagull seagull;

    public GameLoop(Seagull seagull, Controls controls) {
        this.seagull = seagull;
        this.controls = controls;
    }

    @Override
    public void run() {
        seagull.tick(1.0 / GameLoop.TICKS_PER_SECOND);

        double forceToAdd = controls.getLeft().isKeyPressed() ? -PhysicsConstants.SEAGULL_SPEED : 0;
        forceToAdd += controls.getRight().isKeyPressed() ? PhysicsConstants.SEAGULL_SPEED : 0;
        seagull.addHorizontalForce(forceToAdd);

        if (controls.getUp().isKeyDown()) {
            seagull.addVerticalForce(-PhysicsConstants.SEAGULL_JUMP_FORCE);
            seagull.flapAnimation();
        }
    }
}