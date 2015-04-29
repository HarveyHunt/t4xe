package test;


import gameLogic.goal.Goal;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PlayerTest {
    private PlayerManager pm;
    Train train1 = new Train("train1", "RedTrain.png", "RedTrainRight.png", 250);
    Train train2 = new Train("train2", "RedTrain.png", "RedTrainRight.png", 250);
    Train train3 = new Train("train3", "RedTrain.png", "RedTrainRight.png", 250);

    Station origin = new Station("station1", new Position(5, 5));
    Station destination = new Station("station2", new Position(2, 2));
    Station intermediary = new Station("station3", new Position(5, 5));
    Goal goal = new Goal(origin, destination, intermediary, 0, 4, 50, 20, train1);

    @Before
    public void setUp() throws Exception {
        pm = new PlayerManager();
        pm.createPlayers(2);
        ArrayList<Train> trainList = new ArrayList<Train>();
        trainList.add(train1);
        trainList.add(train2);
        trainList.add(train3);
    }

    @Test
    public void testGetTrains() {
        Player p1 = pm.getActivePlayer();
        p1.addResource(train1);
        p1.addResource(train2);
        Assert.assertFalse(p1.getResources().contains(train3));
        p1.addResource(train3);
        Assert.assertTrue(p1.getResources().contains(train1)&&
                p1.getResources().contains(train2)&&
                p1.getResources().contains(train3));
    }

    @Test
    public void testAddGoal() {
        Player p1 = pm.getActivePlayer();
        p1.addGoal(goal);
        Assert.assertTrue(p1.getGoals().contains(goal));
    }
}
