package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import fvs.taxe.actor.TrainActor;
import gameLogic.Game;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

class TrainMoveController {
    //This class handles all train movement in the game
    private final Context context;
    private final Train train;

    public TrainMoveController(Context context, Train train) {
        this.context = context;
        this.train = train;
        addMoveActions();
    }

    // an action for the train to run before it starts moving across the screen
    private RunnableAction beforeAction() {
        return new RunnableAction() {
            public void run() {
                train.getActor().setVisible(true);

                // If a train's location is (-1, -1) it's moving....
                train.setPosition(new Position(-1, -1));
            }
        };
    }

    private RunnableAction perStationAction(final Station station) {
        return new RunnableAction() {
            public void run() {
                if (!train.getRoute().get(0).equals(station))
                    train.getActor().setRecentlyPaused(false);

                train.addHistory(station, context.getGameLogic().getPlayerManager().getTurnNumber());
                int stationIndex = train.getRoute().indexOf(station); //find this station in route
                int nextIndex = stationIndex + 1;

                //This checks whether or not the train is at its final
                //destination by checking whether the index is still less than the list size
                if (nextIndex < train.getRoute().size()) {
                    //Checks whether the next connection is blocked, if so the train is paused, if not the train is unpaused.
                    if (Game.getInstance().getMap().isConnectionBlocked(station,
                            train.getRoute().get(nextIndex))) {
                        train.getActor().setPaused(true);
                        train.getActor().setRecentlyPaused(false);
                    } else if (train.getActor().isPaused()) {
                        train.getActor().setPaused(false);
                        train.getActor().setRecentlyPaused(true);
                    }
                } else {
                    //If the train is at its final destination then the train
                    //is set to unpaused so that it does not cause issues
                    //elsewhere in the program.
                    train.getActor().setPaused(false);
                }
            }
        };
    }

    // an action for the train to run after it has moved the whole route
    private RunnableAction afterAction() {
        return new RunnableAction() {
            public void run() {
                //This informs the user that their train has completed a goal, if it has
                ArrayList<String> completedGoals = context.getGameLogic().getGoalManager().trainArrived(train, train.getPlayer());
                for (String message : completedGoals)
                    context.getTopBarController().displayFlashMessage(message, Color.WHITE, 2);

                //Sets the train's position to be equal to its final destination's
                //position so that it is appropriately hidden and linked to the station
                train.setPosition(train.getFinalDestination().getLocation());
                train.getActor().setVisible(false);
                train.setFinalDestination(null);
            }
        };
    }

    void addMoveActions() {
        SequenceAction actions = Actions.sequence();
        IPositionable current = train.getPosition();

        if (train.isMoving())
            current = new Position((int) train.getActor().getBounds().getX(), (int) train.getActor().getBounds().getY());
        actions.addAction(beforeAction());

        for (final Station station : train.getRoute()) {
            IPositionable next = station.getLocation();
            //This calculates how long it will take for the train to travel to the next station on the route
            float duration = getDistance(current, next) / train.getSpeed();

            //This adds the action to the actor which makes it move from point A to point B in a certain amount of time, calculated using duration and the two station positions.
            actions.addAction(moveTo(next.getX() - TrainActor.width / 2, next.getY() - TrainActor.height / 2, duration));
            actions.addAction(perStationAction(station));
            current = next;
        }
        actions.addAction(afterAction());
        train.getActor().clearActions();
        train.getActor().addAction(actions);
    }

    private float getDistance(IPositionable a, IPositionable b) {
        //This method returns the absolute distance from point A to point B in pixels
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
    }
}
