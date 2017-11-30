package com.laserscorpion.impromptu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/* from Google documentation and the following website */
/* https://www.bignerdranch.com/blog/splash-screens-the-right-way/ */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LaunchAppRoutingActivity.class);
        startActivity(intent);
        finish();
    }
}