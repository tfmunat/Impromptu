package com.laserscorpion.impromptu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A screen shown after the user logs into Facebook that asks them for their interests.
 * After submitting this, they can start using the app.
 */
public class BasicProfileCompletionActivity extends Activity {
    private static final int NUM_LIKES = 5;
    private static final String TAG = "BasicProfileCompletion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_profile_completion);
    }

    /**
     * Submit the new user's interests and identifying details to the server
     * @param view the button that was clicked
     */
    public void registerDetailsWithServer(View view) {
        JSONObject userDetails = getUserDetails();
        sendRegistrationDetails(userDetails);


    }

    private void startMapActivity() {
        Intent intent = new Intent(this, FindEventActivity.class);
        startActivity(intent);
    }

    private void sendRegistrationDetails(JSONObject userInfo) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.sign_up_new_user_url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        startMapActivity();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorDialog dialog = ErrorDialog.newInstance(error.getMessage());
                dialog.show(getFragmentManager(), "SignupError");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private JSONObject getUserDetails() {
        JSONObject userDetails = new JSONObject();
        AccessToken token = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        String facebookID = profile.getId();
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();
        ArrayList<String> interests = new ArrayList<>();
        for (int i = 1; i <= NUM_LIKES; i++) {
            int resId = getResources().getIdentifier("interest" + i, "id", getPackageName());
            EditText textfield = (EditText)findViewById(resId);
            CharSequence interest = textfield.getText();
            if (interest.length() > 0)
                interests.add(interest.toString());
        }

        try {
            userDetails.put("first_name", firstName);
            userDetails.put("last_name", lastName);
            userDetails.put("id", facebookID);
            userDetails.put("likes", new JSONArray(interests.toArray()));
            Log.d(TAG, userDetails.toString(4));
        } catch (JSONException e) {
            // unclear why this would be thrown
            return null;
        }
        return userDetails;
    }

}
