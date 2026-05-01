package com.drivemax.app.view.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.drivemax.app.R;
import com.drivemax.app.databinding.ActivityLoginBinding;
import com.drivemax.app.model.database.AppDatabase;
import com.drivemax.app.model.entities.Utilisateur;
import com.drivemax.app.view.admin.AdminDashboardActivity;
import com.drivemax.app.view.admin.EmployeDashboardActivity;
import com.drivemax.app.view.client.ClientDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private SessionManager sessionManager;
    private AppDatabase localDb;
    private ExecutorService executor;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
            isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);
        localDb = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();
        setupThemeToggle();
        setupListeners();
    }

    private void setupThemeToggle() {
        boolean isDark = prefs.getBoolean("dark_mode", false);
        binding.switchTheme.setChecked(isDark);
        binding.ivThemeIcon.setImageResource(isDark ? R.drawable.ic_moon : R.drawable.ic_sun);
        binding.switchTheme.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            binding.ivThemeIcon.setImageResource(isChecked ? R.drawable.ic_moon : R.drawable.ic_sun);
            AppCompatDelegate.setDefaultNightMode(
                isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            recreate();
        });
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                        .withEndAction(this::attemptLogin).start()
                ).start();
        });
        binding.tvRegister.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));
        binding.tvForgotPassword.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Entrez votre email", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email envoye !", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        });
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) { binding.etEmail.setError("Email requis"); return; }
        if (TextUtils.isEmpty(password)) { binding.etPassword.setError("Mot de passe requis"); return; }
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
                    Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_LONG).show();
                }
            });
    }

    private void loadUserRole(String uid, String email) {
        firestore.collection("utilisateurs").document(uid).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists() && doc.getString("role") != null) {
                    String role = doc.getString("role").toUpperCase();
                    String nom = doc.getString("nom") != null ? doc.getString("nom") : email.split("@")[0];
                    finishLogin(uid, email, role, nom);
                } else {
                    firestore.collection("employes")
                        .whereEqualTo("email", email).get()
                        .addOnSuccessListener(snap -> {
                            if (!snap.isEmpty()) {
                                QueryDocumentSnapshot d = (QueryDocumentSnapshot) snap.getDocuments().get(0);
                                String role = d.getString("role") != null ? d.getString("role").toUpperCase() : "EMPLOYE";
                                String nom = d.getString("prenom") + " " + d.getString("nom");
                                finishLogin(uid, email, role, nom.trim());
                            } else {
                                finishLogin(uid, email, "CLIENT", email.split("@")[0]);
                            }
                        })
                        .addOnFailureListener(e -> finishLogin(uid, email, "CLIENT", email.split("@")[0]));
                }
            })
            .addOnFailureListener(e -> finishLogin(uid, email, "CLIENT", email.split("@")[0]));
    }

    private void finishLogin(String uid, String email, String role, String nom) {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnLogin.setEnabled(true);
        sessionManager.saveSession(uid, email, role, nom);
        Utilisateur u = new Utilisateur(uid, email, nom, "", role);
        u.setSynced(false);
        executor.execute(() -> localDb.utilisateurDao().insert(u));
        android.util.Log.e("LOGIN", "ROLE DETECTE: " + role); redirectByRole(role);
    }

    private void redirectByRole(String role) {
        Intent intent;
        if ("ADMIN".equals(role)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else if ("EMPLOYE".equals(role)) {
            intent = new Intent(this, EmployeDashboardActivity.class);
        } else {
            intent = new Intent(this, ClientDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
