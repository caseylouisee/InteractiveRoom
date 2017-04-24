package com.example.caseydenner.interactiveroom;

/**
 * Created by caseydenner on 07/02/2017.
 */

import android.content.Intent;

import android.test.ActivityInstrumentationTestCase2;
import android.test.mock.MockContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;


/**
 * Testing class for MainActivity
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2 {



    @Mock
    MainActivity mainActivity = new MainActivity();

    private static final String EXTRA_ADDRESS = "address";

    public MainActivityTest(String pkg, Class activityClass) {
        super(pkg, activityClass);
    }

    @Test
    public void addressTest() {
        String test = "test";
        mainActivity.setAddress(test);
        assertEquals(mainActivity.getAddress(), test);

    }

    @Test
    public void testBeach(){
        mainActivity.findViewById(R.id.radioBeach);
    }

//    @Test
//    public void testIntentCreated(){
//        Intent intent = new Intent(mainActivity, BeachActivity.class);
//        assertNotNull(intent);
//        intent.putExtra(EXTRA_ADDRESS, "test");
//        assertEquals("test", intent.getStringExtra(EXTRA_ADDRESS));
//    }
}
