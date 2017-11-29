package com.laserscorpion.impromptu;

import android.app.DialogFragment;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Profile;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.*;

public class CreateEventActivity extends FragmentActivity {

    String title, description, category, time;
    private EditText e_title = (EditText)findViewById(R.id.event_title);
    private EditText e_desc = (EditText)findViewById(R.id.event_desc);
    private EditText e_category = (EditText)findViewById(R.id.event_category);
    View mapView;
    private GoogleMap mMap;
    private int[] unixTime;
    private LocationManager locationManager;
    private static final String TAG = "FindCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
    }

    /**
     * Submit the new event details to the server
     * @param view the create button that was clicked
     */
    public void sendEventToDatabase(View view) {
        title = e_title.getText().toString();
        description = e_desc.getText().toString();
        category = e_category.getText().toString();
        JSONObject eventDetails = getEventDetails();
        sendEventRegistrationDetails(eventDetails);
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, FindEventActivity.class);
        startActivity(intent);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void sendEventRegistrationDetails(final JSONObject eventInfo) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_base_url) + "/create";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "here's the response \n" + response);
                        startMapActivity();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                ErrorDialog dialog = ErrorDialog.newInstance(error.getMessage());
                dialog.show(getFragmentManager(), "CreateError");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError
            {
                try {
                    String eventString = eventInfo.toString();
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

    private JSONObject getEventDetails() {
        JSONObject eventDetails = new JSONObject();
        Profile profile = Profile.getCurrentProfile();
        String facebookID = profile.getId();

        // get the details from entries and selections
        // title, description, category and time already populated
        //String geo_loc = ;
        //String place_id = ;

        // populate eventDetails
        try {
            eventDetails.put("title", title);
            eventDetails.put("description", description);
            //eventDetails.put("geo_loc", geo_loc);
            //eventDetails.put("place_id", place_id);
            eventDetails.put("time", time);
            eventDetails.put("owner", facebookID);
            eventDetails.put("category", category);
        } catch (JSONException e) {
            return null;
        }

        return eventDetails;
    }
}