package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import gameLogic.resource.Obstacle;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceObstacle extends Dialog {
    private final List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();

    public DialogResourceObstacle(Obstacle obstacle, Skin skin) {
        super(obstacle.toString(), skin);

        text("What do you want to do with this obstacle?");
        button("Place on a connection", "PLACE");
        button("Drop", "DROP");
        button("Cancel", "CLOSE");
    }

    @Override
    public Dialog show(Stage stage) {
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2),
                Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }

    @Override
    public void hide() {
        hide(null);
    }

    private void clicked(Button button) {
        for (ResourceDialogClickListener listener : clickListeners)
            listener.clicked(button);
    }

    public void subscribeClick(ResourceDialogClickListener listener) {
        clickListeners.add(listener);
    }

    @Override
    protected void result(Object obj) {
        if (obj.equals("CLOSE"))
            this.remove();
        else if (obj.equals("DROP"))
            clicked(Button.OBSTACLE_DROP);
        else if (obj.equals("PLACE"))
            clicked(Button.OBSTACLE_USE);
    }
}
