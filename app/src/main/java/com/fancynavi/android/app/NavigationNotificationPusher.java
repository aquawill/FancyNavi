package com.fancynavi.android.app;

import static com.fancynavi.android.app.DataHolder.CHANNEL;
import static com.fancynavi.android.app.DataHolder.FOREGROUND_SERVICE_ID;
import static com.fancynavi.android.app.DataHolder.TAG;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.util.Log;

import com.here.android.mpa.common.OnScreenCaptureListener;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapOffScreenRenderer;
import com.here.android.mpa.routing.Maneuver;

class NavigationNotificationPusher {
    NavigationNotificationPusher(int maneuverIconId) {

        MapOffScreenRenderer mapOffScreenRenderer = new MapOffScreenRenderer(DataHolder.getActivity());
        mapOffScreenRenderer.setSize(1080, 640);
        mapOffScreenRenderer.setMap(DataHolder.getMap());

        Intent notificationIntent = new Intent(DataHolder.getActivity(), MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(DataHolder.getActivity(), 0, notificationIntent, 0);

        DataHolder.getNavigationManager().setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
        Maneuver nextManeuver = DataHolder.getNavigationManager().getNextManeuver();
        if (nextManeuver != null) {
            DataHolder.getMap().setOrientation(nextManeuver.getMapOrientation());
        }
        new ShiftMapCenter().setTransformCenter(DataHolder.getMap(), 0.5f, 0.5f);
        DataHolder.getMap().setTilt(0);
        DataHolder.getMap().setCenter(nextManeuver.getCoordinate(), Map.Animation.NONE);
        DataHolder.getMap().setZoomLevel(18);

        mapOffScreenRenderer.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mapOffScreenRenderer.getScreenCapture(new OnScreenCaptureListener() {
            @Override
            public void onScreenCaptured(Bitmap bitmap) {
                Log.d(TAG, "onScreenCaptured");
                String nextRoadName = DataHolder.getNavigationManager().getNextManeuver().getNextRoadName();
                Maneuver.Turn turn = DataHolder.getNavigationManager().getNextManeuver().getTurn();
                TurnPresenter turnPresenter = new TurnPresenter(turn);
                String localizedNameOfTurn = turnPresenter.getTurnLocalizedName();
                long distance = DataHolder.getNavigationManager().getNextManeuverDistance();
                String distanceString;
                if (distance >= 1000) {
                    distanceString = distance / 1000 + "." + distance % 1000 + "公里";
                } else {
                    distanceString = distance + "公尺";
                }
                Notification notification =
                        new Notification.Builder(DataHolder.getActivity().getApplicationContext(), CHANNEL)
                                .setSmallIcon(R.mipmap.ic_navigator_round)
                                .setLargeIcon(Icon.createWithResource(DataHolder.getActivity(), maneuverIconId))
                                .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                                .setContentTitle(distanceString)
                                .setContentText(localizedNameOfTurn + "進入" + nextRoadName)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setTicker(localizedNameOfTurn + "進入" + nextRoadName)
                                .build();
                DataHolder.getNotificationManager().cancel(FOREGROUND_SERVICE_ID);
                DataHolder.getNotificationManager().notify(FOREGROUND_SERVICE_ID, notification);
                DataHolder.getNavigationManager().setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW_NOZOOM);
            }
        });
        mapOffScreenRenderer.stop();
    }
}
