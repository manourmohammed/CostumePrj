package com.example.costumeapp.ui.auth;

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
import com.example.costumeapp.model.User;
import com.example.costumeapp.ui.main.AdminActivity;
import com.example.costumeapp.ui.main.MainActivity;
import com.example.costumeapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des dépendances
        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // AUTO-LOGIN : Si on a déjà un token, on redirige directement vers l'accueil
        if (sessionManager.getToken() != null) {
            navigateBasedOnRole(sessionManager.getRole());
        }

        // Liaison des vues XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Configuration des événements clics
        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        // Animation d'entrée stylée pour les éléments de l'interface
        playEntryAnimations();
    }

    /**
     * Crée un effet de fondu et de montée pour les éléments du login.
     */
    private void playEntryAnimations() {
        View[] views = { findViewById(R.id.tvTitle), findViewById(R.id.tvSubtitle),
                findViewById(R.id.tilEmail), findViewById(R.id.tilPassword),
                btnLogin, tvRegister };

        long startDelay = 100;
        for (View view : views) {
            view.setAlpha(0f);
            view.setTranslationY(60f);

            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(startDelay)
                    .setDuration(500)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            startDelay += 80;
        }
    }

    /**
     * Logique de connexion. Récupère les entrées et appelle l'API.
     */
    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation simple
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // État visuel de chargement
        btnLogin.setEnabled(false);
        btnLogin.setText("Connexion...");

        User user = new User(email, password);
        Call<LoginResponse> call = apiService.login(user);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Se connecter");

                if (response.isSuccessful() && response.body() != null) {
                    // 1. Récupérer les données de la réponse
                    String rawToken = response.body().getAccessToken();
                    String role = response.body().getUser().getRole();

                    // 2. Sauvegarder localement pour les futurs appels
                    sessionManager.saveToken(rawToken);
                    sessionManager.saveRole(role);
                    sessionManager.saveUser(response.body().getUser().getName(), response.body().getUser().getEmail());

                    // 3. Rediriger selon le rôle (Admin ou Client)
                    navigateBasedOnRole(role);
                } else {
                    Snackbar.make(btnLogin, "Email ou mot de passe incorrect", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Se connecter");
                Snackbar.make(btnLogin, "Erreur réseau : " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Redirige l'utilisateur vers son écran dédié et ferme l'activité actuelle.
     */
    private void navigateBasedOnRole(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(this, com.example.costumeapp.ui.main.AdminManagementActivity.class);
        } else {
            intent = new Intent(this, com.example.costumeapp.ui.main.MainActivity.class);
        }
        startActivity(intent);
        finish(); // Supprime l'écran de login de la pile pour ne pas y revenir via "Retour"
    }
}
