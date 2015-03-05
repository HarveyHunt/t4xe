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
import gameLogic.map.Connection;
import gameLogic.map.ConnectionType;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.resource.Engineer;
import gameLogic.resource.Obstacle;
import gameLogic.resource.Skip;
import gameLogic.resource.Train;

public class DialogButtonClicked implements ResourceDialogClickListener {
    //This class is huge and seemingly complicated because it handles the events based off of any button being clicked
    private final Context context;
    private final Player currentPlayer;
    private final Train train;
    private final Obstacle obstacle;
    private final Skip skip;
    private final Engineer engineer;

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
                currentPlayer.removeResource(train);
                break;
            //The reason that all the placement case statements are in their own scope ({}) is due to the fact that switch statements do not create their own scopes between cases.
            //Instead these must be manually defined, which was done to allow for instantiation of new TrainControllers.
            case TRAIN_PLACE: {
                Pixmap pixmap = new Pixmap(Gdx.files.internal(train.getCursorImage()));
                Gdx.input.setCursorImage(pixmap, 0, 0);
                pixmap.dispose();

                Game.getInstance().setState(GameState.PLACING_TRAIN);
                TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);

                //A station click listener is generated to handle the placement of the train
                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                        if (station instanceof CollisionStation) {
                            context.getTopBarController().displayFlashMessage("Trains cannot be placed at junctions.", Color.RED);
                        } else {
                            train.setPosition(station.getLocation());
                            train.addHistory(station, Game.getInstance().getPlayerManager().getTurnNumber());

                            //Resets the cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Hides the current train but makes all moving trains visible
                            TrainController trainController = new TrainController(context);
                            TrainActor trainActor = trainController.renderTrain(train);
                            trainController.setTrainsVisible(null, true);
                            train.setActor(trainActor);

                            StationController.unsubscribeStationClick(this);
                            Game.getInstance().setState(GameState.NORMAL);
                        }
                    }
                };

                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ESCAPE) {
                            //Sets all of the currently placed trains back to visible
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            //Resets the cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);

                            context.getStage().removeListener(this);
                        }
                        return true;
                    }
                };
                context.getStage().addListener(keyListener);
                StationController.subscribeStationClick(stationListener);
                break;
            }

            case TRAIN_ROUTE:
                context.getRouteController().begin(train);
                break;

            case VIEW_ROUTE:
                context.getRouteController().viewRoute(train);
                break;

            case OBSTACLE_DROP:
                currentPlayer.removeResource(obstacle);
                break;

            case OBSTACLE_USE: {
                Pixmap pixmap = new Pixmap(Gdx.files.internal("BlockageCursor.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0);
                pixmap.dispose();

                // While it would be useful to see trains while placing an obstacle,
                // this was done to remove the possibility of trains preventing the user being able to click a node
                Game.getInstance().setState(GameState.PLACING_RESOURCE);
                final TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);
                context.getTopBarController().displayMessage("Placing Obstacle", Color.BLACK);

                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                        //If the station clicked is the first one to be chosen by the user
                        if (obstacle.getStation1() == null) {
                            obstacle.setStation1(station);
                        } else {
                            //Sets the second station of the blockage to be the one that the user selects once they have selected the first one
                            obstacle.setStation2(station);
                            if (context.getGameLogic().getMap().doesConnectionExist(obstacle.getStation1().getName(), obstacle.getStation2().getName())) {
                                //If the connections exists then the connection is blocked for 5 turns
                                obstacle.use(context.getGameLogic().getMap().getConnection(obstacle.getStation1(), obstacle.getStation2()));
                                currentPlayer.removeResource(obstacle);
                                //Note: No checking is put in place to see if a train is already travelling along the track that the user blocks
                                //In practice this means that a train already on the track will continue its motion unopposed
                                //This is considered the intended behaviour of the obstacle feature as its intent is to reward proactive players, not reward reactive ones
                                //If this is not how you want your obstacles to work you might consider preventing the player from placing obstacles on blocked connections or immediately pausing any train on that connection
                            } else {
                                //Informs the player that their selection is invalid and cancels placement
                                Dialog dialog = new Dialog("Invalid Selection", context.getSkin());
                                dialog.text("You have selected two stations which are not connected." +
                                        "\nPlease use the obstacle again.").align(Align.center);
                                dialog.button("OK", "OK");
                                dialog.show(context.getStage());
                                obstacle.setStation1(null);
                                obstacle.setStation2(null);
                            }
                            context.getTopBarController().clearMessage();
                            StationController.unsubscribeStationClick(this);

                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);
                            trainController.setTrainsVisible(null, true);
                        }
                    }
                };

                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ESCAPE) {
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            Gdx.input.setCursorImage(null, 0, 0);

                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);
                            context.getTopBarController().clearMessage();
                            context.getStage().removeListener(this);
                        }
                        return true;
                    }
                };
                context.getStage().addListener(keyListener);
                StationController.subscribeStationClick(stationListener);
                break;
            }

            case ENGINEER_REPAIR_TRACK: {
                Game.getInstance().setState(GameState.PLACING_RESOURCE);
                Pixmap pixmap = new Pixmap(Gdx.files.internal("engineer.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0);
                pixmap.dispose();

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
                            Dialog dialog = new Dialog("Invalid Selection", context.getSkin());
                            dialog.button("OK", "OK");
                            Connection connection = context.getGameLogic().getMap().getConnection(engineer.getStation1(),
                                    engineer.getStation2());

                            if (connection != null && connection.isBlocked()) {
                                //If the connection is blocked then it removes the blockage
                                engineer.use(context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()));
                                currentPlayer.removeResource(engineer);
                            } else if (connection == null) {
                                //If the connection does not exist then placement is cancelled and the user is informed of this
                                dialog.text("You have selected two stations which are not connected." +
                                        "\nPlease use the engineer again.").align(Align.center);
                                dialog.show(context.getStage());
                                engineer.setStation1(null);
                                engineer.setStation2(null);
                            } else {
                                //If the connection is not blocked then placement is cancelled and the user is informed
                                dialog.text("You have selected a connection which is not blocked." +
                                        "\nPlease use the engineer again.").align(Align.center);
                                dialog.show(context.getStage());
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

                // Handle ESC key-press
                StationController.subscribeStationClick(stationListener);
                final InputListener keyListener = escPressedHandler(stationListener);
                this.context.getStage().addListener(keyListener);
                break;
            }

            case ENGINEER_DROP:
                currentPlayer.removeResource(engineer);
                break;

            case SKIP_RESOURCE:
                context.getGameLogic().getPlayerManager().getInactivePlayer().setSkip(true);
                currentPlayer.removeResource(skip);
                break;

            case SKIP_DROP:
                currentPlayer.removeResource(skip);
                break;

            case TRAIN_CHANGE_ROUTE:
                context.getRouteController().begin(train);
                break;

            case ENGINEER_ADD_TRACK: {
                context.getGameLogic().setState(GameState.ADDING_TRACK);

                Pixmap pixmap = new Pixmap(Gdx.files.internal("engineer.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0);
                pixmap.dispose();

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

                            Connection connection = context.getGameLogic().getMap().getConnection(engineer.getStation1(),
                                    engineer.getStation2(), ConnectionType.DISABLED);

                            //If a connection exists then it checks whether the connection is blocked
                            if (connection != null && connection.isBlocked()) {
                                //If the connection is blocked then it removes the blockage
                                //engineer.use(context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2()));

                                context.getGameLogic().getMap().enableConnection(context.getGameLogic().getMap().getConnection(engineer.getStation1(), engineer.getStation2(), ConnectionType.DISABLED));
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
                            context.getTopBarController().clearMessage();
                            StationController.unsubscribeStationClick(this);
                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);
                            trainController.setTrainsVisible(null, true);
                        }
                    }
                };
                // Handle ESC key-press
                StationController.subscribeStationClick(stationListener);
                final InputListener keyListener = escPressedHandler(stationListener);
                this.context.getStage().addListener(keyListener);
                break;
            }


            case ENGINEER_REMOVE_TRACK: {
                //This is called when the player presses a ENGINEER_REMOVE_TRACK button

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
                            Connection connection = context.getGameLogic().getMap().getConnection(engineer.getStation1(),
                                    engineer.getStation2());
                            if (connection != null) {
                                context.getGameLogic().getMap().disableConnection(connection);
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
                            context.getTopBarController().clearMessage();
                            StationController.unsubscribeStationClick(this);
                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);
                            trainController.setTrainsVisible(null, true);
                        }
                    }
                };

                // Handle ESC key-press
                StationController.subscribeStationClick(stationListener);
                final InputListener keyListener = escPressedHandler(stationListener);
                this.context.getStage().addListener(keyListener);
                break;
            }
        }
    }

    private InputListener escPressedHandler(final StationClickListener stationListener) {
        return new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    TrainController trainController = new TrainController(context);
                    trainController.setTrainsVisible(null, true);

                    Gdx.input.setCursorImage(null, 0, 0);

                    StationController.unsubscribeStationClick(stationListener);
                    Game.getInstance().setState(GameState.NORMAL);
                    context.getTopBarController().clearMessage();
                    context.getStage().removeListener(this);
                }
                return true;
            }
        };
    }
}
