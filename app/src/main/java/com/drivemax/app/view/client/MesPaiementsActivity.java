package com.drivemax.app.view.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.drivemax.app.databinding.ActivityMesPaiementsBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Paiement;
import com.drivemax.app.view.adapter.PaiementAdapter;
import com.drivemax.app.view.auth.SessionManager;
import java.util.Locale;

public class MesPaiementsActivity extends AppCompatActivity {

    private ActivityMesPaiementsBinding binding;
    private AppDatabase db;
    private PaiementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMesPaiementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(0xFF4361EE);

        db = AppDatabase.getInstance(this);
        SessionManager sessionManager = new SessionManager(this);

        adapter = new PaiementAdapter();
        binding.recyclerPaiements.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerPaiements.setAdapter(adapter);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnVersMesContrats.setOnClickListener(v ->
                startActivity(new Intent(this, MesContratsActivity.class)));

        db.paiementDao().getPaiementsByClient(sessionManager.getUserId())
                .observe(this, paiements -> {
                    if (paiements == null || paiements.isEmpty()) {
                        binding.layoutEmptyPaiements.setVisibility(View.VISIBLE);
                        binding.recyclerPaiements.setVisibility(View.GONE);
                        binding.tvTotalPaiements.setText("0 MAD");
                        binding.tvNbPaiements.setText("0");
                        binding.tvPaiementsAttente.setText("0");
                        return;
                    }
                    binding.layoutEmptyPaiements.setVisibility(View.GONE);
                    binding.recyclerPaiements.setVisibility(View.VISIBLE);
                    adapter.submitList(paiements);
                    double total = 0;
                    int attente = 0;
                    for (Paiement p : paiements) {
                        if ("PAYE".equals(p.getStatut())) total += p.getMontant();
                        if ("EN_ATTENTE".equals(p.getStatut())) attente++;
                    }
                    binding.tvTotalPaiements.setText(String.format(Locale.getDefault(), "%.0f MAD", total));
                    binding.tvNbPaiements.setText(String.valueOf(paiements.size()));
                    binding.tvPaiementsAttente.setText(String.valueOf(attente));
                });
    }
}
