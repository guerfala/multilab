package com.example.multilabmob.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mission implements Serializable {
    private int id;
    private String organisme;
    private User user;
    private String date;
    private List<Integer> objets; // List of selected ObjetPredifini IDs
    private String fcmToken;

    public <E> Mission(int i, String missionAlpha, Object o, String date, ArrayList<E> es, String s) {
    }

    public Mission() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrganisme() {
        return organisme;
    }

    public void setOrganisme(String organisme) {
        this.organisme = organisme;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getObjets() {
        return objets;
    }

    public void setObjets(List<Integer> objets) {
        this.objets = objets;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "Mission: " + organisme + " on " + date;
    }

}
