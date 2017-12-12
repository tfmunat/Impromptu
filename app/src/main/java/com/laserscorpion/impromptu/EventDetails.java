package com.laserscorpion.impromptu;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class EventDetails implements Serializable {
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
