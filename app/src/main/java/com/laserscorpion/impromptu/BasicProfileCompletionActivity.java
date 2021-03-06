package com.laserscorpion.impromptu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A screen shown after the user logs into Facebook that asks them for their interests.
 * After submitting this, they can start using the app.
 */
public class BasicProfileCompletionActivity extends Activity {
    private Context context = this;
    private static final int NUM_LIKES = 5;
    private static final String TAG = "BasicProfileCompletion";
    private static final String USER_ID = "impromptu_user_id";
    private static final String INTERESTS = "my_interests";


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

    private void sendRegistrationDetails(final JSONObject userInfo) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_base_url) + getString(R.string.sign_up_new_user_url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "here's the response \n" + response);
                        JSONObject json;
                        try {
                            json = new JSONObject(response);
                            String id = json.getString("id");
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(USER_ID, id);
                            editor.commit();
                        } catch (JSONException e) {
                            ErrorDialog dialog = ErrorDialog.newInstance("Got bad JSON from server, can't log in");
                            dialog.show(getFragmentManager(), "SignupError");
                            e.printStackTrace();
                            return;
                        }
                        startMapActivity();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                error.printStackTrace();
                ErrorDialog dialog = ErrorDialog.newInstance(error.toString());
                dialog.show(getFragmentManager(), "SignupError");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError
            {
                try {
                    String userString = userInfo.toString();
                    //byte[] bytes  = userString.getBytes("UTF-8");
                    return userString.getBytes("UTF-8");
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

    private JSONObject getUserDetails() {
        JSONObject userDetails = new JSONObject();
        Profile profile = Profile.getCurrentProfile();
        String facebookID = profile.getId();
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();
        ArrayList<String> interests = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= NUM_LIKES; i++) {
            int resId = getResources().getIdentifier("interest" + i, "id", getPackageName());
            EditText textfield = findViewById(resId);
            CharSequence interest = textfield.getText();
            if (interest.length() > 0) {
                interests.add(interest.toString());
            }
        }

        // save all the interests to sb as a String for later usage
        String prefix = "";
        for(String s: interests) {
            sb.append(prefix);
            prefix = ",";
            sb.append(s);
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(INTERESTS, sb.toString());
        editor.apply();

        try {
            userDetails.put("first_name", firstName);
            userDetails.put("last_name", lastName);
            userDetails.put("fb_id", facebookID);
            Log.d(TAG, facebookID);
            userDetails.put("likes", new JSONArray(interests.toArray()));
            //Log.d(TAG, userDetails.toString(4));
        } catch (JSONException e) {
            // unclear why this would be thrown
            return null;
        }
        return userDetails;
    }

}
