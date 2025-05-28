package com.example.soundaware;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
public class NotificationHelper {
    private static final String CHANNEL_ID = "main_alerts";
    private static final String CHANNEL_NAME = "SoundAware_alerts";
    private static final String CHANNEL_DESCRIPTION = "Alerts from SoundAware";

    public NotificationHelper(Context context) {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESCRIPTION);

                NotificationManager manager = context.getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }
        } catch (Exception e){
            Log.e("NotificationHelper", "Failed to start channel: " + e.getMessage());
        }

    }


    public static void showNotification(Context context, String title, String message) {
        int notificationId = (int) (System.currentTimeMillis() / 1000);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.soundaware)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}
