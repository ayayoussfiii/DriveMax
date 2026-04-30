 
package com.drivemax.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.drivemax.app.model.entities.Reservation;
import java.util.List;

@Dao
public interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Reservation reservation);

    @Update
    void update(Reservation reservation);

    @Delete
    void delete(Reservation reservation);

    @Query("SELECT * FROM reservations ORDER BY date_creation DESC")
    LiveData<List<Reservation>> getAllReservations();

    @Query("SELECT * FROM reservations WHERE client_id = :clientId ORDER BY date_creation DESC")
    LiveData<List<Reservation>> getReservationsByClient(String clientId);

    @Query("SELECT * FROM reservations WHERE voiture_id = :voitureId ORDER BY date_creation DESC")
    LiveData<List<Reservation>> getReservationsByVoiture(String voitureId);

    @Query("SELECT * FROM reservations WHERE id = :id")
    Reservation getReservationById(String id);

    @Query("SELECT * FROM reservations WHERE statut = :statut ORDER BY date_creation DESC")
    LiveData<List<Reservation>> getReservationsByStatut(String statut);

    @Query("UPDATE reservations SET statut = :statut WHERE id = :id")
    void updateStatut(String id, String statut);

    @Query("SELECT * FROM reservations WHERE statut = 'EN_COURS' AND date_fin < :now AND alerte_envoyee = 0")
    List<Reservation> getReservationsEnRetard(long now);

    @Query("UPDATE reservations SET alerte_envoyee = 1 WHERE id = :id")
    void markAlerteEnvoyee(String id);

    @Query("SELECT * FROM reservations WHERE synced = 0")
    List<Reservation> getUnsyncedReservations();

    @Query("UPDATE reservations SET synced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("SELECT COUNT(*) FROM reservations WHERE statut = 'EN_COURS'")
    int getReservationsEnCoursCount();

    @Query("SELECT COUNT(*) FROM reservations")
    int getTotalReservations();

    @Query("SELECT SUM(prix_total) FROM reservations WHERE statut = 'TERMINEE' AND date_fin BETWEEN :debut AND :fin")
    double getRevenuPeriode(long debut, long fin);

    @Query("SELECT SUM(prix_total) FROM reservations WHERE statut = 'TERMINEE'")
    double getRevenuTotal();

    @Query("SELECT COUNT(*) FROM reservations WHERE date_debut BETWEEN :debut AND :fin")
    int getReservationsCountPeriode(long debut, long fin);

    @Query("SELECT COUNT(*) FROM reservations WHERE client_id = :clientId")
    int getReservationsCountByClient(String clientId);
}
