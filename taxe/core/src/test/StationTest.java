package test;


import gameLogic.map.Position;
import gameLogic.map.Station;
import junit.framework.TestCase;

public class StationTest extends TestCase {

    public void testStationCreation() throws Exception {
        int x = 5000;
        int y = 7000;
        String name = "TestStation";

        Station testStation = new Station(name, new Position(x, y));

        assertTrue("Position is wrong", testStation.getLocation().getX() == x
                && testStation.getLocation().getY() == y);
        assertTrue("Name is wrong", testStation.getName().equals(name));
    }
}
