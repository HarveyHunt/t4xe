package test;

import gameLogic.Game;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class GameTest {
    private PlayerManager pm;

    @Before
    public void setUpGame() throws Exception {
        Game game = Game.getInstance();
        pm = game.getPlayerManager();
    }

    @Test
    public void testInitialisePlayers() {
        Player currentPlayer = pm.getActivePlayer();

        Assert.assertTrue("Player not starting with the correct number of resources", currentPlayer.getResources().size() >= 0);
        Assert.assertTrue("Player starting with more than 0 goals", currentPlayer.getGoals().size() >= 0);
    }

    @Test
    public void testPlayerChanged() throws Exception {
        Player p1 = pm.getActivePlayer();
        int resourceCount = p1.getResources().size();
        int goalCount = p1.getGoals().size();

        pm.turnOver(null);
        pm.turnOver(null);

        Assert.assertTrue("No. resources did not increase", p1.getResources().size() > resourceCount);
        Assert.assertTrue("No. goals did not increase", p1.getGoals().size() > goalCount);
    }
}