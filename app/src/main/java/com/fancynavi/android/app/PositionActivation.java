package com.fancynavi.android.app;

import android.os.Build;

import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.PositioningManager;

class PositionActivation {
    PositioningManager m_positioningManager;

    PositionActivation(PositioningManager.LocationMethod locationMethod) {
        /* PositioningManager init */
        m_positioningManager = PositioningManager.getInstance();

        /* Advanced positioning */
        if (!Build.FINGERPRINT.contains("generic")) {
            LocationDataSourceHERE m_hereDataSource;
            m_hereDataSource = LocationDataSourceHERE.getInstance();
            m_positioningManager.setDataSource(m_hereDataSource);
            m_positioningManager.enableProbeDataCollection(true);
        }

        if (m_positioningManager != null) {
            m_positioningManager.start(locationMethod);
        }
    }

    PositioningManager getPositioningManager() {
        return m_positioningManager;
    }
}
