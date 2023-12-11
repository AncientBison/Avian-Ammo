package avianammo;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import avianammo.networking.GameSocket;

public class GameLoop extends TimerTask  {
    public static final int TICKS_PER_SECOND = 60;
    private final Controls controls;
    
    private final Seagull seagull;
    private final Seagull opponentSeagull;
    private final GameSocket socket;

    public GameLoop(Seagull seagull, Seagull opponentSeagull, Controls controls, GameSocket socket) {
        this.seagull = seagull;
        this.opponentSeagull = opponentSeagull;
        this.controls = controls;
        this.socket = socket;
    }

    @Override
    public void run() {
        seagull.tick(1.0 / GameLoop.TICKS_PER_SECOND);
        if (socket.getLatestInformationData() != null) {
            if (opponentSeagull.getMovement() instanceof RemoteMovement movement) {
                updateOpponentSeagull(movement);
            }
            updateOpponentDroppings();
            updateSeagullHealth();
        }

        if (seagull.getMovement() instanceof PhysicsMovement movement) {
            updateSeagullPosition(movement);
        }

        updateSeagullDroppings();

        try {
            socket.sendInformation(seagull, opponentSeagull.getHealth());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSeagullDroppings() {
        Dropping intersectingDropping = opponentSeagull.getFirstIntersectingDropping(seagull.getDroppings().values());
        if (intersectingDropping != null) {
            seagull.getDroppings().remove(intersectingDropping.getId());
            if (opponentSeagull.getHealth() - 1 == 0) {
                try {
                    socket.sendWin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            opponentSeagull.takeDamage((byte) 1);
        }
    }

    private void updateSeagullPosition(PhysicsMovement movement) {
        if (seagull.canMove()) {
            double forceToAdd = controls.getLeft().isKeyPressed() ? -PhysicsConstants.SEAGULL_SPEED : 0;
            forceToAdd += controls.getRight().isKeyPressed() ? PhysicsConstants.SEAGULL_SPEED : 0;
            movement.addHorizontalForce(forceToAdd);

            if (controls.getUp().isKeyDown()) {
                movement.addVerticalForce(-PhysicsConstants.SEAGULL_JUMP_FORCE);
                seagull.flapAnimation();
            }

            if (controls.getDropping().isKeyDown() && seagull.canDropDropping()) {
                try {
                    seagull.createDropping();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateOpponentSeagull(RemoteMovement movement) {
        double x = socket.getLatestInformationData().opponentX();
        double y = socket.getLatestInformationData().opponentY();

        movement.setPosition(new Position(x, y));

        boolean opponentFlapping = socket.getLatestInformationData().opponentFlapping();
        Direction animationDirection = socket.getLatestInformationData().opponentAnimationDirection();

        if (opponentFlapping) {
            opponentSeagull.flapAnimation();
        } else {
            opponentSeagull.stopFlap();
        }

        movement.setDirection(animationDirection);
    }

    private void updateOpponentDroppings() {
        Map<Integer, Dropping> networkDroppings = socket.getLatestInformationData().droppings();

        Map<Integer, Dropping> clientDroppings = opponentSeagull.getDroppings();
        Iterator<Map.Entry<Integer, Dropping>> clientDroppingIterator = clientDroppings.entrySet().iterator();
        while (clientDroppingIterator.hasNext()) {
            Dropping clientDropping = clientDroppingIterator.next().getValue();
            if (!networkDroppings.containsKey(clientDropping.getId())) {
                clientDroppingIterator.remove();
                continue;
            }

            Dropping networkDropping = networkDroppings.get(clientDropping.getId());
            RemoteMovement droppingMovement = (RemoteMovement) clientDropping.getMovement();
            droppingMovement.setPosition(networkDropping.getPosition());
        }

        for (Dropping networkDropping : networkDroppings.values()) {
            if (!clientDroppings.containsKey(networkDropping.getId())) {
                clientDroppings.put(networkDropping.getId(), networkDropping);
            }
        }
    }

    private void updateSeagullHealth() {
        seagull.setHealth(socket.getLatestInformationData().seagullHealth());
    }
}