package com.laserscorpion.impromptu;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.AccessToken;
import com.facebook.Profile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LaunchAppRoutingTest {
    ActivityTestRule mActivityRule;

    public LaunchAppRoutingTest() {
        //super("com.laserscorpion.impromptu", LaunchAppRoutingActivity.class);
        mActivityRule = new ActivityTestRule<>(LaunchAppRoutingActivity.class, false, false);
    }

    @Before
    public void setUp() {
        FacebookResourceLocator.setToken( null );
        FacebookResourceLocator.setProfile( null );
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        prefs.edit().clear().commit();
    }

    @After
    public void tearDown() {
        FacebookResourceLocator.setToken( null );
        FacebookResourceLocator.setProfile( null );
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        prefs.edit().clear().commit();
    }

    /*@Test
    public void notLoggedInLaunchesFacebookLogin() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(FacebookSignupActivity.class.getName(), null, false);
        launchActivity();

        Activity nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(nextActivity);
        assertEquals(nextActivity.getClass(), FacebookSignupActivity.class);
        nextActivity.finish();
    }*/

    @Test
    public void loggedInLaunchesFindEvent() {
        FacebookResourceLocator.setToken( new AccessToken("mock", "mock", "mock", null, null, null, null, null) );
        FacebookResourceLocator.setProfile( new Profile("mock", null, null, null, null, null) );

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(FindEventActivity.class.getName(), null, false);
        launchActivity();

        Activity nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertNotNull(nextActivity);
        assertEquals(nextActivity.getClass(), FindEventActivity.class);
        nextActivity.finish();
    }

    private void launchActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        mActivityRule.launchActivity(intent);
    }

}