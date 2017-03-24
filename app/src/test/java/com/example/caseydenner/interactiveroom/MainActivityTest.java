package com.example.caseydenner.interactiveroom;

/**
 * Created by caseydenner on 07/02/2017.
 */

import android.content.Intent;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * Testing class for MainActivity
 */
public class MainActivityTest {

    @Mock
    MainActivity mainActivity = new MainActivity();

    private static final String EXTRA_ADDRESS = "address";

    @Test
    public void addressTest() {
        String test = "test";
        mainActivity.setAddress(test);
        assertEquals(mainActivity.getAddress(), test);
    }

//    @Test
//    public void testIntentCreated(){
//        Intent intent = new Intent(mainActivity, BeachActivity.class);
//        assertNotNull(intent);
//        intent.putExtra(EXTRA_ADDRESS, "test");
//        assertEquals("test", intent.getStringExtra(EXTRA_ADDRESS));
//    }
}
