package test;

import gameLogic.goal.Goal;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;
import org.junit.Test;
import org.junit.Assert;

public class GoalTest {

    Station origin = new Station("station1", new Position(5, 5));
    Station destination = new Station("station2", new Position(2, 2));

    Station station3 = new Station("station3", new Position(3, 5));
    Station station4 = new Station("station4", new Position(4, 2));
    Station station5 = new Station("station5", new Position(5, 1));
    Station station6 = new Station("station6", new Position(6, 2));
    Station station7 = new Station("station7", new Position(7, 5));
    Station station8 = new Station("station8", new Position(8, 2));


    Station intermediary = new Station("station3", new Position(5, 5));
    Train train = new Train("RedTrain", "RedTrain.png", "RedTrainRight.png", 250);
    Goal goal = new Goal(origin, destination, intermediary, 0, 4, 50, 20, train);

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

    @Test
    public void testCompletedWithinMaxTurns() throws Exception {

        Goal anotherGoal = new Goal(origin, destination, intermediary, 20, 0, 20, 50, train);
        Assert.assertFalse(anotherGoal.completedWithinMaxTurns(train));

        Goal yetAnotherGoal = new Goal(destination, origin, intermediary, 20, 10, 20, 50, train);
        train.addHistory(origin, 20);
        train.addHistory(station4, 22);
        train.addHistory(station6, 23);
        train.addHistory(station7, 40);
        train.addHistory(station8, 56);
        train.addHistory(destination, 60);
        Assert.assertFalse(yetAnotherGoal.completedWithinMaxTurns(train));

    }

    @Test
    public void testCompletedWithTrain() throws Exception {
        Assert.assertEquals(goal.getTrain().getName(), train.getName());
        Train timeOfMyLife = new Train("I just love testing", "RedTrain.png", "RedTrainRight.png", 250);
        Assert.assertFalse(goal.getTrain().getName().equals(timeOfMyLife.getName()));

    }


}