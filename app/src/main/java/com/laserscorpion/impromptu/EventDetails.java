package com.laserscorpion.impromptu;

import com.google.android.gms.location.places.*;

import java.util.ArrayList;
import java.util.Date;

public class EventDetails {
    public String id;
    public String owner;
    public String ownerName;
    public String description;
    public String title;
    public String category;
    public Place place;
    public ArrayList<String> attendees;
    public Date time;
}
