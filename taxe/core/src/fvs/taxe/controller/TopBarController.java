package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.TaxeGame;
import gameLogic.GameState;
import gameLogic.listeners.GameStateListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TopBarController {
    //This class controls what is displayed in the topBar, the primary method of informing the players of events that occur in game
    //It's very possible to move away from a topBar orientated design and more to dialogs as we have done, but we decided not to entirely due to the work required.
    public final static int CONTROLS_HEIGHT = 40;

    private final Context context;
    private Color controlsColor = Color.LIGHT_GRAY;
    private TextButton endTurnButton;
    private TextButton replayButton;
    private Label flashMessage;

    public TopBarController(Context context) {
        this.context = context;
        //This creates a listener that changes the bar colour based on the state that the game is in
        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if (state == GameState.ANIMATING)
                    controlsColor = Color.GREEN;
                else
                    controlsColor = Color.LIGHT_GRAY;
            }
        });
        createFlashActor();
    }

    private void createFlashActor() {
        flashMessage = new Label("", context.getSkin());
        flashMessage.setPosition(400, TaxeGame.HEIGHT - 24);
        context.getStage().addActor(flashMessage);
    }

    public void displayFlashMessage(String message, Color color) {
        displayFlashMessage(message, color, 1.75f);
    }

    public void displayFlashMessage(String message, Color color, float time) {
        flashMessage.setText(message);
        flashMessage.setColor(color);
        flashMessage.addAction(sequence(delay(time), fadeOut(0.25f)));
    }

    public void displayMessage(String message, Color color) {
        flashMessage.setText(message);
        flashMessage.setColor(color);
    }

    public void clearMessage() {
        flashMessage.setText("");
        flashMessage.setColor(Color.LIGHT_GRAY);
    }

    public void drawBackground() {
        TaxeGame game = context.getTaxeGame();
        //This method draws the topBar onto the game screen
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(controlsColor);
        game.shapeRenderer.rect(0, TaxeGame.HEIGHT - CONTROLS_HEIGHT, TaxeGame.WIDTH, CONTROLS_HEIGHT);
        game.shapeRenderer.setColor(Color.BLACK);
        game.shapeRenderer.rect(0, TaxeGame.HEIGHT - CONTROLS_HEIGHT, TaxeGame.WIDTH, 1);
        game.shapeRenderer.end();
    }

    public void addEndTurnButton() {
        endTurnButton = new TextButton("End Turn", context.getSkin());
        endTurnButton.setPosition(TaxeGame.WIDTH - 100.0f, TaxeGame.HEIGHT - 33.0f);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //This sets the turn to be over in the backend
                context.getGameLogic().getPlayerManager().turnOver(context);
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                //This sets whether or not the endTurn button is displayed based on the state of the game
                //This is important as it prevents players from ending their turn mid placement or mid routing
                if (state == GameState.NORMAL)
                    endTurnButton.setVisible(true);
                else
                    endTurnButton.setVisible(false);
            }
        });
        context.getStage().addActor(endTurnButton);
    }

    public void addReplayButton() {
        replayButton = new TextButton("Replay", context.getSkin());
        replayButton.setPosition(TaxeGame.WIDTH - 200.0f, TaxeGame.HEIGHT - 33.0f);
        replayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //This sets the turn to be over in the backend
                context.getStage().replay();
            }
        });

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                //This sets whether or not the endTurn button is displayed based on the state of the game
                //This is important as it prevents players from ending their turn mid placement or mid routing
                if (state == GameState.NORMAL)
                    replayButton.setVisible(true);
                else
                    replayButton.setVisible(false);
            }
        });
        context.getStage().addActor(replayButton);
    }
}
