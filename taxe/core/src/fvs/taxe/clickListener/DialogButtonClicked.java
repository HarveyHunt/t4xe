package fvs.taxe.clickListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import fvs.taxe.Button;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.StationController;
import fvs.taxe.controller.TrainController;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.resource.Engineer;
import gameLogic.resource.Obstacle;
import gameLogic.resource.Skip;
import gameLogic.resource.Train;

public class DialogButtonClicked implements ResourceDialogClickListener {
    //This class is huge and seemingly complicated because it handles the events based off of any button being clicked
    private Context context;
    private Player currentPlayer;
    private Train train;
    private Obstacle obstacle;
    private Skip skip;
    private Engineer engineer;

    public DialogButtonClicked(Context context, Player player, Train train) {
        //This constructor is used when a train dialog button is clicked.
        //Train is set to the train that the dialog was associated with and the other variables are set to null
        this.currentPlayer = player;
        this.train = train;
        this.context = context;
        this.obstacle = null;
        this.skip = null;
        this.engineer = null;
    }

    public DialogButtonClicked(Context context, Player player, Obstacle obstacle) {
        //This constructor is used when an obstacle dialog button is clicked.
        //obstacle is set to the obstacle that the dialog was associated with and the other variables are set to null
        this.currentPlayer = player;
        this.train = null;
        this.skip = null;
        this.context = context;
        this.obstacle = obstacle;
        this.engineer = null;
    }

    public DialogButtonClicked(Context context, Player player, Skip skip) {
        //This constructor is used when an skip dialog button is clicked.
        //skip is set to the skip that the dialog was associated with and the other variables are set to null
        this.currentPlayer = player;
        this.train = null;
        this.skip = skip;
        this.context = context;
        this.obstacle = null;
        this.engineer = null;
    }

    public DialogButtonClicked(Context context, Player player, Engineer engineer) {
        //This constructor is used when an engineer dialog button is clicked.
        //engineer is set to the engineer that the dialog was associated with and the other variables are set to null
        this.currentPlayer = player;
        this.train = null;
        this.engineer = engineer;
        this.context = context;
        this.obstacle = null;
        this.skip = null;
    }

    @Override
    public void clicked(Button button) {
        switch (button) {
            case TRAIN_DROP:
                //If a TRAIN_DROP button is pressed then the train is removed from the player's resources
                currentPlayer.removeResource(train);
                break;

            //The reason that all the placement case statements are in their own scope ({}) is due to the fact that switch statements do not create their own scopes between cases.
            //Instead these must be manually defined, which was done to allow for instantiation of new TrainControllers.
            case TRAIN_PLACE: {
                //If the TRAIN_PLACE button is pressed then the game is set up so that the train can be placed

                //This sets the cursor to be the one associated with the train loaded from the assets folder
                //We updated the cursors from FVS' original ones to add clarity to the player
                Pixmap pixmap = new Pixmap(Gdx.files.internal(train.getCursorImage()));
                Gdx.input.setCursorImage(pixmap, 0, 0); // these numbers will need tweaking
                pixmap.dispose();

                //Begins the placement of a train
                Game.getInstance().setState(GameState.PLACING_TRAIN);

                //Hides all trains currently on the map
                TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);

                //A station click listener is generated to handle the placement of the train
                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                        //Checks whether a node is a junction or not. If it is then the train cannot be placed there and the user is informed
                        if (station instanceof CollisionStation) {
                            context.getTopBarController().displayFlashMessage("Trains cannot be placed at junctions.", Color.RED);

                        } else {
                            //This puts the train at the station that the user clicks and adds it to the trains visited history
                            train.setPosition(station.getLocation());
                            train.addHistory(station, Game.getInstance().getPlayerManager().getTurnNumber());

                            //Resets the cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Hides the current train but makes all moving trains visible
                            TrainController trainController = new TrainController(context);
                            TrainActor trainActor = trainController.renderTrain(train);
                            trainController.setTrainsVisible(null, true);
                            train.setActor(trainActor);

                            //Unsubscribes from the listener so that it does not call this code again when it is obviously not necessary, without this placing of trains would never end
                            StationController.unsubscribeStationClick(this);
                            Game.getInstance().setState(GameState.NORMAL);
                        }
                    }
                };

                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        //If the Escape key is pressed while placing a train then it is cancelled
                        //This is a new addition as the original code did not allow the user to cancel placement of trains once they had begun which was frustrating
                        if (keycode == Input.Keys.ESCAPE) {
                            //Sets all of the currently placed trains back to visible
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            //Resets the cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Unsubscribes from the listener so that it does not call the code when it is not intended to
                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);

