package fvs.taxe.clickListener;

import fvs.taxe.Button;
import gameLogic.goal.Goal;
import gameLogic.player.Player;

public class DialogGoalButtonClicked implements ResourceDialogClickListener {
    private final Player currentPlayer;
    private final Goal goal;

    public DialogGoalButtonClicked(Player player, Goal goal) {
        this.currentPlayer = player;
        this.goal = goal;
    }

    @Override
    public void clicked(Button button) {
        switch (button) {
            case GOAL_DROP:
                currentPlayer.removeGoal(goal);
                // TODO: simulate mouse exiting goal button to remove tooltips
                break;
        }
    }
}
