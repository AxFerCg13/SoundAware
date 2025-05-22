package com.example.soundaware.Adapter;

public class Alert {
    // Attributes at desing
    private int id;

    private String  iconPath;
    private String date;

    private String classfication;

    private String priority;
    private String description;


    public Alert(int id, String iconPath, String date, String classfication, String priority, String description) {
        this.id = id;
        this.iconPath = iconPath;
        this.date = date;
        this.classfication = classfication;
        this.priority = priority;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClassfication() {
        return classfication;
    }

    public void setClassfication(String classfication) {
        this.classfication = classfication;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", iconPath='" + iconPath + '\'' +
                ", date='" + date + '\'' +
                ", classfication='" + classfication + '\'' +
                ", priority='" + priority + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
