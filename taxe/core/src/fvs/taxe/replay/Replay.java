package fvs.taxe.replay;

import java.util.ArrayList;

/**
 * This class represents a replay - whether that be a complete replay that
 * has just been loaded from a file, or a replay that is being recorded currently.
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
