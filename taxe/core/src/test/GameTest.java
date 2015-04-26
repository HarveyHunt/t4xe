package test;

import gameLogic.Game;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GameTest extends LibGdxTest {
    private PlayerManager pm;

    @Before
    public void setUpGame() throws Exception {
        Game game = Game.getInstance();
        game.getPlayerManager();
        pm = game.getPlayerManager();
    }

    @Test
    public void testInitialisePlayers() {
        Player currentPlayer = pm.getCurrentPlayer();

        assertTrue("Player starting with more than 0 resources", currentPlayer.getResources().size() == 0);
        assertTrue("Player starting with more than 0 goals", currentPlayer.getGoals().size() == 0);
    }

    @Test
    public void testPlayerChanged() throws Exception {
        Player p1 = pm.getCurrentPlayer();
        int resourceCount = p1.getResources().size();
        int goalCount = p1.getGoals().size();

        pm.turnOver(null);
        pm.turnOver(null);

        assertTrue("No. resources did not increase", p1.getResources().size() > resourceCount);
        assertTrue("No. goals did not increase", p1.getGoals().size() > goalCount);
    }
}