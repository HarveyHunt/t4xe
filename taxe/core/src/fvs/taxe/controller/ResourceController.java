package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import fvs.taxe.TaxeGame;
import fvs.taxe.clickListener.EngineerClicked;
import fvs.taxe.clickListener.ObstacleClicked;
import fvs.taxe.clickListener.SkipClicked;
import fvs.taxe.clickListener.TrainClicked;
import gameLogic.listeners.PlayerChangedListener;
import gameLogic.player.Player;
import gameLogic.resource.*;

import javax.xml.soap.Text;

public class ResourceController {
    private final Context context;
    private final Group resourceButtons = new Group();

    public ResourceController(final Context context) {
        this.context = context;
        //Subscribes to the listener so that the resources are redrawn whenever the player changes.
        context.getGameLogic().getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                drawPlayerResources(context.getGameLogic().getPlayerManager().getActivePlayer());
            }
        });
    }

    public void drawHeaderText() {
        TaxeGame game = context.getTaxeGame();

        game.batch.begin();
        game.fontSmall.setColor(Color.BLACK);
        game.fontSmall.draw(game.batch, "Unplaced Resources:", 10.0f, (float) TaxeGame.HEIGHT - 250.0f);
        game.batch.end();
    }

    public void drawPlayerResources(Player player) {
        float top = (float) TaxeGame.HEIGHT;
        float x = 10.0f;
        //The value of y is set based on how much space the header texts and
        //goals have taken up (assumed that 3 goals are always present for a
        //consistent interface)
        float y = top - 250.0f;
        y -= 50;
        resourceButtons.remove();
        resourceButtons.clear();

        for (final Resource resource : player.getResources()) {
            TextButton button = null;
            InputListener listener = null;

            if (resource instanceof Train) {
                Train train = (Train) resource;
                // Don't show a button for trains that have been placed,
                // trains placed are still part of the 7 total upgrades
                // If a train is not placed then its position is null so this is used to check
                if (!train.isPlaced()) {
                    listener = new TrainClicked(context, train);
                    button = new TextButton(resource.toString(), context.getSkin());
                }
            } else if (resource instanceof Obstacle) {
                listener = new ObstacleClicked(context, (Obstacle) resource);
                button = new TextButton("Obstacle", context.getSkin());
            } else if (resource instanceof Skip) {
                listener = new SkipClicked(context, (Skip) resource);
                button = new TextButton("Skip", context.getSkin());
            } else if (resource instanceof Engineer) {
                listener = new EngineerClicked(context, (Engineer) resource);
                button = new TextButton("Engineer", context.getSkin());
            }
            if (button != null) {
                button.setPosition(x, y);
                button.addListener(listener);
                resourceButtons.addActor(button);
                y -= 30;
            }
        }
        context.getStage().addActor(resourceButtons);
    }
}
