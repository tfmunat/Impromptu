package com.laserscorpion.impromptu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class FindEventActivity extends FragmentActivity implements OnMapReadyCallback, EventRequestReceiver {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "FindEventActivity";
    private static final String INTERESTS = "my_interests";
    private LocationManager locationManager;
    private GoogleMap mMap;
    private Set<EventDetails> nearbyEvents;
    private Map<Marker, EventDetails> markerMap;
    private EditText searchBox;
    View mapView;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        nearbyEvents = new HashSet<>();
        searchBox = findViewById(R.id.search_box);
        mapView = findViewById(R.id.map);
        markerMap = new HashMap<>();
        CheckBox map_interest_checkbox = findViewById(R.id.map_interest_checkbox);

        map_interest_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String interest_list  = prefs.getString(INTERESTS, null);
                    if (interest_list == null) {
                        // bad
                        ErrorDialog dialog = ErrorDialog.newInstance("Can't find interests; try reentering");
                        dialog.show(getFragmentManager(), "Search Error");
                        Log.e(TAG, "don't have interests?!?!");
                    } else {
                        Log.d(TAG, interest_list);
                        String[] user_interests = interest_list.split(",");
                        search_interests(user_interests);
                    }
                } else {
                    search();
                }
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert manager != null;
                    manager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                    searchBox.clearFocus();
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton myFab = findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton myFab2 = findViewById(R.id.myFAB2);
        myFab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) throws java.lang.NullPointerException{
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                Location currentLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (currentLoc == null) {
                    mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(40.8075,-73.9619) , 14f) );
                } else {
                    mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(currentLoc.getLatitude(),
                            currentLoc.getLongitude()) , 14f) );
                }
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                EventDetails details = markerMap.get(marker);
                Intent intent = new Intent(context,JoinEventActivity.class);
                intent.putExtra("event_host", details.ownerName);
                intent.putExtra("event_attendees", details.attendees);
                intent.putExtra("event_time", details.time.toString());
                intent.putExtra("event_title", details.title);
                intent.putExtra("event_description", details.description);
                intent.putExtra("event_category", details.category);
                intent.putExtra("event_venue", details.place.getName());
                intent.putExtra("event_id", details.id);
                startActivity(intent);
            }
        });
        search();
    }

    private float getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        return (outMetrics.widthPixels / density);
    }

    private ArrayList<String> getInterestSearchTerms(String[] user_interests) {
        ArrayList<String> result = new ArrayList<>();
        for (String s : user_interests) {
            StringTokenizer tokenizer = new StringTokenizer(s);
            while (tokenizer.hasMoreTokens()) {
                result.add(tokenizer.nextToken());
            }
        }
        return result;
    }

    private void search_interests(String[] user_interests){
        ArrayList<String> keywords = getInterestSearchTerms(user_interests);

        float width = getWidth();
        float zoom = mMap.getCameraPosition().zoom;
        LatLng mapPos = mMap.getCameraPosition().target;
        double meters_per_pixel = 156543.03392 * Math.cos(mapPos.latitude * Math.PI / 180) / Math.pow(2, zoom); // https://stackoverflow.com/questions/9356724/google-map-api-zoom-range
        double meters = meters_per_pixel * width;

        EventSearcher searcher = new EventSearcher(this, this);
        searcher.search(mapPos, (int)meters, keywords);
    }

    private ArrayList<String> getSearchTerms() {
        ArrayList<String> result = new ArrayList<>();
        String keywords = searchBox.getText().toString();
        StringTokenizer tokenizer = new StringTokenizer(keywords);
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }

    private void search() {
        ArrayList<String> keywords = getSearchTerms();

        float width = getWidth();
        float zoom = mMap.getCameraPosition().zoom;
        LatLng mapPos = mMap.getCameraPosition().target;
        double meters_per_pixel = 156543.03392 * Math.cos(mapPos.latitude * Math.PI / 180) / Math.pow(2, zoom); // https://stackoverflow.com/questions/9356724/google-map-api-zoom-range
        double meters = meters_per_pixel * width;

        EventSearcher searcher = new EventSearcher(this, this);
        searcher.search(mapPos, (int)meters, keywords);
    }

    public void onEventsReceived(Collection<EventDetails> events) {
        Log.d(TAG, "Received " + events.size() + " events");
        runOnUiThread(new Runnable() {
            public void run() {
                mMap.clear();
            }
        });
        nearbyEvents.clear();
        markerMap.clear();
        nearbyEvents.addAll(events);

        for (EventDetails event : nearbyEvents) {
            LatLng latlng = event.place.getLatLng();
            final MarkerOptions marker = new MarkerOptions();
            marker.position(latlng);
            marker.title(event.title);
            if (event.attendees.size() == 0){
                marker.snippet("\nHost: " + event.ownerName + "\nVenue: " + event.place.getName() +
                        "\nAttendees: None" + "\n" + event.time);
            } else if(event.attendees.size() > 0){
                marker.snippet("\nHost: " + event.ownerName + "\nVenue: " + event.place.getName() +
                        "\nAttendees: " + event.attendees + "\n" + event.time);
            }

            runOnUiThread( new MapEditor(markerMap, marker, event) );
        }
    }



    @Override
    public void onRequestFailed(String reason) {
        ErrorDialog dialog = ErrorDialog.newInstance(reason);
        dialog.show(getFragmentManager(), "HTTP Error");
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // this will never happen
                        return;
                    }
                    Location currentLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    LatLng currentLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
                } else {
                    ErrorDialog dialog = ErrorDialog.newInstance("Can't get current location. Grant access to location to see nearby events.");
                    dialog.show(getFragmentManager(), "Location Error");
                }
            }
        }
    }

    private class MapEditor implements Runnable {
        Map<Marker, EventDetails> eventMarkers;
        MarkerOptions marker;
        EventDetails details;

        public MapEditor(Map<Marker, EventDetails> data, MarkerOptions marker, EventDetails details) {
            eventMarkers = data;
            this.marker = marker;
            this.details = details;
        }

        @Override
        public void run() {
            Marker realMarker = mMap.addMarker(marker);
            markerMap.put(realMarker, details);
        }
    }
}
