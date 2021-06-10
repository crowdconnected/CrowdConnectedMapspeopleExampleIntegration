package net.crowdconnected.mapspeople.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.mapsindoors.mapssdk.MapControl;
import com.mapsindoors.mapssdk.MapsIndoors;
import com.mapsindoors.mapssdk.OnRouteResultListener;
import com.mapsindoors.mapssdk.Route;
import com.mapsindoors.mapssdk.Venue;
import com.mapsindoors.mapssdk.errors.MIError;

import net.crowdconnected.android.core.Configuration;
import net.crowdconnected.android.core.ConfigurationBuilder;
import net.crowdconnected.android.core.CrowdConnected;
import net.crowdconnected.android.ips.IPSModule;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, OnRouteResultListener {

    private static final String LOG_TAG = "MAP_ACTIVITY";

    private static final String MAPSPEOPLE_API_KEY = "YOUR_MAPSPEOPLE_API_KEY";
    private static final String GOOGLE_API_KEY = "YOUR_GOOGLE_API_KEY";
    private static final String CROWDCONNECTED_APP_KEY = "YOUR_CROWDCONNECTED_APP_KEY";
    private static final String CROWDCONNECTED_TOKEN = "YOUR_CROWDCONNECTED_TOKEN";
    private static final String CROWDCONNECTED_SECRET = "YOUR_CROWDCONNECTED_SECRET";

    private View mapView;
    private GoogleMap googleMap;
    private MapControl mapControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        MapsIndoors.initialize(getApplicationContext(), MAPSPEOPLE_API_KEY);
        MapsIndoors.setGoogleAPIKey(GOOGLE_API_KEY);
        if (CrowdConnected.getInstance() == null) {
            Log.i(LOG_TAG, "Colocator Instance is null, starting Colocator");
            Configuration configuration = new ConfigurationBuilder()
                    .withAppKey(CROWDCONNECTED_APP_KEY)
                    .withToken(CROWDCONNECTED_TOKEN)
                    .withSecret(CROWDCONNECTED_SECRET)
                    .withStatusCallback(reason -> Log.i(LOG_TAG, "Start up failure: " + reason))
                    .addModule(new IPSModule())
                    .build();
            CrowdConnected.start(this, configuration);
            CrowdConnected.getInstance().navigation(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CrowdConnected crowdConnectedInstance = CrowdConnected.getInstance();
        if (crowdConnectedInstance != null) {
            crowdConnectedInstance.stop();
        }
        MapsIndoors.onApplicationTerminate();
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (mapView != null) {
            initMapControl(mapView);
            CrowdConnectedMapspeopleLocationProvider mapspeopleLocationProvider = new CrowdConnectedMapspeopleLocationProvider(this);
            MapsIndoors.setPositionProvider(mapspeopleLocationProvider);
            mapControl.showUserPosition(true);
            mapspeopleLocationProvider.startPositioning(null);
        }
    }

    private void initMapControl(View view) {
        //Creates a new instance of MapControl
        mapControl = new MapControl(this);
        //Sets the Google map object and the map view to the MapControl
        mapControl.setGoogleMap(googleMap, view);
        //Initiates the MapControl
        mapControl.init(miError -> {
            if (miError == null) {
                //No errors so getting the first venue (in the white house solution the only one)
                Venue venue = MapsIndoors.getVenues().getCurrentVenue();
                runOnUiThread(() -> {
                    if (venue != null) {
                        //Animates the camera to fit the new venue
                        try {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(venue.getLatLngBoundingBox(), 19));
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Failed to animate camera to venue", e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRouteResult(@Nullable Route route, @Nullable MIError miError) {
        //Not implemented
    }
}