package com.fancynavi.app;

import com.here.android.mpa.mapping.Map;

public class MapSchemeChanger {

    private Map map;

    MapSchemeChanger(Map map) {
        this.map = map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    void darkenMap() {
        switch (map.getMapScheme()) {
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
        switch (map.getMapScheme()) {
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
