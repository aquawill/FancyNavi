package com.fancynavi.android.app;

import com.here.android.mpa.guidance.NavigationManager;

class NavigationListeners {
    private NavigationManager.LaneInformationListener laneinformationListener;
    private NavigationManager.RealisticViewListener realisticViewListener;
    private NavigationManager.NavigationManagerEventListener navigationManagerEventListener;
    private NavigationManager.PositionListener positionListener;
    private NavigationManager.RerouteListener rerouteListener;
    private NavigationManager.TrafficRerouteListener trafficRerouteListener;
    private NavigationManager.SafetySpotListener safetySpotListener;

    NavigationManager.LaneInformationListener getLaneinformationListener() {
        return laneinformationListener;
    }

    void setLaneinformationListener(NavigationManager.LaneInformationListener laneinformationListener) {
        this.laneinformationListener = laneinformationListener;
    }

    NavigationManager.RealisticViewListener getRealisticViewListener() {
        return realisticViewListener;
    }

    void setRealisticViewListener(NavigationManager.RealisticViewListener realisticViewListener) {
        this.realisticViewListener = realisticViewListener;
    }

    NavigationManager.NavigationManagerEventListener getNavigationManagerEventListener() {
        return navigationManagerEventListener;
    }

    void setNavigationManagerEventListener(NavigationManager.NavigationManagerEventListener navigationManagerEventListener) {
        this.navigationManagerEventListener = navigationManagerEventListener;
    }

    NavigationManager.PositionListener getPositionListener() {
        return positionListener;
    }

    void setPositionListener(NavigationManager.PositionListener positionListener) {
        this.positionListener = positionListener;
    }

    NavigationManager.RerouteListener getRerouteListener() {
        return rerouteListener;
    }

    void setRerouteListener(NavigationManager.RerouteListener rerouteListener) {
        this.rerouteListener = rerouteListener;
    }

    NavigationManager.TrafficRerouteListener getTrafficRerouteListener() {
        return trafficRerouteListener;
    }

    void setTrafficRerouteListener(NavigationManager.TrafficRerouteListener trafficRerouteListener) {
        this.trafficRerouteListener = trafficRerouteListener;
    }

    NavigationManager.SafetySpotListener getSafetySpotListener() {
        return safetySpotListener;
    }

    void setSafetySpotListener(NavigationManager.SafetySpotListener safetySpotListener) {
        this.safetySpotListener = safetySpotListener;
    }
}
