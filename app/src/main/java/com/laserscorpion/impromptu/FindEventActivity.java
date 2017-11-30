package com.laserscorpion.impromptu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import android.Manifest;

public class FindEventActivity extends FragmentActivity implements OnMapReadyCallback, EventRequestReceiver {
    private static final int WIDTH_HEIGHT_RATIO = 2;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "FindEventActivity";
    private LocationManager locationManager;
    private GoogleMap mMap;
    private Set<EventDetails> nearbyEvents;
    private EditText searchBox;
    LatLng currentLatLng;
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
        searchBox = (EditText) findViewById(R.id.search_box);
        mapView = findViewById(R.id.map);

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                    searchBox.clearFocus();
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateEventActivity.class);
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
                Intent intent = new Intent(FindEventActivity.this,JoinEventActivity.class);
                startActivity(intent);
            }
        });
        //search();
    }

    private float getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;
        return dpWidth;
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

            runOnUiThread(new Runnable() {
                public void run() {
                    mMap.addMarker(marker);
                }
            });
        }
    }

    @Override
    public void onRequestFailed(String reason) {
        ErrorDialog dialog = ErrorDialog.newInstance(reason);
        dialog.show(getFragmentManager(), "HTTP Error");
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
