package fvs.taxe.replay;

import java.util.List;

public class Replay {
    public final long seed;
    public final List<ClickEvent> clicks;

    public Replay(long seed, List<ClickEvent> clicks) {
        this.seed = seed;
        this.clicks = clicks;
    }
}
