package test;

import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerManagerTest {
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