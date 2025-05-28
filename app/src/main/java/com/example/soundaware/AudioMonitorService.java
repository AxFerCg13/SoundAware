package com.example.soundaware;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.soundaware.Adapter.Alert;

public class AudioMonitorService extends Service {

    private AudioManager audioManager;
    private NotificationHelper notifHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        notifHelper = new NotificationHelper(getApplicationContext());
        audioManager = new AudioManager(getApplicationContext(), notifHelper, this::onAlertReceived);

        startForegroundServiceNotification();
        audioManager.startScheduledRecording();
    }

    private void onAlertReceived(Alert alert) {

        Log.d("AudioService", "Alerta generada");
    }

    private void startForegroundServiceNotification() {
        String channelId = "audio_service_channel";
        NotificationChannel channel = new NotificationChannel(channelId, "Audio Service", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("SoundAware en ejecución")
                .setContentText("Monitoreando sonido en segundo plano.")
                .setSmallIcon(R.drawable.soundaware)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AudioService", "Servicio detenido");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

