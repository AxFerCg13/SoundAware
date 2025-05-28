package com.example.soundaware;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.soundaware.Adapter.Alert;
import com.example.soundaware.api.connection.ApiClient;
import com.example.soundaware.api.models.audio.AudioResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioManager {
    private static final int RECORDING_INTERVAL_SECONDS = 10;
    private static final double LOUD_SOUND_THRESHOLD = 50.0;

    private final Context context;
    private final NotificationHelper notifHelper;
    private final Consumer<Alert> onAlertReceived;

    public AudioManager(Context context, NotificationHelper notifHelper, Consumer<Alert> onAlertReceived) {
        this.context = context.getApplicationContext();;
        this.notifHelper = notifHelper;
        this.onAlertReceived = onAlertReceived;
    }

    public void startScheduledRecording() {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        exec.scheduleWithFixedDelay(() -> {
            try {
                File audioFile = createNewAudioFile();
                AudioRecorder recorder = new AudioRecorder(audioFile);

                recorder.startRecorder();
                double currentAmplitude = recorder.getMaxAmplitude();
                Thread.sleep(RECORDING_INTERVAL_SECONDS * 1000L);
                currentAmplitude = recorder.getMaxAmplitude();
                recorder.stopRecording();

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
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, "Sonido fuerte detectado: " + amplitude, Toast.LENGTH_LONG).show()
        );

        sendAudioToApi(audioFile);
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
                    processApiResponse(response.body(), 0, "last_icon");
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

    private void processApiResponse(AudioResponse response, int id, String iconType) {
        if (response.getIs_alarm()) {
            notifHelper.showNotification(context, response.getClassMessage(), response.getDescription());

            Alert alert = new Alert(id, iconType, response.getDate(), response.getClassMessage(), response.getUrgency_level(), response.getDescription());

            //TODO: Almacenar en la BD
        }
    }

    private File createNewAudioFile() {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir == null) {
            throw new RuntimeException("No se puede acceder al almacenamiento externo");
        }

        return new File(dir, "SoundAware_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".wav");
    }
}

