package com.drivemax.app.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.drivemax.app.databinding.ActivityContratBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Client;
import com.drivemax.app.model.entities.Reservation;
import com.drivemax.app.model.entities.Voiture;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContratActivity extends AppCompatActivity {

    private ActivityContratBinding binding;
    private AppDatabase db;
    private ExecutorService executor;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContratBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();

        String reservationId = getIntent().getStringExtra("reservation_id");
        String voitureId = getIntent().getStringExtra("voiture_id");
        String clientId = getIntent().getStringExtra("client_id");

        loadContrat(reservationId, voitureId, clientId);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnRetour.setOnClickListener(v -> finish());
        binding.btnConfirmer.setOnClickListener(v -> confirmerContrat());
        binding.btnPrint.setOnClickListener(v ->
            Toast.makeText(this, "Ã°Å¸â€œâ€ž Impression en cours...", Toast.LENGTH_SHORT).show());
    }

    private void loadContrat(String reservationId, String voitureId, String clientId) {
        executor.execute(() -> {
            Reservation r = db.reservationDao().getReservationById(reservationId);
            Voiture v = db.voitureDao().getVoitureById(voitureId);
            Client c = db.clientDao().getClientById(clientId);

            runOnUiThread(() -> {
                if (r == null || v == null || c == null) return;

                // NumÃƒÂ©ro contrat
                binding.tvNumeroContrat.setText("NÃ‚Â° DM-" +
                    new SimpleDateFormat("yyyy", Locale.FRANCE).format(new Date()) +
                    "-" + reservationId.substring(0, 6).toUpperCase());

                // Client
                binding.tvClientNom.setText(c.getNomComplet());
                binding.tvClientCin.setText(c.getCin() != null ? c.getCin() : "Ã¢â‚¬â€ 
