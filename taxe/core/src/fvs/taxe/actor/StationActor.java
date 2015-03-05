package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

public class StationActor extends Image {
    private static Station station;
    private final Rectangle bounds;

    public StationActor(IPositionable location, Station stat) {
        super(new Texture(Gdx.files.internal("station_dot.png")));
        int width = 20;
        int height = 20;
        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
        bounds = new Rectangle();
        bounds.set(getX(), getY(), getWidth(), getHeight());
        station = stat;
    }

    public static Station getStation() {
        return station;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }
}
