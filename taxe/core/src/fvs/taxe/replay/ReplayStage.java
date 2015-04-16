package fvs.taxe.replay;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;

public class ReplayStage extends Stage {

    private final List<ClickEvent> clickEvents = new ArrayList<ClickEvent>();
    private boolean replaying = false;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!replaying)
            clickEvents.add(new ClickEvent(screenX, screenY, pointer, button));

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }

    public void replay() {
        // Don't process the last event as that will be the use clicking the
        // replay button.
        replaying = true;
        for (ClickEvent c : clickEvents) {
            touchDown(c.screenX, c.screenY, c.pointer, c.button);
            touchUp(c.screenX, c.screenY, c.pointer, c.button);
            System.out.println(c);
        }
        replaying = false;
    }
}
