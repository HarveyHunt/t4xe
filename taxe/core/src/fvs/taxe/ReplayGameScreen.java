package fvs.taxe;

import java.util.Timer;
import java.util.TimerTask;

public class ReplayGameScreen extends GameScreen {

    private final String filepath;

    public ReplayGameScreen(TaxeGame game, String filepath) {
        super(game);
        this.filepath = filepath;
    }

    public void startReplay() {
        this.stage.loadReplay(filepath);
        scheduleClickReplay();
    }

    private void scheduleClickReplay() {
        stage.replaySingleClick();

        if (!stage.hasMoreClicks())
            return;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scheduleClickReplay();
            }
        }, stage.getNextClickTimeStamp());
    }
}
