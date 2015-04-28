package test;

import gameLogic.player.Player;
import gameLogic.player.PlayerManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import org.junit.Test;
import org.junit.Assert;

public class ResourceManagerTest {
    ResourceManager rm = new ResourceManager();

    @Test
    public void testGetRandomTrain() throws Exception {
        Train train1 = rm.getRandomTrain();
        Assert.assertNotNull(train1.toString());

        Train train2 = rm.getRandomTrain();
        Assert.assertNotNull(train2.toString());
    }

    @Test
    public void testAddRandomResourceToPlayer() throws Exception {
        PlayerManager playerManager = new PlayerManager();
        playerManager.createPlayers(2);
        Player player1 = playerManager.getActivePlayer();

        rm.addRandomResourceToPlayer(player1);
        rm.addRandomResourceToPlayer(player1);
        rm.addRandomResourceToPlayer(player1);

        Assert.assertEquals(3, player1.getResources().size());

    }
}