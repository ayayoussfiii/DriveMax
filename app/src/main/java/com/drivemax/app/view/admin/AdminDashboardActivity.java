 
package com.drivemax.app.view.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.drivemax.app.databinding.ActivityAdminDashboardBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Client;
import com.drivemax.app.model.entities.Voiture;
import com.drivemax.app.model.entities.Reservation;
import com.drivemax.app.notification.NotificationHelper;
import com.drivemax.app.view.auth.LoginActivity;
import com.drivemax.app.view.auth.SessionManager;
import com.drivemax.app.view.client.MonProfilActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private AppDatabase db;
    private SessionManager sessionManager;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executor = Executors.newSingleThreadExecutor();

        binding.tvWelcome.setText(sessionManager.getUserNom());

        setupDashboard();
        checkRetards();
        setupNavigation();

        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnProfil.setOnClickListener(v ->
                startActivity(new Intent(this, MonProfilActivity.class)));
    }

    private void setupDashboard() {
        executor.execute(() -> {
            int disponibles = db.voitureDao().getVoituresDisponiblesCount();
            int louees = db.voitureDao().getVoituresLoueesCount();
            int enCours = db.reservationDao().getReservationsEnCoursCount();
            int clients = db.clientDao().getClientCount();
            int employes = db.employeDao().getEmployeCount();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            long debutMois = cal.getTimeInMillis();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            long finMois = cal.getTimeInMillis();
            double revenuMois = db.reservationDao().getRevenuPeriode(debutMois, finMois);

            int total = disponibles + louees;
            int taux = total > 0 ? (louees * 100 / total) : 0;

            runOnUiThread(() -> {
                binding.tvVoituresDisponibles.setText(String.valueOf(disponibles));
                binding.tvVoituresLouees.setText(String.valueOf(louees));
                binding.tvReservationsEnCours.setText(String.valueOf(enCours));
                binding.tvTotalClients.setText(String.valueOf(clients));
                binding.tvTotalEmployes.setText(String.valueOf(employes));
                binding.tvRevenuMois.setText(String.format("%.0f MAD", revenuMois));
                binding.progressOccupation.setProgress(taux);
                binding.tvTauxOccupation.setText("Taux occupation: " + taux + "%");
            });
        });
    }

    private void checkRetards() {
        executor.execute(() -> {
            List<Reservation> retards = db.reservationDao()
                    .getReservationsEnRetard(System.currentTimeMillis());
            for (Reservation r : retards) {
                Client client = db.clientDao().getClientById(r.getClientId());
                Voiture voiture = db.voitureDao().getVoitureById(r.getVoitureId());
                if (client != null && voiture != null) {
                    NotificationHelper.sendRetardNotification(this,
                            client.getNomComplet(),
                            voiture.getNomComplet(),
                            com.drivemax.app.utils.DateUtils.getRetardDescription(r.getDateFin()));
                    db.reservationDao().markAlerteEnvoyee(r.getId());
                }
            }
        });
    }

    private void setupNavigation() {
        binding.cardClients.setOnClickListener(v ->
                startActivity(new Intent(this, GestionClientsActivity.class)));
        binding.cardVoitures.setOnClickListener(v ->
                startActivity(new Intent(this, GestionVoituresActivity.class)));
        binding.cardReservations.setOnClickListener(v ->
                startActivity(new Intent(this, ToutesReservationsActivity.class)));
        binding.cardPaiements.setOnClickListener(v ->
                startActivity(new Intent(this, HistoriquePaiementsActivity.class)));
        binding.cardEmployes.setOnClickListener(v ->
                startActivity(new Intent(this, GestionEmployesActivity.class)));
        binding.cardStatistiques.setOnClickListener(v ->
                startActivity(new Intent(this, StatistiquesActivity.class)));
        binding.cardCarteGps.setOnClickListener(v ->
                startActivity(new Intent(this, CarteGpsActivity.class)));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDashboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
