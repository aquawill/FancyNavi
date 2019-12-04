package com.fancynavi.android.app;

import android.os.Build;
import android.util.Log;

import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.PositioningManager;

import static com.fancynavi.android.app.DataHolder.TAG;

class PositionActivation {
    PositioningManager m_positioningManager;

    PositionActivation(PositioningManager.LocationMethod locationMethod) {
        /* PositioningManager init */
        m_positioningManager = PositioningManager.getInstance();
        Log.d(TAG, "Build.FINGERPRINT: " + Build.FINGERPRINT);
        /* Advanced positioning */
        if (!Build.FINGERPRINT.contains("generic") && !Build.FINGERPRINT.contains("vbox")) {
            LocationDataSourceHERE m_hereDataSource;
            m_hereDataSource = LocationDataSourceHERE.getInstance();
            m_positioningManager.setDataSource(m_hereDataSource);
            m_positioningManager.enableProbeDataCollection(true);
        }

        Log.d(TAG, m_positioningManager.getDataSource().getLocationSource().name());
        Log.d(TAG, String.valueOf(m_positioningManager.getDataSource().getGpsStatus()));

        if (m_positioningManager != null) {
            m_positioningManager.start(locationMethod);
        }
    }

    PositioningManager getPositioningManager() {
        return m_positioningManager;
    }
}
