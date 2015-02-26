package gameLogic.resource;

import gameLogic.Disposable;
import gameLogic.player.Player;

public abstract class Resource implements Disposable {
    String name;
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isOwnedBy(Player player) {
        //Returns whether a given resource is owned by the player passed to the method
        return player == this.player;
    }

    @Override
    public String toString() {
        return name;
    }

    void changed() {
        player.changed();
    }

    public Player getPlayer() {
        return player;
    }
}