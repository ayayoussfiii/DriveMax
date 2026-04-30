package com.drivemax.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.drivemax.app.model.entities.Client;
import java.util.List;

@Dao
public interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Client client);

    @Update
    void update(Client client);

    @Delete
    void delete(Client client);

    @Query("SELECT * FROM clients ORDER BY nom ASC")
    LiveData<List<Client>> getAllClients();

    @Query("SELECT * FROM clients WHERE id = :id")
    Client getClientById(String id);

    @Query("SELECT * FROM clients WHERE email = :email LIMIT 1")
    Client getClientByEmail(String email);

    @Query("SELECT * FROM clients WHERE cin = :cin LIMIT 1")
    Client getClientByCin(String cin);

    @Query("SELECT * FROM clients WHERE nom LIKE '%' || :query || '%' OR prenom LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    LiveData<List<Client>> searchClients(String query);

    @Query("SELECT * FROM clients WHERE synced = 0")
    List<Client> getUnsyncedClients();

    @Query("UPDATE clients SET synced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("SELECT COUNT(*) FROM clients")
    int getClientCount();
} 
