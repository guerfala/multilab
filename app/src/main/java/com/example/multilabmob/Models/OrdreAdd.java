package com.example.multilabmob.Models;

import java.util.List;

public class OrdreAdd {
    private int id;
    private String organisme;
    private int missionId;
    private int userId;
    private List<Integer> objetMissionIds;

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

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getObjetMissionIds() {
        return objetMissionIds;
    }

    public void setObjetMissionIds(List<Integer> objetMissionIds) {
        this.objetMissionIds = objetMissionIds;
    }

    // Getters and Setters
}