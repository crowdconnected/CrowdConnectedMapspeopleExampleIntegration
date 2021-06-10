package net.crowdconnected.mapspeople.example;

import android.app.Activity;

import com.mapsindoors.mapssdk.MPPositionResult;
import com.mapsindoors.mapssdk.OnPositionUpdateListener;
import com.mapsindoors.mapssdk.OnStateChangedListener;
import com.mapsindoors.mapssdk.PermissionsAndPSListener;
import com.mapsindoors.mapssdk.Point;
import com.mapsindoors.mapssdk.PositionProvider;
import com.mapsindoors.mapssdk.PositionResult;

import net.crowdconnected.android.core.CrowdConnected;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;

public class CrowdConnectedMapspeopleLocationProvider implements PositionProvider {

    private final Activity activity;

    private OnPositionUpdateListener onPositionUpdateListener;
    private boolean isStarted = false;

    public CrowdConnectedMapspeopleLocationProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void startPositioning(String s) {
        CrowdConnected.getInstance().registerPositionCallback(position -> {
            activity.runOnUiThread(() -> {
                if (onPositionUpdateListener != null) {
                    MPPositionResult positionResult = new MPPositionResult(new Point(position.getLat(), position.getLng()), 0, 0, position.getFloor());
                    onPositionUpdateListener.onPositionUpdate(positionResult);
                }
            });
        });
        isStarted = true;
    }

    @Override
    public void stopPositioning(@Nullable String s) {
        CrowdConnected.getInstance().deregisterPositionCallback();
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
