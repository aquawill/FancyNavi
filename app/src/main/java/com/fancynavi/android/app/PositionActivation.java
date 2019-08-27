package com.fancynavi.android.app;

import android.os.Build;
import android.util.Log;

import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.PositioningManager;

class PositionActivation {
    PositioningManager m_positioningManager;

    PositionActivation(PositioningManager.LocationMethod locationMethod) {
        /* PositioningManager init */
        m_positioningManager = PositioningManager.getInstance();
        Log.d("test", "Build.FINGERPRINT: " + Build.FINGERPRINT);
        /* Advanced positioning */
        if (!Build.FINGERPRINT.contains("generic") && !Build.FINGERPRINT.contains("vbox")) {
            LocationDataSourceHERE m_hereDataSource;
            m_hereDataSource = LocationDataSourceHERE.getInstance();
            m_positioningManager.setDataSource(m_hereDataSource);
            m_positioningManager.enableProbeDataCollection(true);
        }

        Log.d("test", m_positioningManager.getDataSource().getLocationSource().name());
        Log.d("test", String.valueOf(m_positioningManager.getDataSource().getGpsStatus()));

        if (m_positioningManager != null) {
            m_positioningManager.start(locationMethod);
        }
    }

    PositioningManager getPositioningManager() {
        return m_positioningManager;
    }
}
