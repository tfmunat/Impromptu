package com.laserscorpion.impromptu;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by joel on 12/8/17.
 */
@RunWith(AndroidJUnit4.class)
public class EventSearchTest implements EventRequestReceiver {
    EventSearchTest tester = this;
    boolean resultReceived;
    boolean requestSucceeded;

    @Before
    public void setUp() {
        resultReceived = false;
        requestSucceeded = false;
    }

    @After
    public void tearDown() {
        resultReceived = false;
        requestSucceeded = false;
    }

    @Test
    public void badURLFailsTest() {
        String badURL = "http://www.totallyfakenotrealwebsite.fake/not-real-search";
        EventSearcher searcher = new EventSearcher(this, badURL);
        searcher.search(new LatLng(0.0, 0.0), 1, new ArrayList<String>());
        waitUninterruptibly();
        assertTrue(resultReceived);
        assertFalse(requestSucceeded);
    }

    @Test
    public void badURLWithKeywordsFailsTest() {
        String badURL = "http://www.totallyfakenotrealwebsite.fake/not-real-search";
        EventSearcher searcher = new EventSearcher(this, badURL);
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("stuff");
        keywords.add("things");
        searcher.search(new LatLng(0.0, 0.0), 1, keywords);
        waitUninterruptibly();
        assertTrue(resultReceived);
        assertFalse(requestSucceeded);
    }

    private synchronized void waitUninterruptibly() {
        while (true) {
            try {
                wait(5000);
                return;
            } catch (InterruptedException e) {  }
        }
    }


    @Override
    public synchronized void onEventsReceived(Collection<EventDetails> events) {
        resultReceived = true;
        requestSucceeded = true;
        notify();
    }

    @Override
    public synchronized void onRequestFailed(String reason) {
        resultReceived = true;
        requestSucceeded = false;
        notify();
    }
}
