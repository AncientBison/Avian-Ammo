package avianammo;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Controls {
    private static int ATTACK_KEY = KeyEvent.VK_SPACE;
    private static int FLY_LEFT_KEY = KeyEvent.VK_LEFT;
    private static int FLY_RIGHT_KEY = KeyEvent.VK_RIGHT;
    private static int FALP_KEY = KeyEvent.VK_UP;
    
    Map<Integer, KeyState> keys = new HashMap<>();

    public Controls() {
        keys.put(ATTACK_KEY, new KeyState());
        keys.put(FLY_LEFT_KEY, new KeyState());
        keys.put(FLY_RIGHT_KEY, new KeyState());
        keys.put(FALP_KEY, new KeyState());
    }

    public void handleKeyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!keys.containsKey(keyCode)) {
            return;
        }

        if (e.getID() == KeyEvent.KEY_PRESSED) {
            keys.get(keyCode).setStatePressing();
        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
            keys.get(keyCode).setStateReleased();
        }
    }

    public KeyState getLeft() {
        return keys.get(FLY_LEFT_KEY);
    }

    public KeyState getRight() {
        return keys.get(FLY_RIGHT_KEY);
    }

    public KeyState getUp() {
        return keys.get(FALP_KEY);
    }

    public KeyState getPoop() {
        return keys.get(ATTACK_KEY);
    }
}
