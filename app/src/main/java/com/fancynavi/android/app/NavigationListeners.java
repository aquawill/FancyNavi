package com.fancynavi.android.app;

import com.here.android.mpa.guidance.NavigationManager;

class NavigationListeners {
    private NavigationManager.LaneInformationListener laneInformationListener;
    private NavigationManager.RoutingZoneListener routingZoneListener;
    private NavigationManager.RealisticViewListener realisticViewListener;
    private NavigationManager.NavigationManagerEventListener navigationManagerEventListener;
    private NavigationManager.PositionListener positionListener;
    private NavigationManager.RerouteListener rerouteListener;
    private NavigationManager.TrafficRerouteListener trafficRerouteListener;
    private NavigationManager.SafetySpotListener safetySpotListener;
    private NavigationManager.ManeuverEventListener maneuverEventListener;

    public NavigationManager.NewInstructionEventListener getNewInstructionEventListener() {
        return newInstructionEventListener;
    }

    public void setNewInstructionEventListener(NavigationManager.NewInstructionEventListener newInstructionEventListener) {
        this.newInstructionEventListener = newInstructionEventListener;
    }

    private NavigationManager.NewInstructionEventListener newInstructionEventListener;

    public NavigationManager.RoutingZoneListener getRoutingZoneListener() {
        return routingZoneListener;
    }

    public void setRoutingZoneListener(NavigationManager.RoutingZoneListener routingZoneListener) {
        this.routingZoneListener = routingZoneListener;
    }

    NavigationManager.ManeuverEventListener getManeuverEventListener() {
        return maneuverEventListener;
    }

    void setManeuverEventListener(NavigationManager.ManeuverEventListener maneuverEventListener) {
        this.maneuverEventListener = maneuverEventListener;
    }

    NavigationManager.LaneInformationListener getLaneInformationListener() {
        return laneInformationListener;
    }

    void setLaneInformationListener(NavigationManager.LaneInformationListener laneInformationListener) {
        this.laneInformationListener = laneInformationListener;
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
