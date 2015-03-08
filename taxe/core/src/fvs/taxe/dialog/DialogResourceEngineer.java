package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import gameLogic.resource.Engineer;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceEngineer extends Dialog {
    private final List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();

    public DialogResourceEngineer(Engineer engineer, Skin skin) {
        super(engineer.toString(), skin);
        //Generates all the buttons that allow the user to interact with the dialog
        text("What do you want to do with this engineer?");
        button("Repair a blocked connection", "PLACE");
        button("Destroy a track", "REM_TRACK");
        button("Add a track", "ADD_TRACK");
        button("Drop", "DROP");
        button("Cancel", "CLOSE");
    }

    @Override
    public Dialog show(Stage stage) {
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
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
        if (obj == "CLOSE")
            this.remove();
        else if (obj == "DROP")
            clicked(Button.ENGINEER_DROP);
        else if (obj == "PLACE")
            clicked(Button.ENGINEER_REPAIR_TRACK);
        else if (obj == "REM_TRACK")
            clicked(Button.ENGINEER_REMOVE_TRACK);
        else if (obj == "ADD_TRACK")
            clicked(Button.ENGINEER_ADD_TRACK);
    }
}
