package com.example.soundaware;

import android.util.Log;
import android.util.LruCache;

import com.example.soundaware.Adapter.Alert;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class AlertCache {
    private static final int MAX_ALERTS = 6;
    private LruCache<String, String> cache;
    private Gson gson;


    public AlertCache() {
        cache = new LruCache<>(1);
        gson = new Gson();
    }

    public void addAlert(Alert newAlert) {
        List<Alert> alerts = getAlerts();
        if (alerts == null) {
            alerts = new LinkedList<>();
        }

        alerts.add(0, newAlert);

        if (alerts.size() > MAX_ALERTS) {
            alerts = alerts.subList(0, MAX_ALERTS);
        }

        saveAlerts(alerts);
    }

    private void saveAlerts(List<Alert> alerts) {
        String json = gson.toJson(alerts);
        cache.put("alerts", json);
    }

    public List<Alert> getAlerts() {
        String json = cache.get("alerts");
        if (json == null) return new LinkedList<>();  // Retorna lista vacía en vez de null
        Type type = new TypeToken<List<Alert>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

