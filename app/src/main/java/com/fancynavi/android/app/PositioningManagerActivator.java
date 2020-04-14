package com.fancynavi.android.app;

import android.os.Build;
import android.util.Log;

import com.here.android.mpa.common.LocationDataSourceGoogleServices;
import com.here.android.mpa.common.PositioningManager;

import static com.fancynavi.android.app.DataHolder.TAG;

class PositioningManagerActivator {
    PositioningManager positioningManager;

    PositioningManagerActivator(PositioningManager.LocationMethod locationMethod, boolean activateHereAdvancedPositioning) {
        /* PositioningManager init */
        positioningManager = PositioningManager.getInstance();
        Log.d(TAG, "Build.FINGERPRINT: " + Build.FINGERPRINT);
        /* Advanced positioning */
        if (activateHereAdvancedPositioning) {
            if (!Build.FINGERPRINT.contains("generic") && !Build.FINGERPRINT.contains("vbox")) {
                LocationDataSourceGoogleServices locationDataSource;
                locationDataSource = LocationDataSourceGoogleServices.getInstance();
                positioningManager.setDataSource(locationDataSource);
                positioningManager.enableProbeDataCollection(true);
            }
        }

        Log.d(TAG, "getLocationSource(): " + positioningManager.getDataSource().getLocationSource().name());
        Log.d(TAG, "getGpsStatus(): " + positioningManager.getDataSource().getGpsStatus());

        positioningManager.start(locationMethod);
        Log.d(TAG, "getLocationMethod: " + positioningManager.getLocationMethod());
    }

    PositioningManager getPositioningManager() {
        return positioningManager;
    }
}
