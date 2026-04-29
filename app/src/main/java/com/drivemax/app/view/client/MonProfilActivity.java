 
package com.drivemax.app.view.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.drivemax.app.databinding.ActivityMonProfilBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Client;
import com.drivemax.app.model.sync.ConnectivityReceiver;
import com.drivemax.app.model.sync.SyncManager;
import com.drivemax.app.view.auth.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonProfilActivity extends AppCompatActivity {

    private ActivityMonProfilBinding binding;
    private AppDatabase db;
    private ExecutorService executor;
    private SessionManager sessionManager;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMonProfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();
        sessionManager = new SessionManager(this);

        loadProfil();

        binding.btnModifier.setOnClickListener(v -> saveProfil());
    }

    private void loadProfil() {
        executor.execute(() -> {
            client = db.clientDao().getClientById(sessionManager.getUserId());
            runOnUiThread(() -> {
                if (client != null) {
                    String nom = (client.getPrenom() != null ? client.getPrenom() : "")
                            + " " + (client.getNom() != null ? client.getNom() : "");
                    binding.tvNomComplet.setText(nom.trim());
                    binding.tvEmail.setText(client.getEmail() != null ? client.getEmail() : "—");
                    binding.tvTelephone.setText(client.getTelephone() != null ? client.getTelephone() : "—");
                    binding.tvCin.setText(client.getCin() != null ? client.getCin() : "—");
                    binding.tvAdresse.setText(client.getAdresse() != null ? client.getAdresse() : "—");
                    binding.tvRole.setText(sessionManager.getUserRole());

                    // Avatar initiales
                    String initiale = nom.trim().length() > 0 ?
                            String.valueOf(nom.trim().charAt(0)).toUpperCase() : "?";
                    binding.tvAvatar.setText(initiale);
                } else {
                    // Utiliser les infos de session
                    String nom = sessionManager.getUserNom();
                    binding.tvNomComplet.setText(nom);
                    binding.tvEmail.setText(sessionManager.getUserEmail());
                    binding.tvRole.setText(sessionManager.getUserRole());
                    String initiale = nom.length() > 0 ?
                            String.valueOf(nom.charAt(0)).toUpperCase() : "A";
                    binding.tvAvatar.setText(initiale);
                    binding.tvTelephone.setText("—");
                    binding.tvCin.setText("—");
                    binding.tvAdresse.setText("—");
                }
            });
        });
    }

    private void saveProfil() {
        Toast.makeText(this, "Fonctionnalité de modification à venir", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
