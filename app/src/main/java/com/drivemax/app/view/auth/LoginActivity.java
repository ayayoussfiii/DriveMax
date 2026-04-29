 
package com.drivemax.app.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.drivemax.app.databinding.ActivityLoginBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Utilisateur;
import com.drivemax.app.view.admin.AdminDashboardActivity;
import com.drivemax.app.view.client.ClientDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private AppDatabase localDb;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        localDb = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();

        setupListeners();
    }

    private void setupListeners() {

        // Animation + login au clic
        binding.btnLogin.setOnClickListener(v -> {
            v.animate()
                .scaleX(0.95f).scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                        .withEndAction(this::attemptLogin)
                        .start()
                ).start();
        });

        binding.tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // Mot de passe oublié
        binding.tvForgotPassword.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Entrez votre email d'abord", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email de réinitialisation envoyé !", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        });
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email requis");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Mot de passe requis");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        loadUserRole(uid, email);
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnLogin.setEnabled(true);
                        Toast.makeText(this, "Erreur : " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadUserRole(String uid, String email) {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnLogin.setEnabled(true);

        String role = email.contains("admin") ? "ADMIN" : "CLIENT";
        String nom = email.split("@")[0];

        sessionManager.saveSession(uid, email, role, nom);

        Utilisateur u = new Utilisateur(uid, email, nom, "", role);
        u.setSynced(false);
        executor.execute(() -> localDb.utilisateurDao().insert(u));

        redirectByRole(role);
    }

    private void redirectByRole(String role) {
        Intent intent;
        if ("ADMIN".equals(role) || "EMPLOYE".equals(role)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(this, ClientDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
