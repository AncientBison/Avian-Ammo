package avianammo.pages;

import java.util.TimerTask;

import avianammo.Controls;

public class GameResultsTimerTask extends TimerTask {

    private Controls controls;

    public GameResultsTimerTask(Controls controls) {
        this.controls = controls;
    }

    @Override
    public void run() {
        if (controls.getPoop().isKeyPressed()) {
            
        }
    }
}
