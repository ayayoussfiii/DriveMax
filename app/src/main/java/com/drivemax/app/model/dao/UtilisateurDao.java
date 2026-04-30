 
package com.drivemax.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.drivemax.app.model.entities.Utilisateur;
import java.util.List;

@Dao
public interface UtilisateurDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Utilisateur utilisateur);

    @Update
    void update(Utilisateur utilisateur);

    @Delete
    void delete(Utilisateur utilisateur);

    @Query("SELECT * FROM utilisateurs ORDER BY nom ASC")
    LiveData<List<Utilisateur>> getAllUtilisateurs();

    @Query("SELECT * FROM utilisateurs WHERE id = :id")
    Utilisateur getUtilisateurById(String id);

    @Query("SELECT * FROM utilisateurs WHERE email = :email LIMIT 1")
    Utilisateur getUtilisateurByEmail(String email);

    @Query("SELECT * FROM utilisateurs WHERE role = :role")
    List<Utilisateur> getUtilisateursByRole(String role);

    @Query("SELECT * FROM utilisateurs WHERE synced = 0")
    List<Utilisateur> getUnsyncedUtilisateurs();

    @Query("UPDATE utilisateurs SET synced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("UPDATE utilisateurs SET fcm_token = :token WHERE id = :id")
    void updateFcmToken(String id, String token);
}
