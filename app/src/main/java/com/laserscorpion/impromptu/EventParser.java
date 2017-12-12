package com.laserscorpion.impromptu;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class EventParser {
    private static final String TAG = "EventParser";

    public static ArrayList<EventDetails> parseEvents(Context context, String response) throws JSONException {
        ArrayList<EventDetails> result = new ArrayList<>();
        JSONArray json = new JSONArray(response);
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            EventDetails event = new EventDetails();
            event.owner = obj.getString("owner");
            event.description = obj.getString("description");
            event.ownerName = obj.getString("owner_name");
            event.title = obj.getString("title");
            event.category = obj.getString("category");
            event.id = obj.getString("_id");
            String placeID = obj.getString("place_id");
            GeoDataClient client = Places.getGeoDataClient(context, null);

            Task<PlaceBufferResponse> someTask = client.getPlaceById(placeID);
            while (!someTask.isComplete()) {} // spinlock because this is already a background thread
            if (!someTask.isSuccessful()) {
                Log.e(TAG, "possibly bad place id received, or sth: " + placeID);
                continue;
            }
            Place place;
            try {
                place = someTask.getResult().get(0);
            } catch (Exception e) {
                Log.e(TAG, "bad place id received: " + placeID);
                continue;
            }
            if (place == null) {
                Log.e(TAG, "bad place id received: " + placeID);
                continue;
            }
            event.place = place;

            JSONArray attendees = obj.getJSONArray("attendees");
            event.attendees = new ArrayList<>();
            for (int j = 0; j < attendees.length(); j++) {
                String name = attendees.getString(j);
                event.attendees.add(name);
            }

            event.time = new Date(obj.getLong("time"));

            result.add(event);
        }
        return result;
    }

}
