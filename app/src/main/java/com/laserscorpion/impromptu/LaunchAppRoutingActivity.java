package com.laserscorpion.impromptu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.Profile;

public class LaunchAppRoutingActivity extends AppCompatActivity {
    protected AccessToken token;
    protected Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userIsLoggedIn()) {
            Intent intent = new Intent(this, FindEventActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, FacebookSignupActivity.class);
            startActivity(intent);
        }
        finish();
    }

    protected void populateFacebookTokens() {
        token = FacebookResourceLocator.getToken();
        profile = FacebookResourceLocator.getProfile();
    }

    private boolean userIsLoggedIn() {
        populateFacebookTokens();
        return token != null && profile != null;
    }
}
