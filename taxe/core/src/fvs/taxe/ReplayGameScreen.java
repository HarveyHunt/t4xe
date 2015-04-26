package fvs.taxe;

public class ReplayGameScreen extends GameScreen {

    private final String filepath;

    public ReplayGameScreen(TaxeGame game, String filepath) {
        super(game);
        this.filepath = filepath;
    }

    public void startReplay() {
        this.stage.replay(filepath);
    }
}
