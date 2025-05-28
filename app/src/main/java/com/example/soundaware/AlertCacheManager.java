package com.example.soundaware;

import com.example.soundaware.Adapter.Alert;

import java.util.ArrayList;
import java.util.List;

public class AlertCacheManager {
    private static AlertCacheManager instance;
    private final List<Alert> alertList = new ArrayList<>();

    private AlertCacheManager() {}

    public static synchronized AlertCacheManager getInstance() {
        if (instance == null) {
            instance = new AlertCacheManager();
        }
        return instance;
    }

    public synchronized List<Alert> getAlerts() {
        return new ArrayList<>(alertList);
    }

    public synchronized void addAlert(Alert alert) {
        alertList.add(0, alert);
        if (alertList.size() > 6) {
            alertList.remove(alertList.size() - 1);
        }
    }
}

