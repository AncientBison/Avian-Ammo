package avianammo;

import java.io.IOException;
import java.util.TimerTask;

import avianammo.networking.GameSocket;

public class GameLoop extends TimerTask  {
    public static final int TICKS_PER_SECOND = 60;
    private final Controls controls;
    
    private Seagull seagull;
    private Seagull opponentSeagull;
    private GameSocket socket;

    public GameLoop(Seagull seagull, Seagull opponentSeagull, Controls controls, GameSocket socket) {
        this.seagull = seagull;
        this.opponentSeagull = opponentSeagull;
        this.controls = controls;
        this.socket = socket;
    }

    @Override
    public void run() {
        seagull.tick(1.0 / GameLoop.TICKS_PER_SECOND);

        if (socket.getLatestInformationData() != null && opponentSeagull.getMovement() instanceof RemoteMovement) {
            RemoteMovement movement = (RemoteMovement)opponentSeagull.getMovement();

            double x = socket.getLatestInformationData().opponentX();
            double y = socket.getLatestInformationData().opponentY();

            movement.setPosition(new Position(x, y));
        }

        if (seagull.getMovement() instanceof PhysicsMovement) {
            PhysicsMovement movement = (PhysicsMovement)seagull.getMovement();

            double forceToAdd = controls.getLeft().isKeyPressed() ? -PhysicsConstants.SEAGULL_SPEED : 0;
            forceToAdd += controls.getRight().isKeyPressed() ? PhysicsConstants.SEAGULL_SPEED : 0;
            movement.addHorizontalForce(forceToAdd);

            if (controls.getUp().isKeyDown()) {
                movement.addVerticalForce(-PhysicsConstants.SEAGULL_JUMP_FORCE);
                seagull.flapAnimation();
            }

            if (controls.getPoop().isKeyDown() && seagull.canPoop()) {
                try {
                    seagull.createPoop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            socket.sendSeagullInformation(seagull);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}