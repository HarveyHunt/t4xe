package test;

import gameLogic.Game;
import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import junit.framework.TestCase;
import org.junit.Before;

public class GameTest extends TestCase {
    private PlayerManager pm;

    @Before
    public void setUpGame() throws Exception {
        Game game = Game.getInstance();
        game.getPlayerManager();
        pm = game.getPlayerManager();
    }

    public void testInitialisePlayers() {
        Player currentPlayer = pm.getActivePlayer();

        assertTrue("Player not starting with 2 resources", currentPlayer.getResources().size() == 0);
        assertTrue("Player starting with more than 0 goals", currentPlayer.getGoals().size() == 0);
    }

    public void testPlayerChanged() throws Exception {
        Player p1 = pm.getActivePlayer();
        int resourceCount = p1.getResources().size();
        int goalCount = p1.getGoals().size();

        pm.turnOver(null);
        pm.turnOver(null);

        assertTrue("No. resources did not increase", p1.getResources().size() > resourceCount);
        assertTrue("No. goals did not increase", p1.getGoals().size() > goalCount);
    }
}