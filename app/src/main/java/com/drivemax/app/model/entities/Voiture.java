 
package com.drivemax.app.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "voitures")
public class Voiture {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "marque")
    private String marque;

    @ColumnInfo(name = "modele")
    private String modele;

    @ColumnInfo(name = "annee")
    private int annee;

    @ColumnInfo(name = "immatriculation")
    private String immatriculation;

    @ColumnInfo(name = "couleur")
    private String couleur;

    @ColumnInfo(name = "prix_journalier")
    private double prixJournalier;

    @ColumnInfo(name = "statut")
    private String statut; // DISPONIBLE, LOUEE, MAINTENANCE

    @ColumnInfo(name = "kilometrage")
    private int kilometrage;

    @ColumnInfo(name = "carburant")
    private String carburant; // ESSENCE, DIESEL, ELECTRIQUE, HYBRIDE

    @ColumnInfo(name = "transmission")
    private String transmission; // MANUELLE, AUTOMATIQUE

    @ColumnInfo(name = "nombre_places")
    private int nombrePlaces;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "synced")
    private boolean synced;

    public Voiture() {}

    public Voiture(@NonNull String id, String marque, String modele, int annee,
                   String immatriculation, double prixJournalier) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.immatriculation = immatriculation;
        this.prixJournalier = prixJournalier;
        this.statut = "DISPONIBLE";
        this.synced = false;
    }

    // Getters & Setters
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public double getPrixJournalier() { return prixJournalier; }
    public void setPrixJournalier(double prixJournalier) { this.prixJournalier = prixJournalier; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getKilometrage() { return kilometrage; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }
    public String getCarburant() { return carburant; }
    public void setCarburant(String carburant) { this.carburant = carburant; }
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public int getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(int nombrePlaces) { this.nombrePlaces = nombrePlaces; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public String getNomComplet() {
        return marque + " " + modele + " (" + annee + ")";
    }

    public boolean isDisponible() {
        return "DISPONIBLE".equals(statut);
    }
}
