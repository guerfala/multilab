package com.example.multilabmob.Models;

import java.util.List;

public class Ordre {
    private int id;
    private String organisme;
    private String status; // Status field (REALISE or NONREALISE)
    private String dateDebut; // DateDebut field as a String for easier parsing in Android

    private String dateFin;
    private List<ObjetMission> objetMissions;
    private String username; // Add the User field for admin purposes

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for organisme
    public String getOrganisme() {
        return organisme;
    }

    public void setOrganisme(String organisme) {
        this.organisme = organisme;
    }

    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter for dateDebut
    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    // Getter and Setter for objetMissions
    public List<ObjetMission> getObjetMissions() {
        return objetMissions;
    }

    public void setObjetMissions(List<ObjetMission> objetMissions) {
        this.objetMissions = objetMissions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }
}
