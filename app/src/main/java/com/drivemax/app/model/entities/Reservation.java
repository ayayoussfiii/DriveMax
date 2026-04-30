 
package com.drivemax.app.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "reservations")
public class Reservation {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "client_id")
    private String clientId;

    @ColumnInfo(name = "voiture_id")
    private String voitureId;

    @ColumnInfo(name = "date_debut")
    private long dateDebut;

    @ColumnInfo(name = "date_fin")
    private long dateFin;

    @ColumnInfo(name = "prix_total")
    private double prixTotal;

    @ColumnInfo(name = "statut")
    private String statut; // EN_ATTENTE, CONFIRMEE, EN_COURS, TERMINEE, ANNULEE

    @ColumnInfo(name = "lieu_prise_en_charge")
    private String lieuPriseEnCharge;

    @ColumnInfo(name = "lieu_retour")
    private String lieuRetour;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "contrat_pdf_url")
    private String contratPdfUrl;

    @ColumnInfo(name = "date_creation")
    private long dateCreation;

    @ColumnInfo(name = "alerte_envoyee")
    private boolean alerteEnvoyee;

    @ColumnInfo(name = "mode_paiement")
    private String modePaiement;

    @ColumnInfo(name = "synced")
    private boolean synced;

    public Reservation() {}

    public Reservation(@NonNull String id, String clientId, String voitureId,
                       long dateDebut, long dateFin, double prixTotal) {
        this.id = id;
        this.clientId = clientId;
        this.voitureId = voitureId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
        this.statut = "EN_ATTENTE";
        this.dateCreation = System.currentTimeMillis();
        this.alerteEnvoyee = false;
        this.synced = false;
    }

    // Getters & Setters
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getVoitureId() { return voitureId; }
    public void setVoitureId(String voitureId) { this.voitureId = voitureId; }
    public long getDateDebut() { return dateDebut; }
    public void setDateDebut(long dateDebut) { this.dateDebut = dateDebut; }
    public long getDateFin() { return dateFin; }
    public void setDateFin(long dateFin) { this.dateFin = dateFin; }
    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getLieuPriseEnCharge() { return lieuPriseEnCharge; }
    public void setLieuPriseEnCharge(String lieuPriseEnCharge) { this.lieuPriseEnCharge = lieuPriseEnCharge; }
    public String getLieuRetour() { return lieuRetour; }
    public void setLieuRetour(String lieuRetour) { this.lieuRetour = lieuRetour; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getContratPdfUrl() { return contratPdfUrl; }
    public void setContratPdfUrl(String contratPdfUrl) { this.contratPdfUrl = contratPdfUrl; }
    public long getDateCreation() { return dateCreation; }
    public void setDateCreation(long dateCreation) { this.dateCreation = dateCreation; }
    public boolean isAlerteEnvoyee() { return alerteEnvoyee; }
    public void setAlerteEnvoyee(boolean alerteEnvoyee) { this.alerteEnvoyee = alerteEnvoyee; }
    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public long getNombreJours() {
        return (dateFin - dateDebut) / (1000 * 60 * 60 * 24);
    }

    public boolean isEnRetard() {
        return "EN_COURS".equals(statut) && System.currentTimeMillis() > dateFin;
    }
    public String getModePaiement() { return modePaiement; }
public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
}
