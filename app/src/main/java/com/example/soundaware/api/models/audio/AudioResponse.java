package com.example.soundaware.api.models.audio;

public class AudioResponse {
    private String classMessage;
    private String date;
    private double confidence;

    // Getters y Setters
    public String getClassMessage() {
        return classMessage;
    }

    public String getDate() {
        return date;
    }

    public double getConfidence() {
        return confidence;
    }
}