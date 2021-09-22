package com.isoterik.racken.test;

import com.isoterik.racken.util.GameWorldUnits;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameWorldUnitsTest {
    GameWorldUnits gameWorldUnits = new GameWorldUnits(100, 100, 64);

    @Test
    public void testToWorldUnits() {
        assertEquals(1, gameWorldUnits.toWorldUnit(64));
    }

    @Test
    public void testToPixelUnits() {
        assertEquals(64, gameWorldUnits.toPixels(1));
    }
}