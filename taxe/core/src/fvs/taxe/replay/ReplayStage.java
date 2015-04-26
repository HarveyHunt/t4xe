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

    public void saveReplay() {
        if (rep.events.isEmpty())
            return;

        Json json = new Json();
        // TODO: Consider a better place to put this.
        String filename = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()) + ".json";
        File file = new File(filename);

        try {
            FileWriter writer = new FileWriter(file, true);
            PrintWriter output = new PrintWriter(writer);
            output.print(json.prettyPrint(rep));
            output.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadReplay(String filepath) {
        Json json = new Json();
        Path path = Paths.get(filepath);

        try {
            String text = new String(Files.readAllBytes(path));
            rep = json.fromJson(Replay.class, text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        replaying = true;
    }

    public void replaySingleClick() {
        Game.setSeed(rep.seed);
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
