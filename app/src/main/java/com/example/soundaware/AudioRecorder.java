package com.example.soundaware;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AudioRecorder {
    private File recAudioFile;
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private int bufferSize;
    private Thread recordingThread;
    private int maxAmplitude = 0;

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    public AudioRecorder(File file) {
        this.recAudioFile = file;
        this.bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    }

    public double getMaxAmplitude() {
        if (maxAmplitude == 0) return 0;
        return 20 * Math.log10((double) maxAmplitude);
    }



    @SuppressLint("MissingPermission")
    public void startRecorder() {
        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
            audioRecord.startRecording();
            isRecording = true;

            recordingThread = new Thread(() -> {
                writeAudioDataToFile();
            });
            recordingThread.start();
        } catch (Exception e) {
            Log.e("AudioRecorder", "Failed to start recording: " + e.getMessage());
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
            isRecording = false;
        }
    }

    private void writeAudioDataToFile() {
        byte[] data = new byte[bufferSize];
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(recAudioFile))) {
            // Write dummy header first
            writeWavHeader(os, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

            int totalAudioLen = 0;
            while (isRecording) {
                int read = audioRecord.read(data, 0, data.length);
                if (read > 0) {
                    os.write(data, 0, read);
                    totalAudioLen += read;

                    // Update max amplitude
                    for (int i = 0; i < read; i += 2) {
                        int sample = (data[i] & 0xFF) | (data[i + 1] << 8);
                        if (sample < 0) sample = -sample;
                        if (sample > maxAmplitude) maxAmplitude = sample;
                    }
                }
            }

            os.flush();
            updateWavHeader(recAudioFile, totalAudioLen);

        } catch (IOException e) {
            Log.e("AudioRecorder", "Error writing audio file", e);
        }
    }

    public void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;
            try {
                recordingThread.join();
            } catch (InterruptedException e) {
                Log.e("AudioRecorder", "Thread join failed", e);
            }
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    public void delete() {
        stopRecording();
        if (recAudioFile != null) {
            recAudioFile.delete();
            recAudioFile = null;
        }
    }

    private void writeWavHeader(BufferedOutputStream out, int sampleRate, int channels, int audioFormat) throws IOException {
        int byteRate = sampleRate * (channels == AudioFormat.CHANNEL_IN_MONO ? 1 : 2) * 2;
        out.write(new byte[] {
                'R', 'I', 'F', 'F', 0, 0, 0, 0,
                'W', 'A', 'V', 'E',
                'f', 'm', 't', ' ', 16, 0, 0, 0,
                1, 0, (byte)(channels == AudioFormat.CHANNEL_IN_MONO ? 1 : 2), 0,
                (byte)(sampleRate & 0xff), (byte)((sampleRate >> 8) & 0xff), (byte)((sampleRate >> 16) & 0xff), (byte)((sampleRate >> 24) & 0xff),
                (byte)(byteRate & 0xff), (byte)((byteRate >> 8) & 0xff), (byte)((byteRate >> 16) & 0xff), (byte)((byteRate >> 24) & 0xff),
                (byte)(2 * (channels == AudioFormat.CHANNEL_IN_MONO ? 1 : 2)), 0, 16, 0,
                'd', 'a', 't', 'a', 0, 0, 0, 0
        });
    }

    private void updateWavHeader(File file, int totalAudioLen) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            int totalDataLen = totalAudioLen + 36;
            raf.seek(4);
            raf.write(new byte[] {
                    (byte)(totalDataLen & 0xff), (byte)((totalDataLen >> 8) & 0xff),
                    (byte)((totalDataLen >> 16) & 0xff), (byte)((totalDataLen >> 24) & 0xff)
            });
            raf.seek(40);
            raf.write(new byte[] {
                    (byte)(totalAudioLen & 0xff), (byte)((totalAudioLen >> 8) & 0xff),
                    (byte)((totalAudioLen >> 16) & 0xff), (byte)((totalAudioLen >> 24) & 0xff)
            });
        }
    }
}
