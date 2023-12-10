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
        if (socket.getLatestInformationData() != null && opponentSeagull.getMovement() instanceof RemoteMovement movement) {
            updateOpponentSeagull(movement);
            updateOpponentPoops();
        }

        if (seagull.getMovement() instanceof PhysicsMovement movement) {
            updateSeagullPosition(movement);
            updateSeagullPoops();
        }

        try {
            socket.sendSeagullInformation(seagull);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSeagullPoops() {
        Poop intersectingPoop = opponentSeagull.getFirstIntersectingPoop(seagull.getPoops().values());
        if (intersectingPoop != null) {
            seagull.getPoops().remove(intersectingPoop.getId());
            if (opponentSeagull.getHealth() - 1 == 0) {
                try {
                    socket.sendWin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            seagull.setShouldSendHitOpponentMessage(true);
            opponentSeagull.takeDamage(1);
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

            if (controls.getPoop().isKeyDown() && seagull.canPoop()) {
                try {
                    seagull.createPoop();
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

        if (socket.getLatestInformationData().gotHit()) {
            seagull.takeDamage(1);
            socket.setGotHit(false);
        }

            movement.setDirection(animationDirection);
    }

    private void updateOpponentPoops() {
        Map<Integer, Poop> networkPoops = socket.getLatestInformationData().poops();

        Map<Integer, Poop> clientPoops = opponentSeagull.getPoops();
        Iterator<Map.Entry<Integer, Poop>> clientPoopIterator = clientPoops.entrySet().iterator();
        while (clientPoopIterator.hasNext()) {
            Poop clientPoop = clientPoopIterator.next().getValue();
            if (!networkPoops.containsKey(clientPoop.getId())) {
                clientPoopIterator.remove();
                continue;
            }

            Poop networkPoop = networkPoops.get(clientPoop.getId());
            RemoteMovement poopMovement = (RemoteMovement) clientPoop.getMovement();
            poopMovement.setPosition(networkPoop.getPosition());
        }

        for (Poop networkPoop : networkPoops.values()) {
            if (!clientPoops.containsKey(networkPoop.getId())) {
                clientPoops.put(networkPoop.getId(), networkPoop);
            }
        }
    }
}