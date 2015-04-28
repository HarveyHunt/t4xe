package test;

import gameLogic.map.Connection;
import gameLogic.map.Position;
import gameLogic.map.Station;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Assert;

public class ConnectionTest {
    Position position1 = new Position(200, 200);
    Position position2 = new Position(500, 500);
    Station station1 = new Station("station1", position1);
    Station station2 = new Station("station2", position2);
    Connection testConnection = new Connection(station1, station2);

    @Test
    public void testBlocking() throws Exception {
        testConnection.setBlocked(1);
        Assert.assertEquals("Number of turns blocked not set", testConnection.getTurnsBlocked(), 1);
        testConnection.decrementBlocked();
        Assert.assertFalse("Number of turns blocked not decrementing", testConnection.isBlocked());
    }
}
