package com.example.soundaware;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundaware.Adapter.Alert;
import com.example.soundaware.Adapter.AlertAdapter;
import com.example.soundaware.api.connection.ApiClient;
import com.example.soundaware.api.models.audio.AudioResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private static final double LOUD_SOUND_THRESHOLD = 1.0;

    private RecyclerView lastAlertRecycler;
    private RecyclerView historyRecycler;
    private AlertAdapter lastAlertAdapter;
    private AlertAdapter historyAlertAdapter;
    private final List<Alert> lastAlertList = new ArrayList<>();
    private final List<Alert> historyAlertList = new ArrayList<>();

    private ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUIComponents();
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

    private void handleAudioRecording() {
        if (checkAudioPermission()) {
            startScheduledRecording();
        } else {
            requestAudioPermission();
        }
    }

    private void startScheduledRecording() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                File audioFile = createNewAudioFile();
                AudioRecorder recorder = new AudioRecorder(audioFile);

                Log.d("AudioRecorder", "Iniciando grabación...");
                recorder.startRecorder();
                Thread.sleep(RECORDING_INTERVAL_SECONDS * 1000L);
                double currentAmplitude = recorder.getMaxAmplitude();
                recorder.stopRecording();

                Log.d("AudioRecorder", "Amplitud: " + currentAmplitude);

                if (currentAmplitude > LOUD_SOUND_THRESHOLD) {
                    handleLoudSoundDetection(audioFile, currentAmplitude);
                } else {
                    if (!audioFile.delete()) {
                        Log.w("AudioCleanup", "Falla al borrar el archivo de audio.");
                    }
                }
            } catch (Exception e) {
                Log.e("RecordingScheduler", "Error durante grabación", e);
            }
        }, 0, RECORDING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void handleLoudSoundDetection(File audioFile, double amplitude) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Sonido fuerte detectado: " + amplitude, Toast.LENGTH_LONG).show();
            sendAudioToApi(audioFile);
        });
    }

    private void sendAudioToApi(File audioFile) {
        RequestBody requestBody = RequestBody.create(
                audioFile, MediaType.parse("audio/*")
        );

        MultipartBody.Part audioPart = MultipartBody.Part.createFormData(
                "file", audioFile.getName(), requestBody
        );

        ApiClient.uploadAudioFile(audioPart).enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processApiResponse(response.body());
                } else {
                    Log.e("API", "Respuesta no exitosa");
                }
            }

            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
                Log.e("APICommunication", "Falla al subir audio", t);
            }
        });
    }

    private void processApiResponse(AudioResponse response) {
        lastAlertList.clear();
        lastAlertList.add(createAlertFromResponse(response, 0, "last_icon"));
        lastAlertAdapter.notifyDataSetChanged();

        if (historyAlertList.size() >= 5) {
            historyAlertList.remove(historyAlertList.size() - 1);
        }
        historyAlertList.add(0, createAlertFromResponse(response, historyAlertList.size(), "history_icon"));
        historyAlertAdapter.notifyDataSetChanged();
    }

    private Alert createAlertFromResponse(AudioResponse response, int id, String iconType) {
        return new Alert(id, iconType, response.getDate(), response.getClassMessage());
    }

    private File createNewAudioFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir == null) {
            Log.e("FileError", "Directorio de música no disponible");
            throw new RuntimeException("No se puede acceder al almacenamiento externo");
        }

        return new File(dir, "SoundAware_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".wav");
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScheduledRecording();
        } else {
            Toast.makeText(this, "Permiso de audio denegado", Toast.LENGTH_SHORT).show();
        }
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
