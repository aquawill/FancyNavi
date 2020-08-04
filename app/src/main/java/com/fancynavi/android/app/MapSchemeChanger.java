package com.fancynavi.android.app;

import android.graphics.Color;
import android.widget.TextView;

import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;

import static com.fancynavi.android.app.DataHolder.isNavigating;

class MapSchemeChanger {

    private Map map;
    private NavigationManager navigationManager;

    MapSchemeChanger(Map map, NavigationManager navigationManager) {
        this.map = map;
        this.navigationManager = navigationManager;
    }

    MapSchemeChanger(Map map) {
        this.map = map;
    }

    void darkenMap() {
        TextView guidanceSpeedView = DataHolder.getActivity().findViewById(R.id.guidance_speed_view);
        TextView speedLabelTextView = DataHolder.getActivity().findViewById(R.id.speed_label_text_view);
        guidanceSpeedView.setTextColor(Color.argb(255, 255, 255, 255));
        speedLabelTextView.setTextColor(Color.argb(255, 255, 255, 255));
        if (navigationManager != null) {
            navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.NIGHT);
        }
        map.setMapScheme(map.getMapScheme().replace("day", "night"));
    }

    void satelliteMapOn() {
        if (isNavigating) {
            map.setMapScheme(map.getMapScheme().replace("carnav", "hybrid"));
        } else {
            map.setMapScheme(map.getMapScheme().replace("normal", "hybrid"));
        }
    }

    void trafficMapOn() {
        if (map.getMapScheme().contains("day")) {
            if (map.getMapScheme().contains("hybrid")) {
                map.setMapScheme(Map.Scheme.HYBRID_TRAFFIC_DAY);
            } else if (map.getMapScheme().contains("carnav")) {
                map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
            } else {
                map.setMapScheme(Map.Scheme.NORMAL_TRAFFIC_DAY);
            }
        } else {
            if (map.getMapScheme().contains("hybrid")) {
                map.setMapScheme(Map.Scheme.HYBRID_TRAFFIC_NIGHT);
            } else if (map.getMapScheme().contains("carnav")) {
                map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_NIGHT);
            } else {
                map.setMapScheme(Map.Scheme.NORMAL_TRAFFIC_NIGHT);
            }
        }
    }

    void trafficMapOff() {
        map.setMapScheme(map.getMapScheme().replace("traffic.", ""));
    }

    void satelliteMapOff() {
        if (isNavigating) {
            map.setMapScheme(map.getMapScheme().replace("hybrid", "carnav"));
        } else {
            map.setMapScheme(map.getMapScheme().replace("hybrid", "normal"));
        }
    }

    void lightenMap() {
        TextView guidanceSpeedView = DataHolder.getActivity().findViewById(R.id.guidance_speed_view);
        TextView speedLabelTextView = DataHolder.getActivity().findViewById(R.id.speed_label_text_view);
        guidanceSpeedView.setTextColor(Color.argb(255, 0, 0, 0));
        speedLabelTextView.setTextColor(Color.argb(255, 0, 0, 0));
        if (navigationManager != null) {
            navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        }
        map.setMapScheme(map.getMapScheme().replace("night", "day"));
    }

    void navigationMapOn() {
        map.setMapScheme(map.getMapScheme().replace("normal.", "carnav."));
    }

    void navigationMapOff() {
        map.setMapScheme(map.getMapScheme().replace("carnav.", "normal."));
    }
}
