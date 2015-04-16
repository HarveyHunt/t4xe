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
        replaying = true;
        // Don't replay the final replay click as we could end up in a loop.
        for (ClickEvent c : clickEvents.subList(0, clickEvents.size() - 1)) {
            // We can get away with reusing the same ClickEvent as we can assume
            // that a click up and down occur at the same location.
            touchDown(c.screenX, c.screenY, c.pointer, c.button);
            touchUp(c.screenX, c.screenY, c.pointer, c.button);
            System.out.println(c);
        }
        replaying = false;
    }
}
