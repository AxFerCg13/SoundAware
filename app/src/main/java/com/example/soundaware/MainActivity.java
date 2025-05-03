package com.example.soundaware;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(this, "Corriendo en segundo plano", Toast.LENGTH_LONG).show();

        if (!checkPermissions()) requestPermissions();

        //ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        //exec.scheduleWithFixedDelay(new Runnable() {
            //public void run() {
                // codigo a ejecutar repetidas veces
                if(checkPermissions()) {
                    File f = new File(getNewFilePath());
                    AudioRecorder mMediaRecorder = new AudioRecorder(f);
                    boolean recording = mMediaRecorder.startRecorder();

                    try{
                        TimeUnit.SECONDS.sleep(10); //Segundos a grabar por cada corte
                        mMediaRecorder.stopRecording();
                    }catch(Exception e){
                       Log.e("MainActivity", "Fallo la grabacion: " + e.getLocalizedMessage());
                   }


                }
            //}
        //}, 0, 11, TimeUnit.SECONDS);


    }

    private String getNewFilePath() {
        File dir = getExternalFilesDir(null);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String timeStamp = formatter.format(new Date());
        return new File(dir, "SoundAware_" + timeStamp + ".wav").getAbsolutePath();
    }
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
        }
    }

}