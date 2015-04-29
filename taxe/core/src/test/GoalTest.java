package test;

import gameLogic.goal.Goal;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class GoalTest {
    // More stations than Metro 2033...
    private Station origin;
    private Station destination;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;
    private Station station7;
    private Station station8;
    private Station intermediary;
    private Train train;
    private Goal goal;

    @Before
    public void setupGoalTest() {
        origin = new Station("station1", new Position(5, 5));
        destination = new Station("station2", new Position(2, 2));
        station3 = new Station("station3", new Position(3, 5));
        station4 = new Station("station4", new Position(4, 2));
        station5 = new Station("station5", new Position(5, 1));
        station6 = new Station("station6", new Position(6, 2));
        station7 = new Station("station7", new Position(7, 5));
        station8 = new Station("station8", new Position(8, 2));
        intermediary = new Station("station3", new Position(5, 5));
        train = new Train("RedTrain", "RedTrain.png", "RedTrainRight.png", 250);
        goal = new Goal(origin, destination, intermediary, 0, 4, 50, 20, train);
    }

    @Test
    public void testIsComplete() throws Exception {
        train.addHistory(origin, 0);
        train.addHistory(station3, 1);
        train.addHistory(station4, 4);
        train.addHistory(station5, 6);
        train.addHistory(station6, 10);
        train.addHistory(station7, 11);
        train.addHistory(station8, 16);

        Assert.assertFalse(goal.isComplete(train));
        train.setFinalDestination(destination);
        train.addHistory(destination, 18);
        Assert.assertTrue(goal.isComplete(train));
    }

    /**
     * For this test, not enough turns have passed for us to have failed but
     * we haven't reached the destination.
     */
    @Test
    public void testNotCompletedWithinMaxTurnsAsNotReachedEnd() {
        goal = new Goal(origin, destination, intermediary, 10, 10, 20, 50, train);
        Assert.assertFalse(goal.completedWithinMaxTurns(train));
    }

    @Test
    public void testMissedDestination() {
        goal = new Goal(destination, origin, intermediary, 10, 10, 20, 50, train);
        train.addHistory(origin, 0);
        train.addHistory(station3, 1);

        Assert.assertFalse(goal.isComplete(train));
    }

    @Test
    public void testMissedOrigin() {
        goal = new Goal(destination, origin, intermediary, 10, 10, 20, 50, train);
        train.addHistory(station3, 0);
        train.addHistory(destination, 1);

        Assert.assertFalse(goal.isComplete(train));
    }

    /**
     * For this test, we make it to the end destination, but not in time.
     */
    @Test
    public void testCompletedWithinMaxTurnsAsTooLate() {
        goal = new Goal(destination, origin, intermediary, 10, 10, 20, 50, train);
        train.addHistory(origin, 0);
        train.addHistory(station3, 1);

        train.setFinalDestination(destination);
        train.addHistory(destination, 8);

        Assert.assertFalse(goal.completedWithinMaxTurns(train));
    }

    @Test
    public void testCompletedWithTrain() throws Exception {
        Assert.assertEquals(goal.getTrain().getName(), train.getName());
        Assert.assertFalse(goal.getTrain().getName().equals("Randomname"));
    }
}