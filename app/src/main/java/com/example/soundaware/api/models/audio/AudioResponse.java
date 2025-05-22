package com.example.soundaware.api.models.audio;

public class AudioResponse {
    private Boolean is_alarm;
    private String classMessage;
    private double confidence;
    private String date;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String urgency_level;

    // Getters y Setters

    public Boolean getIs_alarm() {
        return is_alarm;
    }

    public void setIs_alarm(Boolean is_alarm) {
        this.is_alarm = is_alarm;
    }

    public String getClassMessage() {
        return classMessage;
    }

    public void setClassMessage(String classMessage) {
        this.classMessage = classMessage;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrgency_level() {
        return urgency_level;
    }

    public void setUrgency_level(String urgency_level) {
        this.urgency_level = urgency_level;
    }
}