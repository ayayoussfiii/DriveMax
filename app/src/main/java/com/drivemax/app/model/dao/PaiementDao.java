 
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
PS C:\Users\lenovo\Downloads\DriveMax> Get-Content "C:\Users\lenovo\Downloads\DriveMax\app\src\main\java\com\drivemax\app\model\dao\PaiementDao.java"                                                                                                                                                   
package com.drivemax.app.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.drivemax.app.model.entities.Paiement;
import java.util.List;

@Dao
public interface PaiementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Paiement paiement);

    @Update
    void update(Paiement paiement);

    @Delete
    void delete(Paiement paiement);

    @Query("SELECT * FROM paiements ORDER BY date_paiement DESC")
    LiveData<List<Paiement>> getAllPaiements();

    @Query("SELECT * FROM paiements WHERE client_id = :clientId ORDER BY date_paiement DESC")
    LiveData<List<Paiement>> getPaiementsByClient(String clientId);

    @Query("SELECT * FROM paiements WHERE reservation_id = :reservationId")
    LiveData<List<Paiement>> getPaiementsByReservation(String reservationId);

    @Query("SELECT * FROM paiements WHERE id = :id")
    Paiement getPaiementById(String id);

    @Query("SELECT * FROM paiements WHERE synced = 0")
    List<Paiement> getUnsyncedPaiements();

    @Query("UPDATE paiements SET synced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("SELECT SUM(montant) FROM paiements WHERE statut = 'PAYE' AND date_paiement BETWEEN :debut AND :fin")
    double getTotalPaiements(long debut, long fin);
}
