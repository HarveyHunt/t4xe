package test;


import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;

public class TrainTest {
    private Train train;

    @Before
    public void trainSetup() throws Exception {
        train = new Train("RedTrain", "RedTrain.png", "RedTrainRight.png", 250);
    }

    @Test
    public void testFinalDestination() throws Error {
        Station station1 = new Station("station1", new Position(5, 5));
        Station station2 = new Station("station2", new Position(6, 6));
        ArrayList<Station> route = new ArrayList<Station>();
        route.add(station1);
        route.add(station2);

        train.setRoute(route);
        Assert.assertTrue("Setting a train route was not successful", train.getRoute().size() == 2);
        Assert.assertTrue("Final destination wasn't set", train.getFinalDestination() == station2);
    }


}
