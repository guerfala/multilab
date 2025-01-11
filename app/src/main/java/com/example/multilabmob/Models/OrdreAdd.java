package com.example.multilabmob.Models;

import java.util.List;

public class OrdreAdd {
    private String organisme; // Name of the organism
    private List<Integer> objetsIds; // List of selected object IDs

    // Constructor
    public OrdreAdd(String organisme, List<Integer> objetsIds) {
        this.organisme = organisme;
        this.objetsIds = objetsIds;
    }

    // Getters and Setters
    public String getOrganisme() {
        return organisme;
    }

    public void setOrganisme(String organisme) {
        this.organisme = organisme;
    }

    public List<Integer> getObjetsIds() {
        return objetsIds;
    }

    public void setObjetsIds(List<Integer> objetsIds) {
        this.objetsIds = objetsIds;
    }
}
