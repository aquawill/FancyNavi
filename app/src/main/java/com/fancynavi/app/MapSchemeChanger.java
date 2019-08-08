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

    MapSchemeChanger(Map map) {
        this.map = map;
    }

    void darkenMap() {
        if (navigationManager != null) {
            navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.NIGHT);
        }
        map.setMapScheme(map.getMapScheme().replace("day", "night"));
    }

    void satMapOn() {
        map.setMapScheme(map.getMapScheme().replace("normal", "hybrid"));
    }

    void satMapOff() {
        map.setMapScheme(map.getMapScheme().replace("hybrid", "normal"));
    }

    void lightenMap() {
        if (navigationManager != null) {
            navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        }
        map.setMapScheme(map.getMapScheme().replace("night", "day"));
    }
}
