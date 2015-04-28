package test;


import gameLogic.map.Position;
import gameLogic.map.Station;
import org.junit.Test;
import org.junit.Assert;

public class StationTest {

    @Test
    public void testStationCreation() throws Exception {
        int x = 5000;
        int y = 7000;
        String name = "TestStation";

        Station testStation = new Station(name, new Position(x, y));

        Assert.assertTrue("Position is wrong", testStation.getLocation().getX() == x
                && testStation.getLocation().getY() == y);
        Assert.assertTrue("Name is wrong", testStation.getName().equals(name));
    }
}
