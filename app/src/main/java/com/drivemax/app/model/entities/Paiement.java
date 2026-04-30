 
package com.drivemax.app.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "paiements")
public class Paiement {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "reservation_id")
    private String reservationId;

    @ColumnInfo(name = "client_id")
    private String clientId;

    @ColumnInfo(name = "montant")
    private double montant;

    @ColumnInfo(name = "methode")
    private String methode; // ESPECES, CARTE, VIREMENT, CHEQUE

    @ColumnInfo(name = "statut")
    private String statut; // EN_ATTENTE, PAYE, REMBOURSE, ANNULE

    @ColumnInfo(name = "date_paiement")
    private long datePaiement;

    @ColumnInfo(name = "reference")
    private String reference;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "synced")
    private boolean synced;

    public Paiement() {}

    public Paiement(@NonNull String id, String reservationId, String clientId,
                    double montant, String methode) {
        this.id = id;
        this.reservationId = reservationId;
        this.clientId = clientId;
        this.montant = montant;
        this.methode = methode;
        this.statut = "EN_ATTENTE";
        this.datePaiement = System.currentTimeMillis();
        this.synced = false;
    }

    // Getters & Setters
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
    public String getMethode() { return methode; }
    public void setMethode(String methode) { this.methode = methode; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public long getDatePaiement() { return datePaiement; }
    public void setDatePaiement(long datePaiement) { this.datePaiement = datePaiement; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }
}
