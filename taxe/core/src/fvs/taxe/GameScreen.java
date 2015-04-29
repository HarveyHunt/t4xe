package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.*;
import fvs.taxe.dialog.DialogEndGame;
import fvs.taxe.replay.ReplayStage;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.listeners.GameStateListener;
import gameLogic.listeners.TurnListener;
import gameLogic.map.Map;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.*;

class GameScreen extends ScreenAdapter {
    private static final int ANIMATION_TIME = 2;
    final private TaxeGame game;
    private final ReplayStage stage;
    private final Texture mapTexture;
    private final Game gameLogic;
    private final Skin skin;
    private final Map map;
    private final Context context;
    private final StationController stationController;
    private final TopBarController topBarController;
    private final ResourceController resourceController;
    private final GoalController goalController;
    private final RouteController routeController;
    private float timeAnimated = 0;
    private final boolean replaying;

    public GameScreen(TaxeGame game, String replayFilePath, boolean replaying) {
        this.game = game;
        this.replaying = replaying;
        stage = new ReplayStage();

        /**
         * If we're replaying, we have to load the replay very early, so that
         * any use of Random will use the replay's seed.
         */
        if (replaying)
            stage.loadReplay(replayFilePath);


        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        gameLogic = Game.getInstance();
        context = new Context(stage, skin, game, gameLogic);
        Gdx.input.setInputProcessor(stage);

        //Draw background
        mapTexture = new Texture(Gdx.files.internal("gamemap.png"));
        map = gameLogic.getMap();

        Tooltip tooltip = new Tooltip(skin);
        stage.addActor(tooltip);

        //Initialises all of the controllers for the UI
        stationController = new StationController(context, tooltip);
        topBarController = new TopBarController(context);
        resourceController = new ResourceController(context);
        goalController = new GoalController(context);
        routeController = new RouteController(context);
        context.setRouteController(routeController);
        context.setTopBarController(topBarController);

        gameLogic.getPlayerManager().subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                //The game will not be set into the animating state for the first
                //turn to prevent player 1 from gaining an inherent advantage
                //by gaining an extra turn of movement.
                if (context.getGameLogic().getPlayerManager().getTurnNumber() != 1) {
                    gameLogic.setState(GameState.ANIMATING);
                    topBarController.displayFlashMessage("Time is passing...", Color.BLACK);
                }
                topBarController.displayFlashMessage("Replay in progress...", Color.BLACK, 10.0f);
            }
        });

        //Adds a listener that checks certain conditions at the end of every turn
        gameLogic.subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if ((gameLogic.getPlayerManager().getTurnNumber() == gameLogic.TOTAL_TURNS
                        || gameLogic.getPlayerManager().getActivePlayer().getScore() >= gameLogic.MAX_POINTS)
                        && state == GameState.NORMAL) {
                    //If the game should end due to the turn number or points total then the appropriate dialog is displayed
                    DialogEndGame dia = new DialogEndGame(GameScreen.this.game, gameLogic.getPlayerManager(), skin);
                    dia.show(stage);
                    saveReplay();
                } else if (gameLogic.getState() == GameState.ROUTING || gameLogic.getState() == GameState.PLACING_TRAIN) {
                    //If the player is routing or place a train then the goals and nodes are colour coded
                    goalController.setColours(StationController.colours);
                } else if (gameLogic.getState() == GameState.NORMAL) {
                    //If the game state is normal then the goal colour are reset to grey
                    goalController.setColours(new Color[3]);
                }
            }
        });
    }

    public void startReplay() {
        assert replaying;
        scheduleClickReplay();
    }

    /**
     * We want to replay click events one at a time, with a time interval
     * between them. Due to the design of this game, sitting in a tight loop
     * that iterates over each ClickEvent and then sleeps means that a lot of
     * systems within the game never get run.
     *
     * As a result, we set a timer that interrupts the game and initiates the
     * mouse click. The timer then schedules another timer to do the same again.
     */
    private void scheduleClickReplay() {
        topBarController.displayFlashMessage("Replay in progress ...", Color.BLACK, 10.0f);
        stage.replaySingleClick();

        if (!stage.hasMoreClicks())
            return;

        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scheduleClickReplay();
            }
        }, stage.getNextClickTimeStamp());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        game.batch.draw(mapTexture, 0, 0);
        game.batch.end();

        topBarController.drawBackground();
        stationController.renderConnections(map.getEnabledConnections(), Color.GRAY);

        if (gameLogic.getState() == GameState.ADDING_TRACK)
            stationController.renderConnections(map.getDisabledConnections(), Color.CYAN);

        if (gameLogic.getState() == GameState.PLACING_TRAIN
                || gameLogic.getState() == GameState.ROUTING)
            stationController.renderStationGoalHighlights();

        if (gameLogic.getState() == GameState.ROUTING) {
            routeController.drawRoute(Color.BLACK);
        } else {
            //Draw train moving
            if (gameLogic.getState() == GameState.ANIMATING) {
                timeAnimated += delta;
                if (timeAnimated >= ANIMATION_TIME) {
                    gameLogic.setState(GameState.NORMAL);
                    timeAnimated = 0;
                }
            }
        }

        //Draw the number of trains at each station
        if (gameLogic.getState() == GameState.NORMAL
                || gameLogic.getState() == GameState.PLACING_TRAIN)
            stationController.displayNumberOfTrainsAtStations();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        game.batch.begin();
        //If statement checks whether the turn is above 30, if it is then display 30 anyway
        game.fontSmall.draw(game.batch,
                "Turn " + ((gameLogic.getPlayerManager().getTurnNumber() +
                        1 < gameLogic.TOTAL_TURNS) ? gameLogic.getPlayerManager().getTurnNumber()
                        + 1 : gameLogic.TOTAL_TURNS) + "/" + gameLogic.TOTAL_TURNS,
                        (float) TaxeGame.WIDTH - 90.0f, 20.0f);
        game.batch.end();

        resourceController.drawHeaderText();
        goalController.drawHeaderText();
    }

    @Override
    // Called when GameScreen becomes current screen of the game
    public void show() {
        //We only render this once a turn, this allows the buttons generated to be clickable.
        //Initially some of this functionality was in the draw() routine, but
        //it was found that when the player clicked on a button a new one was
        //rendered before the input could be handled This is why the header
        // texts and the buttons are rendered separately, to prevent these
        // issues from occuring
        stationController.renderStations();
        topBarController.addEndTurnButton();
        goalController.showCurrentPlayerGoals();
        resourceController.drawPlayerResources(gameLogic.getPlayerManager().getActivePlayer());
    }

    @Override
    public void dispose() {
        mapTexture.dispose();
        stage.dispose();
    }

    protected void saveReplay() {
        if (replaying)
            return;

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Replay files", "json");
        chooser.setFileFilter(filter);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            stage.saveReplay(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}