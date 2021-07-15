package net.crowdconnected.mapspeople.example;

import android.app.Activity;
import android.util.Log;

import com.mapsindoors.mapssdk.MPPositionResult;
import com.mapsindoors.mapssdk.OnPositionUpdateListener;
import com.mapsindoors.mapssdk.OnStateChangedListener;
import com.mapsindoors.mapssdk.PermissionsAndPSListener;
import com.mapsindoors.mapssdk.Point;
import com.mapsindoors.mapssdk.PositionProvider;
import com.mapsindoors.mapssdk.PositionResult;

import net.crowdconnected.android.core.Configuration;
import net.crowdconnected.android.core.ConfigurationBuilder;
import net.crowdconnected.android.core.CrowdConnected;
import net.crowdconnected.android.ips.IPSModule;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;

public class CrowdConnectedMapspeopleLocationProvider implements PositionProvider {

    private static final String CROWDCONNECTED_APP_KEY = "YOUR_CROWDCONNECTED_APP_KEY";
    private static final String CROWDCONNECTED_TOKEN = "YOUR_CROWDCONNECTED_TOKEN";
    private static final String CROWDCONNECTED_SECRET = "YOUR_CROWDCONNECTED_SECRET";

    private final Activity activity;

    private OnPositionUpdateListener onPositionUpdateListener;
    private boolean isStarted = false;

    public CrowdConnectedMapspeopleLocationProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void startPositioning(String s) {
        Log.i("LOC_PROV", "Start positioning");
        if (CrowdConnected.getInstance() == null) {
            Log.i("LOC_PROV", "Colocator Instance is null, starting Colocator");
            Configuration configuration = new ConfigurationBuilder()
                    .withAppKey(CROWDCONNECTED_APP_KEY)
                    .withToken(CROWDCONNECTED_TOKEN)
                    .withSecret(CROWDCONNECTED_SECRET)
                    .withStatusCallback(reason -> Log.i("LOC_PROV", "Start up failure: " + reason))
                    .addModule(new IPSModule())
                    .build();
            CrowdConnected.start(activity.getApplication(), configuration);
        }
        CrowdConnected.getInstance().registerPositionCallback(position -> {
            activity.runOnUiThread(() -> {
                if (onPositionUpdateListener != null) {
                    MPPositionResult positionResult = new MPPositionResult(new Point(position.getLatitude(), position.getLongitude()), 0, 0, position.getFloor());
                    onPositionUpdateListener.onPositionUpdate(positionResult);
                }
            });
        });
        isStarted = true;
    }

    @Override
    public void stopPositioning(@Nullable String s) {
        Log.i("LOC_PROV", "Stop positioning");
        CrowdConnected crowdConnectedInstance = CrowdConnected.getInstance();
        if (crowdConnectedInstance != null) {
            crowdConnectedInstance.deregisterPositionCallback();
            crowdConnectedInstance.stop();
        }
    }

    @NotNull
    @Override
    public String[] getRequiredPermissions() {
        return new String[0];
    }

    @Override
    public boolean isPSEnabled() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return isStarted;
    }

    @Override
    public void addOnPositionUpdateListener(@Nullable OnPositionUpdateListener onPositionUpdateListener) {
        this.onPositionUpdateListener = onPositionUpdateListener;
    }

    @Override
    public void removeOnPositionUpdateListener(@Nullable OnPositionUpdateListener onPositionUpdateListener) {
        this.onPositionUpdateListener = null;
    }

    @Override
    public void setProviderId(@Nullable String s) {
        //Not implemented
    }

    @Override
    public void addOnStateChangedListener(@Nullable OnStateChangedListener onStateChangedListener) {
        //Not implemented
    }

    @Override
    public void removeOnStateChangedListener(@Nullable OnStateChangedListener onStateChangedListener) {
        //Not implemented
    }

    @Override
    public void checkPermissionsAndPSEnabled(@Nullable PermissionsAndPSListener permissionsAndPSListener) {
        //Not implemented
    }

    @Override
    public String getProviderId() {
        return null;
    }

    @Override
    public PositionResult getLatestPosition() {
        return null;
    }

    @Override
    public void startPositioningAfter(int i, @Nullable String s) {
        //Not implemented
    }

    @Override
    public void terminate() {
        //Not implemented
    }

}
