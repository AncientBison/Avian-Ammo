package avianammo;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
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

            boolean opponentFlapping = socket.getLatestInformationData().opponentFlapping();
            Direction animationDirection = socket.getLatestInformationData().opponentAnimationDirection();

            if (opponentFlapping) {
                opponentSeagull.flapAnimation();
            } else {
                opponentSeagull.stopFlap();
            }

            movement.setPosition(new Position(x, y));

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

            movement.setDirection(animationDirection);
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

            Poop intersectingPoop = opponentSeagull.getFirstIntersectingPoop(seagull.getPoops().values());
            if (intersectingPoop != null) {
                seagull.getPoops().remove(intersectingPoop.getId());
                System.out.println("bonk");
            }
        }

        try {
            socket.sendSeagullInformation(seagull);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}