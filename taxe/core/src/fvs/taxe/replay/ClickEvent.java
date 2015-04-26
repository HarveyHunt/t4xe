package fvs.taxe.replay;

public class ClickEvent {
    public final int screenX;
    public final int screenY;
    public final int pointer;
    public final int button;
    public final long timestamp;

    public ClickEvent(int screenX, int screenY, int pointer, int button, long timestamp) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.pointer = pointer;
        this.button = button;
        this.timestamp = timestamp;
    }

    public ClickEvent() {
        screenX = 0;
        screenY = 0;
        pointer = 0;
        button = 0;
        timestamp = 0;
    }

    @Override
    public String toString() {
        return "ClickEvent{" +
                "screenX=" + screenX +
                ", screenY=" + screenY +
                ", pointer=" + pointer +
                ", button=" + button +
                ", timestamp=" + timestamp +
                '}';
    }
}
