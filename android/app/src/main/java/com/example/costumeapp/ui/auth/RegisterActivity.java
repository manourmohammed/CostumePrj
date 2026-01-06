package com.example.costumeapp.ui.auth;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.costumeapp.R;
import com.example.costumeapp.api.ApiService;
import com.example.costumeapp.api.RetrofitClient;
import com.example.costumeapp.model.LoginResponse;
import com.example.costumeapp.ui.main.AdminActivity;
import com.example.costumeapp.ui.main.MainActivity;
import com.example.costumeapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private MaterialButtonToggleGroup toggleRole;
    private MaterialButton btnRegister;
    private TextView tvLogin;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisation API et Session
        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Liaison des vues
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        toggleRole = findViewById(R.id.toggleRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Configuration des boutons
        btnRegister.setOnClickListener(v -> register());
        tvLogin.setOnClickListener(v -> finish()); // Retourne au Login

        // Rôle par défaut sélectionné
        toggleRole.check(R.id.btnRoleUser);

        // Animations d'entrée fluides
        playEntryAnimations();
    }

    /**
     * Animation séquentielle pour un aspect premium.
     */
    private void playEntryAnimations() {
        View[] views = { findViewById(R.id.tvTitle), findViewById(R.id.tvSubtitle),
                findViewById(R.id.tilName), findViewById(R.id.tilEmail), findViewById(R.id.tilPassword),
                findViewById(R.id.tvRoleLabel), toggleRole, btnRegister, tvLogin };

        long startDelay = 100;
        for (View view : views) {
            view.setAlpha(0f);
            view.setTranslationY(100f);

            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(startDelay)
                    .setDuration(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            startDelay += 100;
        }
    }

    /**
     * Logique d'inscription.
     */
    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation des champs
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Le mot de passe doit faire au moins 8 caractères");
            return;
        }

        // Détermination du rôle via le ToggleGroup
        String role = "user";
        int checkedId = toggleRole.getCheckedButtonId();
        if (checkedId == R.id.btnRoleAdmin) {
            role = "admin";
        }

        // État de chargement
        btnRegister.setEnabled(false);
        btnRegister.setText("Création du compte...");

        // Appel API (Envoi des données en FormUrlEncoded)
        Call<LoginResponse> call = apiService.register(name, email, password, role);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("S'inscrire");

                if (response.isSuccessful() && response.body() != null) {
                    // Connexion automatique après inscription réussie
                    String token = "Bearer " + response.body().getAccessToken();
                    String userRole = response.body().getUser().getRole();

                    sessionManager.saveToken(token);
                    sessionManager.saveRole(userRole);

                    navigateBasedOnRole(userRole);
                } else {
                    String errorMsg = "Échec de l'inscription";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " : " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                    }
                    Snackbar.make(btnRegister, errorMsg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("S'inscrire");
                Snackbar.make(btnRegister, "Erreur réseau : " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Redirection après inscription.
     */
    private void navigateBasedOnRole(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(this, AdminActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finishAffinity(); // Ferme toutes les activités précédentes
    }
}
