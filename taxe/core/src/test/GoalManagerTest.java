package test;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import org.junit.Assert;
import org.junit.Test;

public class GoalManagerTest {
    ResourceManager rs = new ResourceManager();
    GoalManager goalManager = new GoalManager(rs);
    PlayerManager playerManager = new PlayerManager();

    Train train = new Train("Green", "", "", 100);

    Station station1 = new Station("station1", new Position(5, 5));
    Station station2 = new Station("station2", new Position(2, 2));
    Station station3 = new Station("station3", new Position(6, 2));

    Goal goal = new Goal(station1, station2, station3, 0, 0, 0, 0, train);

    @Test
    public void testGenerateRandom() throws Exception {

        Goal newGoal1 = goalManager.generateRandom(1);
        Assert.assertNotNull("Goals not generating correctly", newGoal1.toString());
        Goal newGoal2 = goalManager.generateRandom(3);
        Assert.assertNotNull("Goals not generating correctly", newGoal2.toString());
    }

    @Test
    public void testAddRandomGoalToPlayer() throws Exception {
        playerManager.createPlayers(2);
        Player player1 = playerManager.getActivePlayer();
        Assert.assertEquals(0, player1.getGoals().size());

        goalManager.addRandomGoalToPlayer(player1);
        goalManager.addRandomGoalToPlayer(player1);
        goalManager.addRandomGoalToPlayer(player1);

        Assert.assertEquals("Goals not being added correctly", 3, player1.getGoals().size());
    }
}