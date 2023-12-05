package avianammo;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Controls {
    Map<Integer, KeyState> keys = new HashMap<>();

    public Controls() {
        keys.put(KeyEvent.VK_LEFT, new KeyState());
        keys.put(KeyEvent.VK_RIGHT, new KeyState());
        keys.put(KeyEvent.VK_SHIFT, new KeyState());
        keys.put(KeyEvent.VK_SPACE, new KeyState());
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
        return keys.get(KeyEvent.VK_LEFT);
    }

    public KeyState getRight() {
        return keys.get(KeyEvent.VK_RIGHT);
    }

    public KeyState getUp() {
        return keys.get(KeyEvent.VK_SPACE);
    }

    public KeyState getPoop() {
        return keys.get(KeyEvent.VK_SHIFT);
    }
}
