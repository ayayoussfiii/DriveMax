 
package com.drivemax.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.drivemax.app.model.entities.Voiture;
import java.util.List;

@Dao
public interface VoitureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Voiture voiture);

    @Update
    void update(Voiture voiture);

    @Delete
    void delete(Voiture voiture);

    @Query("SELECT * FROM voitures ORDER BY marque ASC")
    LiveData<List<Voiture>> getAllVoitures();

    @Query("SELECT * FROM voitures WHERE statut = 'DISPONIBLE' ORDER BY prix_journalier ASC")
    LiveData<List<Voiture>> getVoituresDisponibles();

    @Query("SELECT * FROM voitures WHERE id = :id")
    Voiture getVoitureById(String id);

    @Query("SELECT * FROM voitures WHERE statut = :statut")
    LiveData<List<Voiture>> getVoituresByStatut(String statut);

    @Query("UPDATE voitures SET statut = :statut WHERE id = :id")
    void updateStatut(String id, String statut);

    @Query("SELECT * FROM voitures WHERE marque LIKE '%' || :query || '%' OR modele LIKE '%' || :query || '%'")
    LiveData<List<Voiture>> searchVoitures(String query);

    @Query("SELECT * FROM voitures WHERE synced = 0")
    List<Voiture> getUnsyncedVoitures();

    @Query("UPDATE voitures SET synced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("SELECT COUNT(*) FROM voitures WHERE statut = 'DISPONIBLE'")
    int getVoituresDisponiblesCount();

    @Query("SELECT COUNT(*) FROM voitures WHERE statut = 'LOUEE'")
    int getVoituresLoueesCount();

    @Query("UPDATE voitures SET latitude = :lat, longitude = :lng WHERE id = :id")
    void updatePosition(String id, double lat, double lng);
}
