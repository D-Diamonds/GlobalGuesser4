package com.example.globalguesser4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap map;
    private MapView mapView;
    private Button checkBtn;
    private TextView textView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private static final String[] locationNames = {"Japan", "Ocean", "Hawaii", "Madagascar", "Amazon", "Antarctica", "Yellowstone", "NYC", "London", "Israel"};
    private static final double[][] locationCoords = {{35.6492851,139.7448251}, {-55.065834,110.0088882}, {21.6925629,-158.0204338},
            {-21.3466226,47.7383032}, {-3.5653189,-73.1831034}, {-66.9523949,-66.7969291}, {44.4175683,-110.5706783}, {40.782865,-73.965355},
            {51.503324,-0.119543}, {31.7767701,35.2342411}};
    private static final String[] locationImage = {"japan.jpg", "ocean.jpg", "hawaii.jpg", "madagascar.jpg", "amazon.jpg", "antarctica.jpg", "yellowstone.jpg", "nyc.jpg", "london.jpg", "israel.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.checkBtn = findViewById(R.id.button);
        this.textView = findViewById(R.id.textView);

        this.checkBtn.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        double distance = getDistance();
    }

    public double getDistance() {
        return 0;
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
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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
