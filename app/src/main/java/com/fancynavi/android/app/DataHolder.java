package com.fancynavi.android.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;

import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapOffScreenRenderer;
import com.here.android.mpa.mapping.SupportMapFragment;

class DataHolder {
    private static Map map;
    static final String TAG = "HERE_SDK_TEST";
    public static int FOREGROUND_SERVICE_ID = 101;
    public static String CHANNEL = "heresdk";
    public static String CHANNEL_NAME = "HRERSDKTEST";
    private static AppCompatActivity activity;
    private static NavigationManager navigationManager;
    private static PositioningManager positioningManager;
    private static SupportMapFragment supportMapFragment;
    private static MapOffScreenRenderer mapOffScreenRenderer;
    private static NotificationChannel notificationChannel;
    private static NotificationManager notificationManager;

    public static NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public static void setNotificationChannel(NotificationChannel notificationChannel) {
        DataHolder.notificationChannel = notificationChannel;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static void setNotificationManager(NotificationManager notificationManager) {
        DataHolder.notificationManager = notificationManager;
    }

    static MapOffScreenRenderer getMapOffScreenRenderer() {
        return mapOffScreenRenderer;
    }

    static void setMapOffScreenRenderer(MapOffScreenRenderer mapOffScreenRenderer) {
        DataHolder.mapOffScreenRenderer = mapOffScreenRenderer;
    }

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
