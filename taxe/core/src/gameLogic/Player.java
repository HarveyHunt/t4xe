package gameLogic;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.map.Connection;
import gameLogic.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private PlayerManager pm;
    private List<Resource> resources;
    private List<Goal> goals;
    private int number;
    private int score;

    public Player(PlayerManager pm, int playerNumber) {
        goals = new ArrayList<Goal>();
        resources = new ArrayList<Resource>();
        this.pm = pm;
        number = playerNumber;
        score = 0;
    }

    public void setObstacle (Connection connection)
    {
        connection.setBlocked(4);
    }

    public void unsetObstacle (Connection connection)
    {
        connection.setBlocked(0);
    }


    public List<Resource> getResources() {
        return resources;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
        changed();
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
        resource.dispose();
        changed();
    }

    public void addGoal(Goal goal) {
    	int uncompleteGoals = 0;
    	for(Goal existingGoal : goals) {
    		if(!existingGoal.getComplete()) {
    			uncompleteGoals++;
    		}
    	}
        if (uncompleteGoals >= GoalManager.CONFIG_MAX_PLAYER_GOALS) {
            //throw new RuntimeException("Max player goals exceeded");
        	return;
        }

        goals.add(goal);
        changed();
    }
    
    public void completeGoal(Goal goal) {
    	this.score = this.score + goal.getScore();
        goal.setComplete();
        changed();

    }

    /**
     * Method is called whenever a property of this player changes, or one of the player's resources changes
     */
    public void changed() {
        pm.playerChanged();
    }

    public List<Goal> getGoals() {
        return goals;
    }
    
    public PlayerManager getPlayerManager() {
    	return pm;
    }
    
    public int getPlayerNumber() {
    	return number;
    }

    public int getScore(){ return score;}

    public void updateScore(Goal goal){
        this.score = this.score + goal.getBonus();
    }

    public void removeGoal(Goal goal) {
        if (goals.contains(goal))
            goals.remove(goal);
    }

}
