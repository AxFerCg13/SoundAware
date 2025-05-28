package com.example.soundaware;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundaware.Adapter.Alert;
import com.example.soundaware.Adapter.AlertAdapter;
import com.example.soundaware.api.connection.ApiClient;
import com.example.soundaware.api.models.audio.AudioResponse;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 200;
    private static final int RECORDING_INTERVAL_SECONDS = 10;
    private static final double LOUD_SOUND_THRESHOLD = 50.0;

    private RecyclerView lastAlertRecycler;
    private RecyclerView historyRecycler;
    private AlertAdapter lastAlertAdapter;
    private AlertAdapter historyAlertAdapter;
    private final List<Alert> lastAlertList = new ArrayList<>();
    private final List<Alert> historyAlertList = new ArrayList<>();

    private ScheduledExecutorService scheduler;

    private NotificationHelper notifHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUIComponents();
        handleNotification();
        handleAudioRecording();

    }

    private void initializeUIComponents() {
        lastAlertRecycler = findViewById(R.id.recycler_last_alert);
        historyRecycler = findViewById(R.id.recycler_history);

        lastAlertRecycler.setLayoutManager(new LinearLayoutManager(this));
        historyRecycler.setLayoutManager(new LinearLayoutManager(this));

        lastAlertAdapter = new AlertAdapter(lastAlertList, this);
        historyAlertAdapter = new AlertAdapter(historyAlertList, this);

        lastAlertRecycler.setAdapter(lastAlertAdapter);
        historyRecycler.setAdapter(historyAlertAdapter);
    }


    private void handleNotification() {
        if (checkNotifPermission()) {
           Log.e("Notification permission:","Permiso de notificaciones concedido");
        } else {
            requestNotifPermission();
        }
    }

    private void handleAudioRecording() {
        if (checkAudioPermission()) {
            Intent serviceIntent = new Intent(this, AudioMonitorService.class);
            ContextCompat.startForegroundService(this, serviceIntent);

        } else {
            requestAudioPermission();
        }
    }

    private void processApiResponse(AudioResponse response) {
        Alert lastAlert = createAlertFromResponse(response, 0, "last_icon");

        if (lastAlert != null) {

            lastAlertList.clear();
            lastAlertList.add(lastAlert);
            lastAlertAdapter.notifyDataSetChanged();

            if (historyAlertList.size() >= 5) {
                historyAlertList.remove(historyAlertList.size() - 1);
            }

            historyAlertList.add(0, createAlertFromResponse(response, historyAlertList.size(), "history_icon"));
            historyAlertAdapter.notifyDataSetChanged();
        } else {
            Log.d("API_ALERT", "Ignorar alerta");
        }
    }


    private Alert createAlertFromResponse(AudioResponse response, int id, String iconType) {
        //TODO: adaptar el mensaje de la notificacion
        if (response.getIs_alarm() == true) {
            notifHelper.showNotification(this, response.getClassMessage(), response.getDescription());
            return new Alert(id, iconType, response.getDate(), response.getClassMessage(), response.getUrgency_level(), response.getDescription());
        }
        return null;
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkNotifPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_AUDIO_PERMISSION_CODE);
    }

    private void requestNotifPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            Log.d("AudioRecorder", "Scheduler detenido");
        }
    }
}
