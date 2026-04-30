 
package com.drivemax.app.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "utilisateurs")
public class Utilisateur {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "nom")
    private String nom;

    @ColumnInfo(name = "prenom")
    private String prenom;

    @ColumnInfo(name = "role")
    private String role; // ADMIN, EMPLOYE, CLIENT

    @ColumnInfo(name = "actif")
    private boolean actif;

    @ColumnInfo(name = "fcm_token")
    private String fcmToken;

    @ColumnInfo(name = "date_creation")
    private long dateCreation;

    @ColumnInfo(name = "synced")
    private boolean synced;

    public Utilisateur() {}

    public Utilisateur(@NonNull String id, String email, String nom, String prenom, String role) {
        this.id = id;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.actif = true;
        this.dateCreation = System.currentTimeMillis();
        this.synced = false;
    }

    // Getters & Setters
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public long getDateCreation() { return dateCreation; }
    public void setDateCreation(long dateCreation) { this.dateCreation = dateCreation; }
    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isEmploye() { return "EMPLOYE".equals(role); }
    public boolean isClient() { return "CLIENT".equals(role); }
    public String getNomComplet() { return prenom + " " + nom; }
}
