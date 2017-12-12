package com.laserscorpion.impromptu;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JoinEventActivity extends FragmentActivity {

    private JoinEventActivity activity = this;
    //Collection<EventDetails> events;
    public String id;
    //private ArrayAdapter<String> adapter;
    //private ArrayList<String> e_title;
    public String user_id;
    private String time;
    private String host;
    private ArrayList<String> attendees;
    private String title;
    private String description;
    private String category;
    private String venue;
    private String eventID;
    private static final String TAG = "CreateEventActivity";
    private static final String USER_ID = "impromptu_user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
        Intent creationIntent = getIntent();
        host = creationIntent.getStringExtra("event_host");
        attendees = creationIntent.getStringArrayListExtra("event_attendees");
        time = creationIntent.getStringExtra("event_time");
        title = creationIntent.getStringExtra("event_title");
        description = creationIntent.getStringExtra("event_description");
        category = creationIntent.getStringExtra("event_category");
        venue = creationIntent.getStringExtra("event_venue");
        eventID = creationIntent.getStringExtra("event_id");

        TextView titleView = (TextView)findViewById(R.id.event_title);
        TextView timeView = (TextView)findViewById(R.id.event_time);
        TextView ownerView = (TextView)findViewById(R.id.event_host);
        TextView descView = (TextView)findViewById(R.id.event_description);
        TextView locationView = (TextView)findViewById(R.id.event_venue);
        TextView attendeeView = (TextView)findViewById(R.id.attendee_list);

        titleView.setText("Event: " + title);
        timeView.setText("Time: " + time);
        ownerView.setText("Host: " + host);
        descView.setText("Description: " + description);
        locationView.setText("Location: " + venue);
        String attendeeList = "Attendees: ";
        for (String s : attendees) {
            attendeeList += s + ", ";
        }
        attendeeView.setText(attendeeList);
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
                            //eventID = json.getString("id");
                            //event_id = eventID;
                            String toastMsg = "Success!";
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
            eventDetails.put("event_id", eventID);
            eventDetails.put("user_id", user_id);
            Log.d(TAG, eventDetails.toString());
        } catch (JSONException e) {
            return null;
        }
        return eventDetails;
    }
}
