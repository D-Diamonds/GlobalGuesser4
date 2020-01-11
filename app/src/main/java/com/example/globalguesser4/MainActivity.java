package com.example.globalguesser4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;

/*
 This app works by starting the game off with a simple play button. Once the button is pressed a map and imagine will appear.
 The image will be randomly selected from the 10 locations and removed from the overall list to prevent duplicates.
 The user then has to select where the image is on the map. They have the ability to change their guess until they click
 the "guess" button. Once they click it the correct marker will show up (the user has to find it by dragging around the map),
 the distance they were from this location will be displayed and it will be added to the current score. To guess the next location
 the user must click "Randomize". This process repeats until there are no more locations available.

 To make score complex, the score has a multiplier. The closer they are to the correct the location
 within certain preset checkpoints, the more the multiplier is increased. At the end of the game the score is divided by the
 multiplier to provide the final score.
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    // views
    private MapView mapView;
    private Button btn;
    private TextView distanceTxt;
    private TextView scoreTxt;
    private ImageView imageView;

    private GoogleMap mMap;

    // point variables
    private double points;
    private double multiplier;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    // location variables
    private int currentLocationIndex;
    private LatLng currentClickedPoint;
    private boolean markerPlaced;

    private static final ArrayList<String> LOCATION_NAMES = new ArrayList<>(Arrays.asList("Japan", "Ocean", "Hawaii", "Madagascar", "Amazon", "Antarctica", "Yellowstone", "NYC", "London", "Israel"));
    private static ArrayList<String> locationNames = new ArrayList<>(Arrays.asList("Japan", "Ocean", "Hawaii", "Madagascar", "Amazon", "Antarctica", "Yellowstone", "NYC", "London", "Israel"));

    private static final double[][] locationCoords = {{35.6492851,139.7448251}, {-55.065834,110.0088882}, {21.6925629,-158.0204338},
            {-21.3466226,47.7383032}, {-3.5653189,-73.1831034}, {-66.9523949,-66.7969291}, {44.4175683,-110.5706783}, {40.782865,-73.965355},
            {51.503324,-0.119543}, {31.7767701,35.2342411}};
    private static final int[] locationImage = {R.drawable.japan, R.drawable.ocean, R.drawable.hawaii, R.drawable.madagascar, R.drawable.amazon, R.drawable.antarctica, R.drawable.yellowstone, R.drawable.nyc, R.drawable.london, R.drawable.israel};


    // Creates the activity and all the map stuff
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btn = findViewById(R.id.button);
        this.distanceTxt = findViewById(R.id.distance);
        this.imageView = findViewById(R.id.locationImage);
        this.scoreTxt = findViewById(R.id.score);

        btn.setText("Play");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationNames.size() > 0)
                    showMapGuessScreen();
            }
        });
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        this.mapView = (MapView) findViewById(R.id.map);
        this.mapView.onCreate(mapViewBundle);
        this.mapView.getMapAsync(this);
    }

    // gets current location index (helper function)
    public int getIndex(ArrayList list, String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(name))
                return i;
        }
        return 0;
    }

    // shows the correct location and stops user from moving guess
    public void showResult() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(locationCoords[currentLocationIndex][0], locationCoords[currentLocationIndex][1])));
        distanceTxt.setVisibility(View.VISIBLE);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
            }
        });
    }


    // randomizes the location to guess
    public void randomizeLocation() {
        markerPlaced = false;
        distanceTxt.setVisibility(View.INVISIBLE);
        int tempIndex = (int) (Math.random() * locationNames.size());
        String currentLocationName = locationNames.get(tempIndex);
        currentLocationIndex = getIndex(LOCATION_NAMES, currentLocationName);
        locationNames.remove(tempIndex);
        imageView.setImageResource(locationImage[currentLocationIndex]);
        mMap.clear();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //allPoints.add(point);
                currentClickedPoint = point;
                mMap.clear();
                markerPlaced = true;
                mMap.addMarker(new MarkerOptions()
                        .position(currentClickedPoint));
            }
        });
    }

    // shows the game guessing screen and all its functionality
    public void showMapGuessScreen() {
        randomizeLocation();
        mapView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        btn.setText("Guess");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button && mapView.getVisibility() != View.INVISIBLE && markerPlaced) {
                    updateScore(getDistance(currentClickedPoint.latitude, currentClickedPoint.longitude, locationCoords[currentLocationIndex][0], locationCoords[currentLocationIndex][1]));
                    showResult();
                    if (locationNames.size() > 0) {
                        btn.setText("Randomize");
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showMapGuessScreen();
                            }
                        });
                    }
                    else {
                        btn.setVisibility(View.INVISIBLE);
                        scoreTxt.setVisibility(View.INVISIBLE);
                        distanceTxt.setText("GAME OVER\n" +
                                "Final Score: " + Double.parseDouble(String.format("%.3f", points / multiplier)) + "\n" +
                                "Multiplier: " + multiplier);
                    }
                }
            }
        });
    }

    // sets up default start state
    public void initialize() {
        multiplier = 1;
        this.mapView.setVisibility(View.INVISIBLE);
        this.imageView.setVisibility(View.INVISIBLE);
        markerPlaced = false;
    }

    // updates score based on distance
    public void updateScore(double distance) {
        if (distance < 500)
            multiplier += 3;
        else if (distance < 1000)
            multiplier += 2;
        else if (distance < 2500)
            multiplier += 1;
        distanceTxt.setText(String.format("%.3f", distance) + " KM");
        if (scoreTxt.getText().toString().equals("Null"))
            points = distance;
        else
            points += distance;
        scoreTxt.setText("Score: " + Double.parseDouble(String.format("%.3f", points)) + "\n" +
                "Multiplier: " + multiplier);
    }

    // initializes map
    @Override
    public void onMapReady(GoogleMap map) {
        initialize();
        mMap = map;
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    // gets distance between user guess and correct location
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // metres
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2-lat1);
        double Δλ = Math.toRadians(lon2-lon1);

        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (R * c) / 1000;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        this.mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.mapView.onStop();
    }

    @Override
    protected void onPause() {
        this.mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        this.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mapView.onLowMemory();
    }
}
