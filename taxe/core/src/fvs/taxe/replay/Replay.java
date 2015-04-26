package fvs.taxe.replay;

import java.util.ArrayList;

public class Replay {
    public final long seed;
    public final ArrayList<ClickEvent> events = new ArrayList<ClickEvent>();

    public Replay(long seed) {
        this.seed = seed;
    }

    public Replay() {
        this.seed = 0;
    }
}
