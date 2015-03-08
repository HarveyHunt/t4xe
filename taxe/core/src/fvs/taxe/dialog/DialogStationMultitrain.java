package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.clickListener.TrainClicked;
import fvs.taxe.controller.Context;
import gameLogic.resource.Train;

import java.util.ArrayList;

public class DialogStationMultitrain extends Dialog {
    private final Context context;
    public DialogStationMultitrain(ArrayList<Train> trains, Skin skin, Context context) {
        super("Select Train", skin);
        this.context = context;
        text("Choose which train you would like");

        for (Train train : trains) {
            String destination = "";
            if (train.getFinalDestination() != null)
                destination = " to " + train.getFinalDestination().getName();
            button(train.getName() + destination + " (player " + train.getPlayer().getPlayerNumber() + ")", train);
            getButtonTable().row();
        }
        button("Cancel", "CANCEL");
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

    @Override
    protected void result(Object obj) {
        if (obj == "CANCEL") {
            this.remove();
        } else {
            TrainClicked clicker = new TrainClicked(context, (Train) obj);
            //This is a small hack, by setting the value of the simulated x value to -1, we can use this to check whether or not
            //This dialog has been opened before. If this was not here then this dialog and trainClicked would get stuck in an endless loop!
            clicker.clicked(null, -1, 0);
        }
    }
}
