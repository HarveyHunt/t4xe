package fvs.taxe.replay;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import gameLogic.Game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * In LibGDX, a Stage is used for handling input and arranging actors to be
 * drawn (http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/Stage.html)
 *
 * We are only interested in messing with the input handling, so this class
 * implements recording of mouse clicks, as well as replaying them. Also in this
 * class is the code required to save a Replay to a json file and to load it
 * once again.
 */
public class ReplayStage extends Stage {
    private final int REPLAY_TIME_MULTIPLIER = 2;
    private boolean replaying = false;
    private Replay rep = new Replay(Game.getSeed());

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // If we are replaying and get a click from pointer 0 the user has
        // clicked something - ignore them so we don't break the replay.
        if (replaying && pointer == 0)
            return true;

        if (!replaying)
            // Set the pointer to 1 so that we know which clicks are from a
            // replay.
            rep.events.add(new ClickEvent(screenX, screenY,
                    1, button, System.currentTimeMillis()));

        return super.touchDown(screenX, screenY, pointer, button);
    }

    /**
     * Convert our Replay instance into a json string and fire it into a file.
     */
    public void saveReplay(String filepath) {
        Json json = new Json();
        // TODO: Consider a better place to put this.
        File file = new File(filepath);

        try {
            FileWriter writer = new FileWriter(file, false);
            PrintWriter output = new PrintWriter(writer);
            output.print(json.prettyPrint(rep));
            output.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Load a replay from a JSON file and (vitally) reseed Game's instance of
     * Random so that the game's events are deterministic.
     *
     * Due to the reseeding of Random, this method needs to be called as early
     * as possible.
     */
    public void loadReplay(String filepath) {
        Json json = new Json();
        Path path = Paths.get(filepath);

        try {
            String text = new String(Files.readAllBytes(path));
            rep = json.fromJson(Replay.class, text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Game.setSeed(rep.seed);
        replaying = true;
    }

    /**
     * Retrieve a single click from the replay and replay it. A single click event
     * is replayed as a mouse down and a subsequent mouse up.
     */
    public void replaySingleClick() {
        ClickEvent c = rep.events.get(0);
        // We can get away with reusing the same ClickEvent as we can assume
        // that a click up and down occur at the same location.
        touchDown(c.screenX, c.screenY, c.pointer, c.button);
        touchUp(c.screenX, c.screenY, c.pointer, c.button);
        rep.events.remove(0);

        // Give control back to the user so they can click the exit button.
        if (!hasMoreClicks()) {
            replaying = false;
        }
    }

    public long getNextClickTimeStamp() {
        return (rep.events.get(0).timestamp - rep.seed) / REPLAY_TIME_MULTIPLIER;
    }

    public boolean hasMoreClicks() {
        return !rep.events.isEmpty();
    }
}
