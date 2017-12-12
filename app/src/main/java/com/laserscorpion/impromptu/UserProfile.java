package com.laserscorpion.impromptu;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserProfile extends ListActivity {
    private com.laserscorpion.impromptu.UserProfile activity = this;
    public String id;
    public String user_id;
    private EventRequestReceiver listener;
    private Context context;
    private ArrayList<EventDetails> events;
    private static final String TAG = "UserProfile";
    private static final String USER_ID = "impromptu_user_id";
    private ArrayAdapter<EventDetails> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = this;
        events = new ArrayList<>();

        adapter = new ArrayAdapter(this, R.layout.list_item_2, events);
        setListAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
        showUserInfo();
    }

    public void showUserInfo(){
        JSONObject userID = getUserID();
        retrieveUserDetails(userID);
    }

    private JSONObject getUserID(){
        JSONObject userDetails = new JSONObject();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id  = prefs.getString(USER_ID, null);
        user_id = id;
        if (id == null) {
            // bad
            ErrorDialog dialog = ErrorDialog.newInstance("Missing user ID, can't retrieve profile");
            dialog.show(getFragmentManager(), "ViewProfileError");
            Log.e(TAG, "don't have id?!?!");
            return null;
        }
        // populate eventDetails
        try {
            userDetails.put("user_id", user_id);
            Log.d(TAG, userDetails.toString());
        } catch (JSONException e) {
            return null;
        }
        return userDetails;
    }

    private void displayEvents() {
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }

    private void retrieveUserDetails(final JSONObject userDetails){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_base_url) + getString(R.string.get_user_url) + user_id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "cool, here's the response \n" + response);
                        try {
                            events = EventParser.parseEvents(context, response);
                            displayEvents();
                        } catch (JSONException e) {
                            ErrorDialog dialog = ErrorDialog.newInstance("Bad JSON received from server, can't create event");
                            dialog.show(getFragmentManager(), "ViewProfileError");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                ErrorDialog dialog = ErrorDialog.newInstance(error.toString());
                dialog.show(getFragmentManager(), "ViewProfileError");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError
            {
                try {
                    String eventString = userDetails.toString();
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
}