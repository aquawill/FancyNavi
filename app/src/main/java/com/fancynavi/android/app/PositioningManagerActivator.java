package com.fancynavi.android.app;

import android.os.Build;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.here.android.mpa.common.LocationDataSourceDevice;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.PositioningManager;

import static com.fancynavi.android.app.DataHolder.TAG;

class PositioningManagerActivator {
    private final PositioningManager positioningManager;

    PositioningManagerActivator(PositioningManager.LocationMethod locationMethod, boolean activateHereAdvancedPositioning) {
        /* PositioningManager init */
        positioningManager = PositioningManager.getInstance();
        Log.d(TAG, "Build.FINGERPRINT: " + Build.FINGERPRINT);
        /* Advanced positioning */
        if (activateHereAdvancedPositioning) {
            Log.d(TAG, "activateHereAdvancedPositioning:" + activateHereAdvancedPositioning);
            if (!Build.FINGERPRINT.contains("generic") && !Build.FINGERPRINT.contains("vbox")) {
                LocationDataSourceHERE locationDataSource = LocationDataSourceHERE.getInstance();
                positioningManager.setDataSource(locationDataSource);
            } else {
                LocationDataSourceDevice locationDataSource = LocationDataSourceDevice.getInstance();
                positioningManager.setDataSource(locationDataSource);
            }
        } else {
            LocationDataSourceDevice locationDataSource = LocationDataSourceDevice.getInstance();
            positioningManager.setDataSource(locationDataSource);
        }

        positioningManager.enableProbeDataCollection(true);

        Log.d(TAG, "getLocationSource(): " + positioningManager.getDataSource().getLocationSource().name());
        Log.d(TAG, "getGpsStatus(): " + positioningManager.getDataSource().getGpsStatus());

        positioningManager.start(locationMethod);
        Snackbar.make(DataHolder.getActivity().findViewById(R.id.mapFragmentView), DataHolder.getAndroidXMapFragment().getString(R.string.positioning) + positioningManager.getDataSource().getLocationSource().name() + " / " + positioningManager.getLocationMethod(), Snackbar.LENGTH_LONG).show();
        Log.d(TAG, "Positioning: " + positioningManager.getDataSource().getLocationSource().name() + " / " + positioningManager.getLocationMethod());
        Log.d(TAG, "isProbeDataCollectionEnabled: " + positioningManager.isProbeDataCollectionEnabled());
    }

    PositioningManager getPositioningManager() {
        return positioningManager;
    }
}
