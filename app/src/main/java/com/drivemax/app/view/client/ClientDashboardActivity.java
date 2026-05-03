 
package com.drivemax.app.view.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.drivemax.app.databinding.ActivityClientDashboardBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Reservation;
import com.drivemax.app.model.sync.SyncManager;
import com.drivemax.app.view.auth.LoginActivity;
import com.drivemax.app.view.auth.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClientDashboardActivity extends AppCompatActivity {

    private ActivityClientDashboardBinding binding;
    private AppDatabase db;
    private SessionManager sessionManager;
    private SyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(0xFF4361EE);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        syncManager = new SyncManager(this);
        syncManager.downloadFromFirestore();

        String nom = sessionManager.getUserNom();
        binding.tvWelcome.setText(nom != null ? nom : "Client");
        binding.tvInitiales.setText(buildInitiales(nom));
        binding.tvLastSync.setText(getSyncText());

        binding.cardCatalogue.setOnClickListener(v ->
                startActivity(new Intent(this, CatalogueActivity.class)));
        binding.cardMesReservations.setOnClickListener(v ->
                startActivity(new Intent(this, MesReservationsActivity.class)));
        binding.cardMesContrats.setOnClickListener(v ->
                startActivity(new Intent(this, MesContratsActivity.class)));
        binding.cardMonProfil.setOnClickListener(v ->
                startActivity(new Intent(this, MonProfilActivity.class)));
        binding.cardReclamations.setOnClickListener(v ->
                startActivity(new Intent(this, ClientReclamationsActivity.class)));
        binding.cardMesPaiements.setOnClickListener(v ->
                startActivity(new Intent(this, MesPaiementsActivity.class)));
        binding.cardNouvelleReservation.setOnClickListener(v ->
                startActivity(new Intent(this, NouvelleReservationActivity.class)));
        binding.cardGps.setOnClickListener(v ->
                startActivity(new Intent(this, ClientCarteGpsActivity.class)));
        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        binding.btnRefresh.setOnClickListener(v -> refreshData());
        binding.swipeRefresh.setOnRefreshListener(this::refreshData);

        loadStats();
        playEntranceAnimation();
    }

    private void loadStats() {
        String clientId = sessionManager.getUserId();
        if (clientId == null) return;

        db.reservationDao().getReservationsByClient(clientId).observe(this, reservations -> {
            if (reservations == null) return;
            long enCours = 0;
            long enAttente = 0;
            long contratsDispos = 0;
            long total = reservations.size();
            for (Reservation r : reservations) {
                if ("EN_COURS".equals(r.getStatut())) enCours++;
                if ("EN_ATTENTE".equals(r.getStatut())) enAttente++;
                if (r.getContratPdfUrl() != null && !r.getContratPdfUrl().trim().isEmpty()) contratsDispos++;
            }
            binding.tvReservationsActives.setText(String.valueOf(enCours));
            binding.tvTotalReservations.setText(String.valueOf(total));
            binding.tvContractsCount.setText("Contrats disponibles : " + contratsDispos);
            binding.tvPendingCount.setText("Reservations en attente : " + enAttente);
        });
    }

    private String buildInitiales(String nom) {
        if (nom == null || nom.trim().isEmpty()) return "CL";
        String[] parts = nom.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) initials.append(Character.toUpperCase(part.charAt(0)));
            if (initials.length() == 2) break;
        }
        return initials.length() > 0 ? initials.toString() : "CL";
    }

    private String getSyncText() {
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
                .format(new Date(System.currentTimeMillis()));
        return "Derniere synchro : " + date;
    }

    private void refreshData() {
        syncManager.downloadFromFirestore();
        binding.tvLastSync.setText(getSyncText());
        binding.swipeRefresh.setRefreshing(false);
    }

    private void playEntranceAnimation() {
        View[] sections = {
                binding.cardKpiActive,
                binding.cardKpiTotal,
                binding.cardInsights,
                binding.cardCatalogue,
                binding.cardMesReservations,
                binding.cardMesContrats,
                binding.cardMonProfil,
                binding.cardReclamations,
                binding.cardMesPaiements,
                binding.cardNouvelleReservation,
                binding.cardGps
        };
        for (int i = 0; i < sections.length; i++) {
            sections[i].setAlpha(0f);
            sections[i].setTranslationY(35f);
            sections[i].animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(420)
                    .setStartDelay(90 + (i * 60L))
                    .setInterpolator(new OvershootInterpolator(1.03f))
                    .start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncManager.shutdown();
    }
}
