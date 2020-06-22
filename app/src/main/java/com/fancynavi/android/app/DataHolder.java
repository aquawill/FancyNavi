package com.fancynavi.android.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.PointF;

import androidx.appcompat.app.AppCompatActivity;

import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;

class DataHolder {
    static final String TAG = "HERE_SDK_TEST";
    static int FOREGROUND_SERVICE_ID = 9527;
    static String CHANNEL = "heresdk";
    static String CHANNEL_NAME = "HRERSDKTEST";
    static boolean simpleMode = false;
    static boolean offScreenRendererEnabled = false;


    private static Map map;
    private static AppCompatActivity activity;
    private static NavigationManager navigationManager;
    private static PositioningManager positioningManager;
    private static AndroidXMapFragment androidXMapFragment;
    private static NotificationChannel notificationChannel;
    private static NotificationManager notificationManager;

    static NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public static boolean isOffScreenRendererEnabled() {
        return offScreenRendererEnabled;
    }

    public static void setOffScreenRendererEnabled(boolean offScreenRendererEnabled) {
        DataHolder.offScreenRendererEnabled = offScreenRendererEnabled;
    }

    public static boolean isSimpleMode() {
        return simpleMode;
    }

    public static void setSimpleMode(boolean simpleMode) {
        DataHolder.simpleMode = simpleMode;
    }

    static void setNotificationChannel(NotificationChannel notificationChannel) {
        DataHolder.notificationChannel = notificationChannel;
    }

    static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    static void setNotificationManager(NotificationManager notificationManager) {
        DataHolder.notificationManager = notificationManager;
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

    static AndroidXMapFragment getAndroidXMapFragment() {
        return androidXMapFragment;
    }

    static void setAndroidXMapFragment(AndroidXMapFragment androidXMapFragment) {
        DataHolder.androidXMapFragment = androidXMapFragment;
    }

    static PointF getMapMarkerAnchorPoint(MapMarker mapMarker) {
        int iconHeight = (int) mapMarker.getIcon().getHeight();
        int iconWidth = (int) mapMarker.getIcon().getWidth();
        return new PointF((float) (iconWidth / 2), (float) iconHeight);
    }

    static PointF getMapOverlayAnchorPoint(int width, int height) {
        return new PointF((float) (width / 2), (float) height);
    }
}
