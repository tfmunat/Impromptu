package com.laserscorpion.impromptu;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityUnitTestCase;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class CreateEventActivityTest  {
    ActivityTestRule mActivityRule;
    CreateEventActivity activity;

    public CreateEventActivityTest() {
        mActivityRule = new ActivityTestRule(CreateEventActivity.class, false, false);
    }

    @Before
    public void setUp() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(CreateEventActivity.class.getName(), null, false);
        launchActivity();
        activity = (CreateEventActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

       // activity = (CreateEventActivity) getActivity();
    }

    @After
    public void tearDown() {
        //activity.finish();
        activity = null;
    }

    /*@Test
    public void badURLFailsTest() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(FindEventActivity.class.getName(), null, false);
        launchActivity();
        CreateEventActivity nextActivity = (CreateEventActivity)getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        String badURL = "http://www.totallyfakenotrealwebsite.fake/not-real-create-event";

        nextActivity.setURL(badURL);

    }*/

    @Test
    public void noIDSelectedFailsTest() {
        /*Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(FindEventActivity.class.getName(), null, false);
        launchActivity();
        CreateEventActivity nextActivity = (CreateEventActivity)getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);*/

        activity.sendEventToDatabase(null);
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {}*/
        Fragment errorDialogFragment = activity.getFragmentManager().findFragmentByTag("CreateError4");
        assertNotNull(errorDialogFragment);
    }

    private void launchActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        mActivityRule.launchActivity(intent);
    }


}
