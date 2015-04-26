package test;

import gameLogic.map.Connection;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Engineer;
import gameLogic.resource.Obstacle;
import junit.framework.TestCase;

public class ObstacleEgnineerTest extends TestCase {

    public void testUse() throws Exception {

        Station station1 = new Station("station1", new Position(6, 2));
        Station station2 = new Station("station2", new Position(4, 2));
        Connection connection = new Connection(station1, station2);

        assertFalse(connection.isBlocked());

        Obstacle obstacle = new Obstacle();
        obstacle.use(connection);

        assertTrue(connection.isBlocked());

        Engineer engineer = new Engineer();
        engineer.use(connection);

        assertFalse(connection.isBlocked());
    }
}