package com.fancynavi.android.app;

import android.os.Build;
import android.util.Log;

import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.PositioningManager;

import static com.fancynavi.android.app.DataHolder.TAG;

class PositioningManagerActivator {
    PositioningManager m_positioningManager;

    PositioningManagerActivator(PositioningManager.LocationMethod locationMethod, boolean activateHereAdvancedPositioning) {
        /* PositioningManager init */
        m_positioningManager = PositioningManager.getInstance();
        Log.d(TAG, "Build.FINGERPRINT: " + Build.FINGERPRINT);
        /* Advanced positioning */
        if (activateHereAdvancedPositioning) {
            if (!Build.FINGERPRINT.contains("generic") && !Build.FINGERPRINT.contains("vbox")) {
                LocationDataSourceHERE m_hereDataSource;
                m_hereDataSource = LocationDataSourceHERE.getInstance();
                m_positioningManager.setDataSource(m_hereDataSource);
                m_positioningManager.enableProbeDataCollection(true);
            }
        }

        Log.d(TAG, m_positioningManager.getDataSource().getLocationSource().name());
        Log.d(TAG, String.valueOf(m_positioningManager.getDataSource().getGpsStatus()));

        m_positioningManager.start(locationMethod);
        Log.d(TAG, "getLocationMethod: " + m_positioningManager.getLocationMethod());
    }

    PositioningManager getPositioningManager() {
        return m_positioningManager;
    }
}
