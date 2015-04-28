package fvs.taxe.clickListener;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.Tooltip;
import fvs.taxe.actor.StationActor;
import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DialogGoal;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.goal.Goal;
import gameLogic.map.Station;
import gameLogic.player.Player;

//Responsible for checking whether the goal is clicked
public class GoalClickListener extends ClickListener {
    private final Context context;
    private final Goal goal;
    private Tooltip tooltip1;
    private Tooltip tooltip2;
    private Tooltip tooltip3;

    private boolean showingTooltips;

    public GoalClickListener(Context context, Goal goal) {
        this.goal = goal;
        this.context = context;
        this.showingTooltips = false;

        tooltip1 = new Tooltip(context.getSkin());
        tooltip2 = new Tooltip(context.getSkin());
        tooltip3 = new Tooltip(context.getSkin());
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        hideToolTips();
        showingTooltips = false;

        if (Game.getInstance().getState() == GameState.NORMAL) {
            Player currentPlayer = Game.getInstance().getPlayerManager().getActivePlayer();
            DialogGoalButtonClicked listener = new DialogGoalButtonClicked(currentPlayer,
                    goal);
            DialogGoal dialog = new DialogGoal(goal, context.getSkin());
            dialog.show(context.getStage());
            dialog.subscribeClick(listener);
        }
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (!showingTooltips) {
            //Need to check whether tooltips are currently being shown as otherwise it redraws them instantly after the clicked routine has ended
            Station origin = goal.getOrigin();
            StationActor originActor = origin.getActor();

            tooltip1.setPosition(originActor.getX() + 20, originActor.getY() + 20);
            tooltip1.show(origin.getName());
            context.getStage().addActor(tooltip1);

            Station destination = goal.getDestination();
            StationActor destinationActor = destination.getActor();
            context.getStage().addActor(tooltip2);
            tooltip2.setPosition(destinationActor.getX() + 20, destinationActor.getY() + 20);
            tooltip2.show(destination.getName());

            Station intermediary = goal.getIntermediary();
            if (!intermediary.getName().equals(origin.getName())) {
                StationActor intermediaryActor = intermediary.getActor();
                context.getStage().addActor(tooltip3);
                tooltip3.setPosition(intermediaryActor.getX() + 20, intermediaryActor.getY() + 20);
                tooltip3.show(intermediary.getName());
            }

            showingTooltips = true;
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        hideToolTips();
        showingTooltips = false;
    }

    private void hideToolTips() {
        //A check was necessary as to whether tooltips were currently being shown
        //This is due to the odd way that the events work
        //When clicking on a goal, it simultaneously performs the enter and exit methods
        //This led to some unintended behaviour where the tooltips were permanently rendered
        //Therefore they are only hidden if they are being shown
        if (showingTooltips) {
            tooltip1.hide();
            tooltip2.hide();

            if (tooltip3 != null)
                tooltip3.hide();
        }
    }
}
