package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.player.Player;
import gameLogic.resource.Train;

public class TrainActor extends Image {
    public static final int width = 36;
    public static final int height = 36;
    public final Train train;

    private final Rectangle bounds;
    private final Drawable leftDrawable;
    private final Drawable rightDrawable;
    private final Context context;
    private boolean facingLeft;
    private float previousX;
    private boolean paused;
    private boolean recentlyPaused;

    public TrainActor(Train train, Context context) {
        super(new Texture(Gdx.files.internal(train.getLeftImage())));

        facingLeft = true;
        paused = false;
        recentlyPaused = false;
        leftDrawable = getDrawable();
        rightDrawable = new Image(new Texture(Gdx.files.internal(train.getRightImage()))).getDrawable();
        this.context = context;

        IPositionable position = train.getPosition();

        train.setActor(this);
        this.train = train;
        setSize(width, height);
        bounds = new Rectangle();
        setPosition(position.getX() - width / 2, position.getY() - height / 2);
        previousX = getX();
    }

    @Override
    public void act(float delta) {
        //This function moves the train actors along their routes.
        //It renders everything every 1/delta seconds
        if ((Game.getInstance().getState() == GameState.ANIMATING)
                && (!this.paused)) {
            super.act(delta);
            updateBounds();
            updateFacingDirection();

            Train collision = collided();
            if (collision != null) {
                context.getTopBarController().displayFlashMessage("Two trains collided.  They were both destroyed.", Color.RED, 2);
                Game.getInstance().getMap().blockConnection(train.getLastStation(), train.getNextStation(), 5);
                collision.getActor().remove();
                collision.getPlayer().removeResource(collision);
                train.getPlayer().removeResource(train);
                this.remove();
            }

        } else if (this.paused) {
            //This ensures that trains do not move through blocked connections when they are not supposed to.
            Station station = train.getHistory().get(train.getHistory().size() - 1).getFirst();
            int index = train.getRoute().indexOf(station);
            Station nextStation = train.getRoute().get(index + 1);
            if (!Game.getInstance().getMap().isConnectionBlocked(station, nextStation)) {
                this.paused = false;
                this.recentlyPaused = true;
            }
        }
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    void updateFacingDirection() {
        float currentX = getX();
        if (facingLeft && previousX < currentX) {
            setDrawable(rightDrawable);
            facingLeft = false;
        } else if (!facingLeft && previousX > currentX) {
            setDrawable(leftDrawable);
            facingLeft = true;
        }
        previousX = getX();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean getPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    boolean isRecentlyPaused() {
        return recentlyPaused;
    }

    public void setRecentlyPaused(boolean recentlyPaused) {
        this.recentlyPaused = recentlyPaused;
    }

    Train collided() {
        Station last = train.getLastStation();
        Station next = train.getNextStation();

        if (paused)
            return null;
        // If the train's location is -1, -1 then it is moving. Whoever designed this is an architectural genius...
        if (train.getPosition().getX() == -1) {
            for (Player player : Game.getInstance().getPlayerManager().getAllPlayers()) {
                // I wish java had filter...
                for (Train otherTrain : player.getTrains()) {
                    if (otherTrain.equals(train)
                            || !otherTrain.isCollidable()
                            || !otherTrain.isOnCollisionCourse(next, last))
                        continue;
                    // If trains have been recently paused, we can run into an issue of stopped trains colliding.
                    if ((this.bounds.overlaps(otherTrain.getActor().getBounds()))
                            && !((this.recentlyPaused)
                            || (otherTrain.getActor().isRecentlyPaused()))) {
                        return otherTrain;
                    }
                }
            }
        }
        return null;
    }
}