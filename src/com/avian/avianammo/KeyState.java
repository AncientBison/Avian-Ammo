package avianammo;

public class KeyState {
    private boolean pressed;
    private boolean wasPressed;

    public void setStatePressing() {
        pressed = true;
    }

    public void setStateReleased() {
        pressed = false;
        wasPressed = false;
    }

    public boolean isKeyDown() {
        if (pressed && !wasPressed) {
            wasPressed = true;
            return true;
        }

        return false;
    }

    public boolean isKeyPressed() {
        return pressed;
    }
}
