package fvs.taxe.clickListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DialogResourceObstacle;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.player.Player;
import gameLogic.resource.Obstacle;

//Responsible for checking whether the Obstacle is clicked.
public class ObstacleClicked extends ClickListener {
    private final Obstacle obstacle;
    private final Context context;
    private boolean displayingMessage;

    public ObstacleClicked(Context context, Obstacle obstacle) {
        this.obstacle = obstacle;
        this.context = context;
        displayingMessage = false;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (Game.getInstance().getState() == GameState.NORMAL) {
            Player currentPlayer = Game.getInstance().getPlayerManager().getActivePlayer();

            DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, obstacle);
            DialogResourceObstacle dialog = new DialogResourceObstacle(obstacle, context.getSkin());
            dialog.show(context.getStage());
            dialog.subscribeClick(listener);
        }
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor trainActor) {
        if (!displayingMessage) {
            displayingMessage = true;
            if (Game.getInstance().getState() == GameState.NORMAL)
                context.getTopBarController().displayMessage("Place an obstacle on a connection on the map", Color.BLACK);
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor trainActor) {
        if (displayingMessage) {
            displayingMessage = false;
            if (Game.getInstance().getState() == GameState.NORMAL)
                context.getTopBarController().clearMessage();
        }
    }
}
