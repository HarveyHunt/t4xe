package fvs.taxe.replay;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;

public class ReplayStage extends Stage {

    private final List<ClickEvent> clickEvents = new ArrayList<ClickEvent>();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        clickEvents.add(new ClickEvent(screenX, screenY,
                pointer, button));

        return super.touchDown(screenX, screenY, pointer, button);
    }

    public void replay() {
        // Don't process the last event as that will be the use clicking the
        // replay button.
        for (ClickEvent c : clickEvents.subList(0, clickEvents.size() - 1)) {
            // TODO: This could be neater and the sleep here is stupid.
            touchDown(c.screenX, c.screenY, c.pointer, c.button);
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
