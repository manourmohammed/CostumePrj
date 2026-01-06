package com.example.costumeapp.ui.main;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.costumeapp.R;
import com.example.costumeapp.adapter.AdminCostumeAdapter;
import com.example.costumeapp.api.ApiService;
import com.example.costumeapp.api.RetrofitClient;
import com.example.costumeapp.model.Costume;
import com.example.costumeapp.ui.auth.LoginActivity;
import com.example.costumeapp.utils.FileUtils;
import com.example.costumeapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité de gestion pour les administrateurs.
 * Affiche la liste de tous les costumes avec possibilité de les modifier ou
 * supprimer.
 */
public class AdminManagementActivity extends AppCompatActivity {
    private RecyclerView rvAdminCostumes;
    private AdminCostumeAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);

        // Initialisation des services
        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Configuration de la liste (RecyclerView)
        rvAdminCostumes = findViewById(R.id.rvAdminCostumes);
        rvAdminCostumes.setLayoutManager(new LinearLayoutManager(this));

        // Bouton flottant pour ajouter un nouveau costume
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        });

        // Bouton de déconnexion
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Premier chargement des données
        fetchCostumes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste à chaque retour sur cet écran (ex: après un ajout/modif)
        fetchCostumes();
    }

    /**
     * Récupère la liste des costumes via l'API.
     */
    private void fetchCostumes() {
        String token = "Bearer " + sessionManager.getToken();
        apiService.getCostumes(token).enqueue(new Callback<List<Costume>>() {
            public void onResponse(Call<List<Costume>> call, Response<List<Costume>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Initialisation de l'adaptateur avec les actions Modifier et Supprimer
                    adapter = new AdminCostumeAdapter(AdminManagementActivity.this, response.body(),
                            new AdminCostumeAdapter.OnCostumeActionListener() {
                                @Override
                                public void onEdit(Costume costume) {
                                    // Passage des données à l'activité de formulaire en mode édition
                                    Intent intent = new Intent(AdminManagementActivity.this, AdminActivity.class);
                                    intent.putExtra("costume_id", costume.getId());
                                    intent.putExtra("costume_name", costume.getName());
                                    intent.putExtra("costume_desc", costume.getDescription());
                                    intent.putExtra("costume_price", costume.getPrice());
                                    intent.putExtra("costume_image", costume.getImageUrl());
                                    intent.putExtra("costume_quantite", costume.getQuantite());
                                    intent.putExtra("costume_date_debut", costume.getDateDebut());
                                    intent.putExtra("costume_date_fin", costume.getDateFin());
                                    startActivity(intent);
                                }

                                @Override
                                public void onDelete(Costume costume) {
                                    // Action de suppression immédiate
                                    deleteCostume(costume.getId());
                                }
                            });
                    rvAdminCostumes.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminManagementActivity.this, "Erreur lors de la récupération", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Costume>> call, Throwable t) {
                Toast.makeText(AdminManagementActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Supprime un costume du serveur.
     */
    private void deleteCostume(int id) {
        String token = "Bearer " + sessionManager.getToken();
        apiService.deleteCostume(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminManagementActivity.this, "Costume supprimé avec succès", Toast.LENGTH_SHORT)
                            .show();
                    fetchCostumes(); // Recharger la liste
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminManagementActivity.this, "Échec de suppression", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
