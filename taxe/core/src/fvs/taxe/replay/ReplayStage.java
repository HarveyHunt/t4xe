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
    private boolean replaying = false;
    private Replay rep = new Replay(Game.getSeed());

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // If we are replaying and get a click from pointer 0 the user has
        // clicked something - ignore them so we don't break the replay.
        if (replaying && pointer == 0)
            return false;

        if (!replaying)
            // Set the pointer to 100 so that we know which clicks are from a
            // replay.
            rep.events.add(new ClickEvent(screenX, screenY,
                    1, button, System.currentTimeMillis()));

        return super.touchDown(screenX, screenY, pointer, button);
    }

    public void saveReplay() {
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

    private Replay loadReplay(String filepath) {
        Json json = new Json();
        Path path = Paths.get(filepath);

        try {
            String text = new String(Files.readAllBytes(path));
            return json.fromJson(Replay.class, text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void replay(String filepath) {
        rep = loadReplay(filepath);
        Game.setSeed(rep.seed);

        replaying = true;
        // Don't replay the final replay click as we could end up in a loop.
        for (ClickEvent c : rep.events.subList(0, rep.events.size() - 1)) {
            // We can get away with reusing the same ClickEvent as we can assume
            // that a click up and down occur at the same location.
            touchDown(c.screenX, c.screenY, c.pointer, c.button);
            touchUp(c.screenX, c.screenY, c.pointer, c.button);

            try {
                /*
                rep.seed is the time the replay was recorded at. Taking this
                away from the timestamp gives us an offset we can use for replay.
                */
                Thread.sleep(c.timestamp - rep.seed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        replaying = false;
    }
}
