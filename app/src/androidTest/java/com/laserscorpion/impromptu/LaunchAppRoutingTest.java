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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class LaunchAppRoutingTest extends LaunchAppRoutingActivity {
    ActivityTestRule mActivityRule;

    public LaunchAppRoutingTest() {
        //super("com.laserscorpion.impromptu", LaunchAppRoutingActivity.class);
        mActivityRule = new ActivityTestRule<>(LaunchAppRoutingTest.class, false, false);
    }

    @Before
    public void setUp() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().clear().commit();
    }

    @Override
    protected void populateFacebookTokens() {

    }

    @Test
    public void notLoggedInLaunchesFacebookLogin() {
        token = null;
        profile = null;
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(FacebookSignupActivity.class.getName(), null, false);

        launchActivity();

        Activity nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertEquals(nextActivity.getClass(), FacebookSignupActivity.class);
        nextActivity.finish();
    }

    @Test
    public void loggedInLaunchesFindEvent() {
        token = new AccessToken("mock", "mock", "mock", null, null, null, null, null);
        profile = new Profile("mock", null, null, null, null, null);
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(FindEventActivity.class.getName(), null, false);

        launchActivity();

        Activity nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        assertEquals(nextActivity.getClass(), FindEventActivity.class);
        nextActivity.finish();
    }

    private void launchActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                mActivityRule.launchActivity(intent);
            }
        });
    }

}