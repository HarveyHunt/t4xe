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
    public void testAddRandomResourceToPlayer() {
        PlayerManager playerManager = new PlayerManager();
        playerManager.createPlayers(1);
        Player player1 = playerManager.getActivePlayer();

        for (int i = 0; i < rm.CONFIG_MAX_RESOURCES - 1; i++){
            rm.addRandomResourceToPlayer(player1);
        }

        Assert.assertEquals(rm.CONFIG_MAX_RESOURCES - 1, player1.getResources().size());
    }

    @Test
    public void testAddTooManyRandomResourceToPlayer() {
        PlayerManager playerManager = new PlayerManager();
        playerManager.createPlayers(1);
        Player player1 = playerManager.getActivePlayer();

        for (int i = 0; i < rm.CONFIG_MAX_RESOURCES + 5; i++){
            rm.addRandomResourceToPlayer(player1);
        }

        Assert.assertEquals(rm.CONFIG_MAX_RESOURCES, player1.getResources().size());
    }
}