package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import gameLogic.goal.Goal;

import java.util.ArrayList;
import java.util.List;

public class DialogGoal extends Dialog {
    private final List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();

    public DialogGoal(Goal goal, Skin skin) {
        super(goal.toString(), skin);
        text("What do you want to do with this goal?");
        button("Drop", "DROP");
        button("Cancel", "CLOSE");
    }

    @Override
    public Dialog show(Stage stage) {
        //Shows the dialog in the centre of the screen
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2),
                Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }


    @Override
    public void hide() {
        //Hides the dialog
        hide(null);
    }

    private void clicked() {
        //Informs all listeners that the dialog has been pressed, and which button has been pressed
        for (ResourceDialogClickListener listener : clickListeners)
            listener.clicked(Button.GOAL_DROP);
    }

    public void subscribeClick(ResourceDialogClickListener listener) {
        //Adds listeners to the dialog, which want to know which button the user pressed
        clickListeners.add(listener);
    }

    @Override
    protected void result(Object obj) {
        //Does things based on which button was pressed
        if (obj.equals("CLOSE"))
            this.remove();
        else if (obj.equals("DROP"))
            //Removes the goal if the drop button is pressed
            clicked();
    }
}
