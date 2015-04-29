package test;

import gameLogic.map.Map;
import gameLogic.map.Position;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class MapTest {
    private Map map;

    @Before
    public void mapSetup() throws Exception {
        map = new Map();
    }

    @Test
    public void testAddStationAndConnection() throws Exception {
        String name1 = "station1";
        String name2 = "station2";

        int previousSize = map.getStations().size();

        map.addStation(name1, new Position(9999, 9999));
        map.addStation(name2, new Position(200, 200));

        Assert.assertTrue("Failed to add stations", map.getStations().size() - previousSize == 2);
        Assert.assertFalse(map.doesConnectionExist(name2, name1));

        map.addConnection(name1, name2);
        Assert.assertTrue("Connection addition failed", map.doesConnectionExist(name2, name1));

        map.disableConnection(map.getDisabledConnections().get(0));
        Assert.assertTrue(map.getDisabledConnections().get(0).isBlocked());

        // Should throw an error by itself
        map.getStationFromPosition(new Position(9999, 9999));
    }
}
