package com.example.multilabmob.Models;

public class ObjetPredifini {
    private int id;
    private String nom;

    public ObjetPredifini() {
    }

    public ObjetPredifini(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