                            //Removes itself from the keylisteners of the game as otherwise there would be a lot of null pointer exceptions and unintended behaviour
                            context.getStage().removeListener(this);
                        }

                        //keyDown requires you to return the boolean true when the function has completed, so this ends the function
                        return true;
                    }
                };

                //Adds the keyListener to the game
                context.getStage().addListener(keyListener);

                //Adds the stationClick listener to the stationController's listeners
                StationController.subscribeStationClick(stationListener);
                break;
            }

            case TRAIN_ROUTE:
                //Begins routing a train if the TRAIN_ROUTE button is clicked
                context.getRouteController().begin(train);
                break;

            case VIEW_ROUTE:
                //Shows the user the train's current route if they click on VIEW_ROUTE button
                context.getRouteController().viewRoute(train);
                break;

            case OBSTACLE_DROP:
                //Removes the obstacle from the current player's inventory if they click the OBSTACLE_DROP button
                currentPlayer.removeResource(obstacle);
                break;

            case OBSTACLE_USE: {

                //Sets the cursor to be the one used to indicate placing a blockage
                Pixmap pixmap = new Pixmap(Gdx.files.internal("BlockageCursor.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0); // these numbers will need tweaking
                pixmap.dispose();

                //Indicates that a resource is currently being placed and to hide all trains
                //While it would be useful to see trains while placing an obstacle, this was done to remove the possibility of trains preventing the user being able to click a node
                Game.getInstance().setState(GameState.PLACING_RESOURCE);
                final TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);
                context.getTopBarController().displayMessage("Placing Obstacle", Color.BLACK);

                //Creates a clickListener for when a station is clicked
                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {

                        //If the station clicked is the first one to be chosen by the user
                        if (obstacle.getStation1() == null) {

                            //Sets the first station to be the one that the user selects
                            obstacle.setStation1(station);

                        } else {
                            //Sets the second station of the blockage to be the one that the user selects once they have selected the first one
                            obstacle.setStation2(station);

                            //Checks whether a connection exists between the two stations
                            if (context.getGameLogic().getMap().doesConnectionExist(obstacle.getStation1().getName(), obstacle.getStation2().getName())) {

                                //If the connections exists then the connection is blocked for 5 turns
                                obstacle.use(context.getGameLogic().getMap().getConnection(obstacle.getStation1(), obstacle.getStation2()));
                                //The obstacle is removed from the player's inventory as it has been used
                                currentPlayer.removeResource(obstacle);

                                //Note: No checking is put in place to see if a train is already travelling along the track that the user blocks
                                //In practice this means that a train already on the track will continue its motion unopposed
                                //This is considered the intended behaviour of the obstacle feature as its intent is to reward proactive players, not reward reactive ones
                                //If this is not how you want your obstacles to work you might consider preventing the player from placing obstacles on blocked connections or immediately pausing any train on that connection

                            } else {

                                //Informs the player that their selection is invalid and cancels placement
                                Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                dia.text("You have selected two stations which are not connected." +
                                        "\nPlease use the obstacle again.").align(Align.center);
                                dia.button("OK", "OK");
                                dia.show(context.getStage());
                                obstacle.setStation1(null);
                                obstacle.setStation2(null);
                            }
                            //This code runs regardless of whether the placement was successful, this returns the game to its normal state

                            //Resets the topBar
                            context.getTopBarController().displayFlashMessage("", Color.BLACK);

                            //Unsubscribes from the StationClickListener as this would cause a lot of errors and unexpected behaviour is not called from the correct context
                            StationController.unsubscribeStationClick(this);

                            //Resets the cursor to the normal one
                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);

                            //Sets all moving trains to be visible
                            trainController.setTrainsVisible(null, true);
                        }
                    }
                };
                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        //If the Escape key is pressed while placing an obstacle then it is cancelled
                        if (keycode == Input.Keys.ESCAPE) {
                            //Makes all trains visible
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            //Resets cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Unsubscribes from the StationClickListener as this would cause a lot of errors and unexpected behaviour is not called from the correct context
                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);

                            //Resets the topBar
                            context.getTopBarController().clearMessage();

                            //Removes itself from the keylisteners of the game as otherwise there would be a lot of null pointer exceptions and unintended behaviour
                            context.getStage().removeListener(this);
                        }
                        //keyDown requires you to return the boolean true when the function has completed, so this ends the function
                        return true;
                    }
                };

                //Adds the listeners to their relevant entities
                context.getStage().addListener(keyListener);
                StationController.subscribeStationClick(stationListener);

                break;
            }

            case ENGINEER_USE: {
                //This is called when the player presses a ENGINEER_USE button

                Game.getInstance().setState(GameState.PLACING_RESOURCE);

                //Sets the cursor to be the one used for placement of engineers
                Pixmap pixmap = new Pixmap(Gdx.files.internal("engineer.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0); // these numbers will need tweaking
                pixmap.dispose();

                //Hides all trains
                final TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);
                context.getTopBarController().displayMessage("Placing Engineer", Color.BLACK);

                //Adds a station click listener that handles all the logic
                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                        if (engineer.getStation1() == null) {
                            //If the station is the first one clicked then it sets it to be station1
                            engineer.setStation1(station);
                        } else {
                            //If the station is the second one clicked then it sets it to station2
                            engineer.setStation2(station);

                            //Checks whether a connection exists between the two selected stations
                            if (context.getGameLogic().getMap().doesConnectionExist(engineer.getStation1().getName(), engineer.getStation2().getName())) {
                                //If a connection exists then it checks whether the connection is blocked
                                if (context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()).isBlocked()) {
                                    //If the connection is blocked then it removes the blockage
                                    engineer.use(context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()));
                                    currentPlayer.removeResource(engineer);
                                } else {
                                    //If the connection is not blocked then placement is cancelled and the user is informed
                                    Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                    dia.text("You have selected a connection which is not blocked." +
                                            "\nPlease use the engineer again.").align(Align.center);
                                    dia.button("OK", "OK");
                                    dia.show(context.getStage());
                                    engineer.setStation1(null);
                                    engineer.setStation2(null);
                                }
                            } else {
                                //If the connection does not exist then placement is cancelled and the user is informed of this
                                Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                dia.text("You have selected two stations which are not connected." +
                                        "\nPlease use the engineer again.").align(Align.center);
                                dia.button("OK", "OK");
                                dia.show(context.getStage());
                                engineer.setStation1(null);
                                engineer.setStation2(null);
                            }
                            //This resets all relevant values and unsubscribes from the listeners created for placing engineers
                            context.getTopBarController().clearMessage();
                            StationController.unsubscribeStationClick(this);
                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);
                            trainController.setTrainsVisible(null, true);
                        }
                    }
                };
                StationController.subscribeStationClick(stationListener);

                //Adds a keyListener that triggers when the
                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ESCAPE) {
                            //Makes all trains visible
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            //Resets cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Unsubscribes from the StationClickListener as this would cause a lot of errors and unexpected behaviour is not called from the correct context
                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);

                            //Resets the topBar
                            context.getTopBarController().clearMessage();

                            //Removes itself from the keylisteners of the game as otherwise there would be a lot of null pointer exceptions and unintended behaviour
                            context.getStage().removeListener(this);
                        }
                        return true;
                    }
                };
                this.context.getStage().addListener(keyListener);
                break;
            }

            case ENGINEER_DROP:
                //If the ENGINEER_DROP button is pressed then the resource is removed from the player's inventory
                currentPlayer.removeResource(engineer);
                break;

            case SKIP_RESOURCE:
                //If SKIP_RESOURCE is pressed then this finds the other player's playerNumber and sets their skipped boolean to true
                //If you wish to add more than 2 players then extra checking would have to be added here to ensure that the right player has their turn skipped
                //For our implementation just checking the two binary values is enough
                int p = context.getGameLogic().getPlayerManager().getCurrentPlayer().getPlayerNumber() - 1;
                if (p == 0) {
                    p = 1;
                } else {
                    p = 0;
                }

                context.getGameLogic().getPlayerManager().getAllPlayers().get(p).setSkip(true);
                //Removes the resource after it has been used
                currentPlayer.removeResource(skip);
                break;

            case SKIP_DROP:
                //Removes the resource from the player if they press the SKIP_DROP button
                currentPlayer.removeResource(skip);
                break;

            case TRAIN_CHANGE_ROUTE:
                //Begins the change route feature when TRAIN_CHANGE_ROUTE is pressed by the player
                context.getRouteController().begin(train);
                break;

            case TRACK_ADD: {
                context.getGameLogic().setState(GameState.ADDING_TRACK);

                //Sets the cursor to be the one used for placement of engineers
                Pixmap pixmap = new Pixmap(Gdx.files.internal("engineer.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0); // these numbers will need tweaking
                pixmap.dispose();

                //Hides all trains
                final TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);
                context.getTopBarController().displayMessage("Adding Track", Color.BLACK);

                //Adds a station click listener that handles all the logic
                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                        if (engineer.getStation1() == null) {
                            //If the station is the first one clicked then it sets it to be station1
                            engineer.setStation1(station);
                        } else {
                            //If the station is the second one clicked then it sets it to station2
                            engineer.setStation2(station);

                            //Checks whether a connection exists between the two selected stations
                            if (context.getGameLogic().getMap().doesDisabledConnectionExist(engineer.getStation1().getName(), engineer.getStation2().getName())) {
                                //If a connection exists then it checks whether the connection is blocked
                                if (context.getGameLogic().getMap().getDisabledConnection(engineer.getStation1(), engineer.getStation2()) != null) {
                                    //If the connection is blocked then it removes the blockage
                                    //engineer.use(context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()));

                                    context.getGameLogic().getMap().enableConnection(context.getGameLogic().getMap().getDisabledConnection(engineer.getStation1(), engineer.getStation2()));
                                    currentPlayer.removeResource(engineer);
                                } else {
                                    //If the connection is not blocked then placement is cancelled and the user is informed
                                    Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                    dia.text("You have selected two stations which cannot be connected." +
                                            "\nPlease use the engineer again.").align(Align.center);
                                    dia.button("OK", "OK");
                                    dia.show(context.getStage());
                                    engineer.setStation1(null);
                                    engineer.setStation2(null);
                                }
                            } else {
                                //If the connection does not exist then placement is cancelled and the user is informed of this
                                Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                dia.text("You have selected two stations which cannot be connected." +
                                        "\nPlease use the engineer again.").align(Align.center);
                                dia.button("OK", "OK");
                                dia.show(context.getStage());
                                engineer.setStation1(null);
                                engineer.setStation2(null);
                            }
                            //This resets all relevant values and unsubscribes from the listeners created for placing engineers
                            context.getTopBarController().clearMessage();
                            StationController.unsubscribeStationClick(this);
                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);
                            trainController.setTrainsVisible(null, true);
                        }
                    }
                };
                StationController.subscribeStationClick(stationListener);

                //Adds a keyListener that triggers when the
                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ESCAPE) {
                            //Makes all trains visible
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            //Resets cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Unsubscribes from the StationClickListener as this would cause a lot of errors and unexpected behaviour is not called from the correct context
                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);

                            //Resets the topBar
                            context.getTopBarController().clearMessage();

                            //Removes itself from the keylisteners of the game as otherwise there would be a lot of null pointer exceptions and unintended behaviour
                            context.getStage().removeListener(this);
                        }
                        return true;
                    }
                };

                this.context.getStage().addListener(keyListener);
                break;
            }


            case TRACK_REMOVE: {
                //This is called when the player presses a TRACK_REMOVE button

                Game.getInstance().setState(GameState.REMOVING_TRACK);

                //Sets the cursor to be the one used for placement of engineers
                Pixmap pixmap = new Pixmap(Gdx.files.internal("engineer.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0); // these numbers will need tweaking
                pixmap.dispose();

                //Hides all trains
                final TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);
                context.getTopBarController().displayMessage("Destroying Track", Color.BLACK);

                //Adds a station click listener that handles all the logic
                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                        if (engineer.getStation1() == null) {
                            //If the station is the first one clicked then it sets it to be station1
                            engineer.setStation1(station);
                        } else {
                            //If the station is the second one clicked then it sets it to station2
                            engineer.setStation2(station);

                            //Checks whether a connection exists between the two selected stations
                            if (context.getGameLogic().getMap().doesConnectionExist(engineer.getStation1().getName(), engineer.getStation2().getName())) {
                                //If a connection exists then it checks whether the connection is blocked
                                if (context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()) != null) {
                                    //If the connection is blocked then it removes the blockage
                                    //engineer.use(context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()));

                                    context.getGameLogic().getMap().disableConnection(context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()));
                                    currentPlayer.removeResource(engineer);
                                } else {
                                    //If the connection is not blocked then placement is cancelled and the user is informed
                                    Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                    dia.text("You have selected two stations which are not connected." +
                                            "\nPlease use the engineer again.").align(Align.center);
                                    dia.button("OK", "OK");
                                    dia.show(context.getStage());
                                    engineer.setStation1(null);
                                    engineer.setStation2(null);
                                }
                            } else {
                                //If the connection does not exist then placement is cancelled and the user is informed of this
                                Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                dia.text("You have selected two stations which are not connected." +
                                        "\nPlease use the engineer again.").align(Align.center);
                                dia.button("OK", "OK");
                                dia.show(context.getStage());
                                engineer.setStation1(null);
                                engineer.setStation2(null);
                            }
                            //This resets all relevant values and unsubscribes from the listeners created for placing engineers
                            context.getTopBarController().clearMessage();
                            StationController.unsubscribeStationClick(this);
                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);
                            trainController.setTrainsVisible(null, true);
                        }
                    }
                };
                StationController.subscribeStationClick(stationListener);

                //Adds a keyListener that triggers when the
                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ESCAPE) {
                            //Makes all trains visible
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            //Resets cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Unsubscribes from the StationClickListener as this would cause a lot of errors and unexpected behaviour is not called from the correct context
                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);

                            //Resets the topBar
                            context.getTopBarController().clearMessage();

                            //Removes itself from the keylisteners of the game as otherwise there would be a lot of null pointer exceptions and unintended behaviour
                            context.getStage().removeListener(this);
                        }
                        return true;
                    }
                };
                this.context.getStage().addListener(keyListener);
                break;
            }
        }
    }
}
