package com.fancynavi.app;

import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;

public class MapSchemeChanger {

    private Map map;
    private NavigationManager navigationManager;

    MapSchemeChanger(Map map, NavigationManager navigationManager) {
        this.map = map;
        this.navigationManager = navigationManager;
    }

    void darkenMap() {
        if (navigationManager != null) {
            navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.NIGHT);
        }
        switch (map.getMapScheme()) {
            case Map.Scheme.NORMAL_DAY:
                map.setMapScheme(Map.Scheme.NORMAL_NIGHT);
                break;
            case Map.Scheme.CARNAV_DAY:
                map.setMapScheme(Map.Scheme.CARNAV_NIGHT);
                break;
            case Map.Scheme.CARNAV_TRAFFIC_DAY:
                map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_NIGHT);
                break;
            case Map.Scheme.TRUCK_DAY:
                map.setMapScheme(Map.Scheme.TRUCK_NIGHT);
                break;
            case Map.Scheme.PEDESTRIAN_DAY:
                map.setMapScheme(Map.Scheme.PEDESTRIAN_NIGHT);
                break;
        }
    }

    void lightenMap() {
        if (navigationManager != null) {
            navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        }
        switch (map.getMapScheme()) {
            case Map.Scheme.NORMAL_NIGHT:
                map.setMapScheme(Map.Scheme.NORMAL_DAY);
                break;
            case Map.Scheme.CARNAV_NIGHT:
                map.setMapScheme(Map.Scheme.CARNAV_DAY);
                break;
            case Map.Scheme.CARNAV_TRAFFIC_NIGHT:
                map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
                break;
            case Map.Scheme.TRUCK_NIGHT:
                map.setMapScheme(Map.Scheme.TRUCK_DAY);
                break;
            case Map.Scheme.PEDESTRIAN_NIGHT:
                map.setMapScheme(Map.Scheme.PEDESTRIAN_DAY);
                break;
        }
    }
}
