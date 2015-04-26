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

    /**
     * We want to replay click events one at a time, with a time interval
     * between them. Due to the design of this game, sitting in a tight loop
     * that iterates over each ClickEvent and then sleeps means that a lot of
     * systems within the game never get run.
     *
     * As a result, we set a timer that interrupts the game and initiates the
     * mouse click.
     */
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
