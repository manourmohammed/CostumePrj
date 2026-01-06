package com.example.costumeapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.costumeapp.R;
import com.example.costumeapp.ui.auth.LoginActivity;
import com.example.costumeapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

/**
 * Activité affichant les informations du profil de l'utilisateur.
 */
public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvUserName, tvUserEmail;
    private Chip chipRole;
    private MaterialButton btnAdminPanel, btnLogout, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Récupération du gestionnaire de session
        sessionManager = new SessionManager(this);

        // Liaison des composants UI
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        chipRole = findViewById(R.id.chipRole);
        btnAdminPanel = findViewById(R.id.btnAdminPanel);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        // Affichage des données persistées en session
        tvUserName.setText(sessionManager.getName() != null ? sessionManager.getName() : "Utilisateur");
        tvUserEmail.setText(sessionManager.getEmail() != null ? sessionManager.getEmail() : "Pas d'email");

        // Affichage stylisé du rôle avec un Chip
        String role = sessionManager.getRole();
        chipRole.setText(role != null ? role.toUpperCase() : "CLIENT");

        // Bouton spécial Admin : Visible uniquement pour les administrateurs
        if ("admin".equalsIgnoreCase(role)) {
            btnAdminPanel.setVisibility(View.VISIBLE);
        }

        // Actions des boutons
        btnBack.setOnClickListener(v -> finish()); // Retour à l'écran précédent

        btnAdminPanel.setOnClickListener(v -> {
            // Accès direct à la gestion des costumes
            startActivity(new Intent(this, AdminManagementActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Déconnexion : Effacement des préférences et retour au Login
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
