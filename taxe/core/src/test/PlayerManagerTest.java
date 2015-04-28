package test;

import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class PlayerManagerTest extends TestCase {
    private PlayerManager pm;

    @Before
    public void setUp() throws Exception {
        pm = new PlayerManager();
        pm.createPlayers(2);
    }

    @Test
    public void testGetCurrentPlayer() throws Exception {
        Player p1 = pm.getActivePlayer();
        pm.turnOver(null);

        // player should change after PlayerManager.turnOver() is called
        assertFalse("Active Player did not change", p1.equals(pm.getActivePlayer()));
    }

    @Test
    public void testTurnNumber() throws Exception {
        int previous = pm.getTurnNumber();
        pm.turnOver(null);

        assertTrue("Turn number did not change", previous == pm.getTurnNumber() - 1);
    }


}