package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceTrain extends Dialog {
    private final List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();

    public DialogResourceTrain(Train train, Skin skin, boolean trainPlaced) {
        super(train.toString(), skin);
        text("What do you want to do with this train?");

        if (!trainPlaced) {
            button("Place at a station", "PLACE");
        } else if (!train.isDeparted()) {
            button("Choose a route", "ROUTE");
        } else if (train.getRoute() != null) {
            button("Change route", "CHANGE_ROUTE");
            button("View Route", "VIEWROUTE");
        }

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
            clicked(Button.TRAIN_DROP);
        else if (obj.equals("PLACE"))
            clicked(Button.TRAIN_PLACE);
        else if (obj.equals("ROUTE"))
            clicked(Button.TRAIN_ROUTE);
        else if (obj.equals("VIEWROUTE"))
            clicked(Button.VIEW_ROUTE);
        else if (obj.equals("CHANGE_ROUTE"))
            clicked(Button.TRAIN_CHANGE_ROUTE);
    }
}
