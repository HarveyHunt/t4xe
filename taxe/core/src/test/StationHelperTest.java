package test;

import gameLogic.map.Map;
import org.junit.Test;
import org.junit.Assert;

public class StationHelperTest {
    @Test
    public void testDoesConnectionExist() throws Exception {
        Map map = new Map();

        Assert.assertTrue(map.doesConnectionExist("Madrid", "Paris"));
        Assert.assertFalse(map.doesConnectionExist("London", "Paris"));
    }
}