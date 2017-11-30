package com.laserscorpion.impromptu;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class JoinEventActivity extends FragmentActivity {

    private JoinEventActivity activity = this;
    public String id;
    public String owner;
    public String ownerName;
    public String description;
    public String title;
    public String category;
    public Place place;
    public ArrayList<String> attendees;
    public Date time;
    private String event_id, eventID, user_id;
    private static final String TAG = "CreateEventActivity";
    private static final String USER_ID = "impromptu_user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
    }

    /* join when the button is clicked */
    public void joinEvent(View v){
        JSONObject eventDetails = getEventDetails();
        sendEventJoinDetails(eventDetails);
    }

    private void sendEventJoinDetails(final JSONObject event_info){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_base_url) + getString(R.string.join_url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "cool, here's the response \n" + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            eventID = json.getString("id");
                            event_id = eventID;
                            String toastMsg = String.format("Success!");
                            Toast.makeText(activity, toastMsg, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            ErrorDialog dialog = ErrorDialog.newInstance("Bad JSON received from server, can't create event");
                            dialog.show(getFragmentManager(), "CreateError");
                            e.printStackTrace();
                        }
                        activity.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                ErrorDialog dialog = ErrorDialog.newInstance(error.toString());
                dialog.show(getFragmentManager(), "CreateError");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError
            {
                try {
                    String eventString = event_info.toString();
                    return eventString.getBytes("UTF-8");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new AuthFailureError("uh oh", e);
                }
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private JSONObject getEventDetails(){
        JSONObject eventDetails = new JSONObject();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id  = prefs.getString(USER_ID, null);
        user_id = id;
        if (id == null) {
            // bad
            ErrorDialog dialog = ErrorDialog.newInstance("Missing user ID, can't join event");
            dialog.show(getFragmentManager(), "CreateError");
            Log.e(TAG, "don't have id?!?!");
            return null;
        }

        // populate eventDetails
        try {
            eventDetails.put("event_id", event_id);
            eventDetails.put("user_id", user_id);
            Log.d(TAG, eventDetails.toString());
        } catch (JSONException e) {
            return null;
        }
        return eventDetails;
    }
}
