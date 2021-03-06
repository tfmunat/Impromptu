package com.laserscorpion.impromptu;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class EventSearcher {
    //private EventSearcher es = this;
    private EventRequestReceiver listener;
    private Context context;

    public EventSearcher(EventRequestReceiver parent, Context context) {
        listener = parent;
        this.context = context;
    }



    public void search(LatLng location, int meters, Collection<String> keywords) {
        String baseURL = context.getString(R.string.server_base_url) + context.getString(R.string.search_url);
        String requestURL = baseURL + '/' + location.longitude + '/' + location.latitude + '/' + meters;
        if (keywords.size() > 0)
            requestURL += '/';
        for (String keyword : keywords) {
            requestURL += keyword + ',';
        }
        if (requestURL.charAt(requestURL.length() - 1) == ',')
            requestURL = requestURL.substring(0, requestURL.length() - 1);

        try {
            URL url = new URL(requestURL);
            HTTPRequester request = new HTTPRequester(url, listener);
            request.start();
        } catch (MalformedURLException e) {
            // not going to malform this
            e.printStackTrace();
        }
    }


    private class HTTPRequester extends Thread {
        private static final String TAG = "HTTPRequester";
        private String CHARSET = "UTF-8";
        private URL url;
        private EventRequestReceiver listener;

        public HTTPRequester(URL url, EventRequestReceiver listener) {
            this.url = url;
            this.listener = listener;
        }

        public void run() {
            try {
                Log.d(TAG, "Requesting " + url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Received " + responseCode);
                if (errorCode(responseCode)) {
                    listener.onRequestFailed("Received error " + responseCode);
                    return;
                }
                if (responseCode == 400) {
                    listener.onRequestFailed("No results found for that search");
                    return;
                }
                String body = readStream(connection.getInputStream());
                Log.d(TAG, "Received body: " + body);
                ArrayList<EventDetails> events = EventParser.parseEvents(context, body);
                listener.onEventsReceived(events);
                //connection.setRequestMethod("GET");
            } catch (MalformedURLException e) {
                // ignore this, i am forming it correctly
                e.printStackTrace();
            } catch (IOException e) {
                // internet connection, a true error
                e.printStackTrace();
                listener.onRequestFailed("Can't connect to server");
            } catch (JSONException e) {
                listener.onRequestFailed("Bad json received");
                e.printStackTrace();
            }
        }


        private boolean errorCode(int responseCode) {
            return responseCode > 400;
        }

        private String readStream(InputStream stream) throws IOException {
            InputStreamReader streamReader = new InputStreamReader(stream, CHARSET);
            BufferedReader reader = new BufferedReader(streamReader);
            String result = "";
            char buf[] = new char[100];
            int read = reader.read(buf, 0, buf.length);
            while (read >= 0) {
                String justRead = new String(buf, 0, read);
                //Log.d(TAG, "read: " + justRead);
                result += justRead;
                read = reader.read(buf, 0, buf.length);
            }
            return result;
        }
    }
}
