package com.example.multilabmob.Models;

public class ObjetMission {
    private int id;
    private String cause;
    private ObjetPredifini objetPredifini;
    private Etat etat; // Change from String to Enum

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public ObjetPredifini getObjetPredifini() {
        return objetPredifini;
    }

    public void setObjetPredifini(ObjetPredifini objetPredifini) {
        this.objetPredifini = objetPredifini;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }
}
