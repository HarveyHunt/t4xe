package test;

import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import junit.framework.TestCase;
import org.junit.Test;

public class ResourceManagerTest extends TestCase {
    ResourceManager rm = new ResourceManager();

    @Test
    public void testGetRandomTrain() throws Exception {
        Train train1 = rm.getRandomTrain();
        assertNotNull(train1.toString());

        Train train2 = rm.getRandomTrain();
        assertNotNull(train2.toString());
    }

    @Test
    public void testAddRandomResourceToPlayer() throws Exception {
        PlayerManager playerManager = new PlayerManager();
        playerManager.createPlayers(2);
        Player player1 = playerManager.getActivePlayer();

        rm.addRandomResourceToPlayer(player1);
        rm.addRandomResourceToPlayer(player1);
        rm.addRandomResourceToPlayer(player1);

        assertEquals(3, player1.getResources().size());

    }
}