package com.example.soundaware.Adapter;

public class Alert {
    // Attributes at desing
    private int id;

    private String  iconPath;
    private String date;
    private String description;


    public Alert(int id, String iconPath, String date, String description) {
        this.id = id;
        this.iconPath = iconPath;
        this.date = date;
        this.description = description;
    }

    public Alert(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", iconPath='" + iconPath + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
