package gameLogic.listeners;

import gameLogic.GameState;

public interface GameStateListener {
    void changed(GameState state);
}
