package com.example.caseydenner.interactiveroom;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.example.caseydenner.interactiveroom.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentationTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void selectBeach(){
        onView(withId(R.id.radioBeach)).perform(click());
        onView(withId(R.id.buttonGo)).perform(click());
    }

    @Test
    public void selectReminiscence(){
        onView(withId(R.id.radioReminisce)).perform(click());
        onView(withId(R.id.buttonGo)).perform(click());
    }
}