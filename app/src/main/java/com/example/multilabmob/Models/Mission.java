package com.example.multilabmob.Models;

import java.util.List;

public class Mission {
    private int id;
    private String organisme;
    private User user;
    private String date;
    private List<Integer> objets; // List of selected ObjetPredifini IDs

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
}
