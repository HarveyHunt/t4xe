package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.Button;
import fvs.taxe.clickListener.ResourceDialogClickListener;
import fvs.taxe.controller.Context;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceSkipped extends Dialog {
    private final List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();

    public DialogResourceSkipped(Context context) {
        super("Skip", context.getSkin());

        text("What do you want to do with this resource?");
        button("Use", "USE");
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

    public void subscribeClick(ResourceDialogClickListener listener) {
        clickListeners.add(listener);
    }

    private void clicked(Button button) {
        for (ResourceDialogClickListener listener : clickListeners)
            listener.clicked(button);
    }

    @Override
    protected void result(Object obj) {
        if (obj.equals("EXIT"))
            Gdx.app.exit();
        else if (obj.equals("DROP"))
            clicked(Button.SKIP_DROP);
        else if (obj.equals("USE"))
            clicked(Button.SKIP_RESOURCE);
    }
}
