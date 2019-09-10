package com.fancynavi.android.app;

import android.support.v7.app.AppCompatActivity;

import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;

class DataHolder {
    private static Map map;
    private static AppCompatActivity activity;
    private static NavigationManager navigationManager;
    private static PositioningManager positioningManager;
    private static SupportMapFragment supportMapFragment;

    static AppCompatActivity getActivity() {
        return activity;
    }

    static void setActivity(AppCompatActivity activity) {
        DataHolder.activity = activity;
    }

    public static Map getMap() {
        return map;
    }

    public static void setMap(Map map) {
        DataHolder.map = map;
    }

    static NavigationManager getNavigationManager() {
        return navigationManager;
    }

    static void setNavigationManager(NavigationManager navigationManager) {
        DataHolder.navigationManager = navigationManager;
    }

    static PositioningManager getPositioningManager() {
        return positioningManager;
    }

    static void setPositioningManager(PositioningManager positioningManager) {
        DataHolder.positioningManager = positioningManager;
    }

    static SupportMapFragment getSupportMapFragment() {
        return supportMapFragment;
    }

    static void setSupportMapFragment(SupportMapFragment supportMapFragment) {
        DataHolder.supportMapFragment = supportMapFragment;
    }
}
