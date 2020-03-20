/*
 * Copyright (c) 2011-2018 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fancynavi.android.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import static com.fancynavi.android.app.DataHolder.CHANNEL;
import static com.fancynavi.android.app.DataHolder.CHANNEL_NAME;
import static com.fancynavi.android.app.DataHolder.FOREGROUND_SERVICE_ID;


public class ForegroundService extends Service {

    public static String START_ACTION = "com.here.app.tcs.fs.action.start";
    public static String STOP_ACTION = "com.here.app.tcs.fs.action.stop";

    @Override
    public void onCreate() {
        super.onCreate();
        initChannels(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(START_ACTION)) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL)
                            .setContentTitle("Guidance")
                            .setContentText("Guidance in progress ...")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .setLocalOnly(true)
                            .build();

            startForeground(FOREGROUND_SERVICE_ID, notification);
        } else if (intent.getAction().equals(STOP_ACTION)) {
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationChannel.enableVibration(true);
        DataHolder.setNotificationChannel(notificationChannel);
        DataHolder.setNotificationManager(notificationManager);
        DataHolder.getNotificationManager().createNotificationChannel(DataHolder.getNotificationChannel());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}
