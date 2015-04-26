package fvs.taxe;

public class ReplayGameScreen extends GameScreen {

    public ReplayGameScreen(TaxeGame game, String filepath) {
        super(game);
        this.stage.replay(filepath);
    }
}
