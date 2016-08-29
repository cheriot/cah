package com.cheriot.horriblecards.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by cheriot on 8/30/16.
 */
public class RandomNamerTest {

    private RandomNamer mRandomNamer;

    @Before
    public void setUp() {
        mRandomNamer = new RandomNamer();
    }

    @Test
    public void randomName() {
        String name = mRandomNamer.randomName();
        assertNotNull("Returns a name.", name);
        assertTrue("Not empty.", !name.isEmpty());
    }
}
