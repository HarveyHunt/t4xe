package test;

import gameLogic.map.Map;
import junit.framework.TestCase;
import org.junit.Test;

public class StationHelperTest extends TestCase {
    public void testDoesConnectionExist() throws Exception {
        Map map = new Map();

        assertTrue(map.doesConnectionExist("Madrid", "Paris"));
        assertFalse(map.doesConnectionExist("London", "Paris"));
    }
}