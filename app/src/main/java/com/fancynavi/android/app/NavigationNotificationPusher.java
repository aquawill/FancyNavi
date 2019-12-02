package com.fancynavi.android.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.here.android.mpa.common.OnScreenCaptureListener;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.routing.Maneuver;

import static android.content.Context.NOTIFICATION_SERVICE;

class NavigationNotificationPusher {
    NavigationNotificationPusher() {
        int NOTIFICATION_ID = 0;
        String CHANNEL_ID = "heresdk";

        AppCompatActivity appCompatActivity = DataHolder.getActivity();
        NavigationManager navigationManager = DataHolder.getNavigationManager();
        Map map = DataHolder.getMap();
        Maneuver nextManeuver = DataHolder.getNavigationManager().getNextManeuver();
        DataHolder.getMapOffScreenRenderer().start();
        navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
        map.setOrientation(nextManeuver.getMapOrientation());
        new ShiftMapCenter(map, 0.5f, 0.5f);
        map.setTilt(0);
        map.setCenter(nextManeuver.getCoordinate(), Map.Animation.NONE);
        map.setZoomLevel(18);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                DataHolder.getMapOffScreenRenderer().getScreenCapture(new OnScreenCaptureListener() {
                    @Override
                    public void onScreenCaptured(Bitmap bitmap) {
                        Log.d("test", "onScreenCaptured");
//                        new ScreenCapturer(bitmap, appCompatActivity);
                        String nextRoadName = navigationManager.getNextManeuver().getNextRoadName();
                        Maneuver.Turn turn = navigationManager.getNextManeuver().getTurn();
                        TurnPresenter turnPresenter = new TurnPresenter(turn);
                        String localizedNameOfTurn = turnPresenter.getTurnLocalizedName();
                        long distance = navigationManager.getNextManeuverDistance();
                        String distanceString;
                        if (distance >= 1000) {
                            distanceString = distance / 1000 + "." + distance % 1000 + "公里";
                        } else {
                            distanceString = distance + "公尺";
                        }
                        NotificationChannel notificationChannel = new NotificationChannel(
                                "heresdk",
                                "HERE_SDK_TEST",
                                NotificationManager.IMPORTANCE_HIGH);
                        NotificationManager notificationManager = (NotificationManager) appCompatActivity.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.createNotificationChannel(notificationChannel);
                        Notification.Builder builder = new Notification.Builder(appCompatActivity);
                        Intent intent = new Intent(appCompatActivity, MainActivity.class);
                        intent.putExtra("yourpackage.notifyId", NOTIFICATION_ID);
                        PendingIntent pendingIntent = PendingIntent.getActivity(appCompatActivity, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(pendingIntent)
                                .setLargeIcon(bitmap)
                                .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                                .setContentTitle(distanceString)
                                .setContentText(localizedNameOfTurn + "進入" + nextRoadName)
                                .setChannelId(CHANNEL_ID);
                        notificationManager.cancel(NOTIFICATION_ID);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                        DataHolder.getMapOffScreenRenderer().stop();
                    }
                });
            }
        }, 100);
    }
}
