package com.example.costumeapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.costumeapp.R;
import com.example.costumeapp.adapter.CostumeAdapter;
import com.example.costumeapp.api.ApiService;
import com.example.costumeapp.api.RetrofitClient;
import com.example.costumeapp.model.Costume;
import com.example.costumeapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CostumeAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd;
    private com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation de Retrofit (API) et du SessionManager (Préférences)
        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Configuration du RecyclerView (Liste des costumes)
        recyclerView = findViewById(R.id.recyclerView);
        // StaggeredGridLayoutManager permet un affichage type "Pinterest" (colonnes de
        // hauteurs variables)
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.StaggeredGridLayoutManager(2,
                androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL));

        // Initialisation avec une liste vide pour éviter les erreurs avant le
        // chargement
        adapter = new CostumeAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fabAdd = findViewById(R.id.fabAdd);
        // GESTION DES RÔLES :
        // Si l'utilisateur est admin, on affiche le bouton flottant "Ajouter".
        // Sinon, on le cache pour les utilisateurs normaux.
        if ("admin".equals(sessionManager.getRole())) {
            fabAdd.setVisibility(android.view.View.VISIBLE);
            fabAdd.setOnClickListener(v -> {
                // Redirection vers l'interface de gestion admin
                startActivity(new Intent(this, AdminManagementActivity.class));
            });
        } else {
            fabAdd.setVisibility(android.view.View.GONE);
        }

        // Configuration de la barre de navigation du bas
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_favorites) {
                // Aller aux favoris
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                // Aller au profil
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return true;
        });

        // Lancer le chargement des données depuis le serveur
        fetchCostumes();
    }

    private void fetchCostumes() {
        Toast.makeText(this, "Chargement...", Toast.LENGTH_SHORT).show();

        // Ajout du préfixe "Bearer " pour l'authentification JWT
        String token = "Bearer " + sessionManager.getToken();

        // Appel asynchrone à l'API
        Call<List<Costume>> call = apiService.getCostumes(token);

        call.enqueue(new Callback<List<Costume>>() {
            @Override
            public void onResponse(Call<List<Costume>> call, Response<List<Costume>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Succès : Récupération de la liste
                    List<Costume> costumes = response.body();
                    if (costumes.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Aucun costume disponible", Toast.LENGTH_SHORT).show();
                    }
                    // Mise à jour de l'adaptateur pour afficher les items
                    adapter.setCostumes(costumes);
                } else {
                    // Erreur serveur (4xx, 5xx)
                    String errorMsg = "Failed to load costumes: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    android.util.Log.e("API_ERROR", errorMsg);
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Costume>> call, Throwable t) {
                // Erreur réseau (Pas d'internet, serveur éteint, timeout)
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
