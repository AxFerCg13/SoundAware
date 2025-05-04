package com.example.soundaware;

import android.media.MediaRecorder;
import android.util.Log;


import java.io.File;
import java.io.IOException;


/***
 * Clase para manejar la captura de audio
 */
public class AudioRecorder {
    private File RecAudioFile;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;

    public AudioRecorder(File file){
        this.RecAudioFile = file;
    }


    public double getMaxAmplitude() {
        double amplitude = 0;
        if (mMediaRecorder != null) {
            try {
                amplitude = 20 * Math.log10((double)Math.abs(mMediaRecorder.getMaxAmplitude()));
            } catch (IllegalArgumentException e) {
                Log.e("MainActivity", "No se pudo obtener amplitud: " + e.getLocalizedMessage());
            }
        }
        return amplitude;
    }

    public void startRecorder(){
        if (RecAudioFile == null) {
            return;
        }
        try {
            mMediaRecorder = new MediaRecorder();

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(RecAudioFile.getAbsolutePath());



            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecording = true;
        } catch(IOException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            isRecording = false ;
            Log.e("MainActivity", "Fallo la grabacion: " + e.getLocalizedMessage());
        }catch(IllegalStateException e){
            stopRecording();
            Log.e("MainActivity", "Fallo la grabacion: " + e.getLocalizedMessage());
            isRecording = false ;
        }
    }

    public void stopRecording() {
        if (mMediaRecorder != null){
            if(isRecording){
                try{
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                }catch(Exception e){
                    Log.e("MainActivity", "Fallo la grabacion: " + e.getLocalizedMessage());
                }
            }
            mMediaRecorder = null;
            isRecording = false ;
        }
    }

    public void delete() {
        stopRecording();
        if (RecAudioFile != null) {
            RecAudioFile.delete();
            RecAudioFile = null;
        }
    }


}
