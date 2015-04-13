package fvs.taxe.replay;

public class ClickEvent {
    public final int screenX;
    public final int screenY;
    public final int pointer;
    public final int button;

    public ClickEvent(int screenX, int screenY, int pointer, int button) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.pointer = pointer;
        this.button = button;
    }
}
