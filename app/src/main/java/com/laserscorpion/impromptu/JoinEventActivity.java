package com.laserscorpion.impromptu;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.Date;

public class JoinEventActivity extends FragmentActivity {

    String id;
    public String owner;
    public String ownerName;
    public String description;
    public String title;
    public String category;
    public Place place;
    public ArrayList<String> attendees;
    public Date time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
    }

    /* join when the button is clicked */
    public void joinEvent(View v){

    }
}
