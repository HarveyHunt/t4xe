package test;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.map.*;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.Engineer;
import gameLogic.resource.Obstacle;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TrackTest {
    private Map map;

    @Before
    public void mapSetup() throws Exception {
        map = new Map();
    }

    @Test
    public void testTracks() throws Exception {
        Station station1 = map.getStationByName("London");
        Station station2 = map.getStationByName("York");

        Connection connection = map.getConnection(station1, station2);

        Assert.assertTrue(connection != null);

        map.disableConnection(connection);

        Assert.assertTrue(map.getConnection(station1, station2, ConnectionType.DISABLED) != null);

        map.enableConnection(connection);

        Assert.assertTrue(map.getConnection(station1, station2, ConnectionType.ENABLED) != null);
    }
}