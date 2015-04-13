package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.TaxeGame;
import fvs.taxe.replay.ReplayStage;
import gameLogic.Game;

public class Context {
    // Context appears to be a class that allows different aspects of the system access parts that they otherwise
    // logically shouldn't have access to. While this is a bit of a workaround to make implementation easier,
    // it does weaken encapsulation somewhat, however a full system overhaul would be unfeasible to remedy this.
    private final TaxeGame taxeGame;
    private final ReplayStage stage;
    private final Skin skin;
    private final Game gameLogic;
    private RouteController routeController;
    private TopBarController topBarController;

    public Context(ReplayStage stage, Skin skin, TaxeGame taxeGame, Game gameLogic) {
        this.stage = stage;
        this.skin = skin;
        this.taxeGame = taxeGame;
        this.gameLogic = gameLogic;
    }

    //Getters and setters: pretty self-explanatory
    public ReplayStage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public TaxeGame getTaxeGame() {
        return taxeGame;
    }

    public Game getGameLogic() {
        return gameLogic;
    }

    public RouteController getRouteController() {
        return routeController;
    }

    public void setRouteController(RouteController routeController) {
        this.routeController = routeController;
    }

    public TopBarController getTopBarController() {
        return topBarController;
    }

    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
    }
}
