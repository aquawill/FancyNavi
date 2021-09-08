package com.fancynavi.android.app;

import static com.fancynavi.android.app.DataHolder.isNavigating;

import android.graphics.Color;
import android.widget.TextView;

import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;

import java.util.EnumSet;

class MapSchemeChanger {

    private final Map map;
    private NavigationManager navigationManager;
    private boolean landmarkVisible;
    private String mapScheme;

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
        landmarkVisible = map.areLandmarksVisible();
        mapScheme = map.getMapScheme();
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
        map.setLandmarksVisible(landmarkVisible);
        map.setMapScheme(mapScheme);
    }

    void trafficMapOff() {
        landmarkVisible = map.areLandmarksVisible();
        mapScheme = map.getMapScheme();
        map.setMapScheme(map.getMapScheme().replace("traffic.", ""));
        map.setLandmarksVisible(landmarkVisible);
        map.setMapScheme(mapScheme);
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
        if (map.getMapScheme().equals(Map.Scheme.NORMAL_DAY) || map.getMapScheme().equals(Map.Scheme.NORMAL_NIGHT)) {
            map.setMapScheme(map.getMapScheme().replace("normal.", "carnav."));
        } else if (map.getMapScheme().equals(Map.Scheme.TRUCK_DAY) || map.getMapScheme().equals(Map.Scheme.TRUCK_NIGHT)) {
            map.setMapScheme(map.getMapScheme().replace("truck.", "trucknav."));
        }
    }

    void navigationMapOff() {
        DataHolder.getMap().setExtrudedBuildingsVisible(true);
        DataHolder.getMap().setMapScheme(Map.Scheme.NORMAL_DAY);
        DataHolder.getMap().setFleetFeaturesVisible(EnumSet.noneOf(Map.FleetFeature.class));
        DataHolder.getMap().setPedestrianFeaturesVisible(EnumSet.noneOf(Map.PedestrianFeature.class));
    }
}
