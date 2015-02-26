package test;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import junit.framework.TestCase;

public class GoalManagerTest extends TestCase {
    private final ResourceManager rs = new ResourceManager();
    private final GoalManager goalManager = new GoalManager(rs);
    private final PlayerManager playerManager = new PlayerManager();


    //  playerManager.createPlayers(2);
    //  Player player1 = playerManager.getCurrentPlayer();

    private final Train train = new Train("Green", "", "", 100);

    private final Station station1 = new Station("station1", new Position(5, 5));
    private final Station station2 = new Station("station2", new Position(2, 2));
    private final Station station3 = new Station("station3", new Position(6, 2));

    private final Goal goal = new Goal(station1, station2, station3, 0, 0, 0, 0, train);

    public void testGenerateRandom() throws Exception {

        System.out.println(goal.toString());
        //player1.addGoal(goal);
        //player1.addResource(train);

        Goal newGoal1 = goalManager.generateRandom(1);
        Goal newGoal2 = goalManager.generateRandom(3);
        Goal newGoal3 = goalManager.generateRandom(5);
        Goal newGoal4 = goalManager.generateRandom(7);
        Goal newGoal5 = goalManager.generateRandom(9);
        Goal newGoal6 = goalManager.generateRandom(11);
        Goal newGoal7 = goalManager.generateRandom(13);
    }

    public void testAddRandomGoalToPlayer() throws Exception {
        playerManager.createPlayers(2);
        Player player1 = playerManager.getCurrentPlayer();
        assertEquals(0, player1.getGoals().size());

        goalManager.addRandomGoalToPlayer(player1);
        goalManager.addRandomGoalToPlayer(player1);
        goalManager.addRandomGoalToPlayer(player1);


        assertEquals(3, player1.getGoals().size());


    }
}