 
package com.drivemax.app.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "clients")
public class Client {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "nom")
    private String nom;

    @ColumnInfo(name = "prenom")
    private String prenom;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "telephone")
    private String telephone;

    @ColumnInfo(name = "cin")
    private String cin;

    @ColumnInfo(name = "permis_numero")
    private String permisNumero;

    @ColumnInfo(name = "adresse")
    private String adresse;

    @ColumnInfo(name = "date_naissance")
    private String dateNaissance;

    @ColumnInfo(name = "fcm_token")
    private String fcmToken;

    @ColumnInfo(name = "date_creation")
    private long dateCreation;

    @ColumnInfo(name = "synced")
    private boolean synced;

    public Client() {}

    public Client(@NonNull String id, String nom, String prenom, String email,
                  String telephone, String cin, String permisNumero, String adresse,
                  String dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.cin = cin;
        this.permisNumero = permisNumero;
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
        this.dateCreation = System.currentTimeMillis();
        this.synced = false;
    }

    // Getters & Setters
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public String getPermisNumero() { return permisNumero; }
    public void setPermisNumero(String permisNumero) { this.permisNumero = permisNumero; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public long getDateCreation() { return dateCreation; }
    public void setDateCreation(long dateCreation) { this.dateCreation = dateCreation; }
    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
