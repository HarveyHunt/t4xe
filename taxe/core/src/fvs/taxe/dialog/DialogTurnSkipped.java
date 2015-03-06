package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import gameLogic.Game;

public class DialogTurnSkipped extends Dialog {

    public DialogTurnSkipped(Skin skin) {
        super("Miss a turn", skin);
        text("Due to circumstances outside our control \n Network Rail would like to apologise for you missing your turn.");
        button("OK", "EXIT");
        align(Align.center);
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

    @Override
    protected void result(Object obj) {
        Game.getInstance().getPlayerManager().turnOver(null);
        this.remove();
    }
}
