package fvs.taxe.replay;

import java.util.ArrayList;

/**
 * This class represents a replay - whether that be a complete replay that
 * has just been loaded from a file, or a replay that is being recorded currently.
 *
 * ********************************
 * The way our replay system works is as follows:
 * - We provide an instance of Random that anything in the program can use. This
 *      instance is seeded with a known seed (the current system time). This
 *      is accessed using game.getConsistentRandom()
 * - We record every mouseclick that the user makes, along with a timestamp.
 * - We then save the mouseclicks and the seed into a JSON file.
 *
 * When we want to replay a previous game, we load the replay from a file.
 * - We get the seed from the JSON file and reseed our instance of Random to
 *      ensure that random events are the same as last time.
 * - We ignore any clicks from the user (once the replay ends we let the user
 *      click things again so that they can exit the game).
 * - We create a timer that waits for the difference in timestamps between two
 *      mouseclicks. Once the timer is activated, it pulls a mouse click out of
 *      the Replay, replays it and then sets another timer to repeat the process.
 * ********************************
 */
public class Replay {
    public final long seed;
    public final ArrayList<ClickEvent> events = new ArrayList<ClickEvent>();

    public Replay(long seed) {
        this.seed = seed;
    }

    /**
     * This is necessary to keep LibGDX's JSON parser happy - without a noarg
     * constructor it has no idea what it is doing...
     */
    public Replay() {
        this.seed = 0;
    }
}
