package com.laserscorpion.impromptu;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Profile;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.*;
import java.util.ArrayList;
import java.util.Date;

import static com.laserscorpion.impromptu.FindEventActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class CreateEventActivity extends FragmentActivity {

    String title, description, category;
    long time;
    private EditText e_title;
    private EditText e_desc;
    private EditText e_category;
    View mapView;
    private Place eventPlace;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final String TAG = "CreateEventActivity";
    private static final String USER_ID = "impromptu_user_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = findViewById(R.id.map);

        e_title = (EditText)findViewById(R.id.event_title);
        e_desc = (EditText)findViewById(R.id.event_desc);
        e_category = (EditText)findViewById(R.id.event_category);

    }

    /**
     * Submit the new event details to the server
     * @param view the create button that was clicked
     */
    /* need to make dure all fields were filled up */
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

    /* need to use the time and date data and also show it on the UI */
    /* choose time */
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /* choose date */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /* choose location on the map,*/
    public void showLocation(View view) throws GooglePlayServicesNotAvailableException,
            GooglePlayServicesRepairableException {

        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                eventPlace = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", eventPlace.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


    private void sendEventRegistrationDetails(final JSONObject eventInfo) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_base_url) + "/eventadd";

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id  = prefs.getString(USER_ID, null);
        if (id == null) {
            // bad
            Log.e(TAG, "don't have id?!?!");
        }
        if (eventPlace == null) {
            Log.e(TAG, "don't have place?!?!");
        }

        // get the details from entries and selections
        // title, description, category and time already populated
        //String geo_loc = ;
        //String place_id = ;

        // populate eventDetails
        LatLng latlng = eventPlace.getLatLng();

        ArrayList<Double> geo = new ArrayList<>();
        geo.add(latlng.longitude);
        geo.add(latlng.latitude);
        try {


            eventDetails.put("title", title);
            eventDetails.put("description", description);
            //eventDetails.put("geo_loc", geo_loc);
            eventDetails.put("geo_loc", new JSONArray(geo));
            //eventDetails.put("place_id", place_id);
            eventDetails.put("place_id", eventPlace.getId());
            // eventDetails.put("time", time);
            eventDetails.put("time", new Date().getTime());
            eventDetails.put("owner", id);
            eventDetails.put("category", category);
            Log.d(TAG, eventDetails.toString());
        } catch (JSONException e) {
            return null;
        }

        return eventDetails;
    }
}