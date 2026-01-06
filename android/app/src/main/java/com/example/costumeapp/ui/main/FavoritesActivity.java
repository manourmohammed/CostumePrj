package com.example.costumeapp.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.costumeapp.R;
import com.example.costumeapp.adapter.CostumeAdapter;
import com.example.costumeapp.model.Costume;
import com.example.costumeapp.utils.FavoritesManager;
import java.util.List;

/**
 * Activité affichant la liste des costumes réservés par l'utilisateur.
 * Ces données sont stockées localement sur le téléphone.
 */
public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView rvFavorites;
    private TextView tvEmptyMessage;
    private CostumeAdapter adapter;
    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesManager = new FavoritesManager(this);
        rvFavorites = findViewById(R.id.rvFavorites);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        rvFavorites.setLayoutManager(new LinearLayoutManager(this));

        // Chargement initial
        loadFavorites();
    }

    /**
     * Récupère les données depuis le FavoritesManager (SharedPreferences).
     */
    private void loadFavorites() {
        List<Costume> favorites = favoritesManager.getFavorites();
        if (favorites.isEmpty()) {
            // Affichage d'un message si la liste est vide
            tvEmptyMessage.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            rvFavorites.setVisibility(View.VISIBLE);
            // Réutilisation de l'adaptateur standard pour l'affichage
            adapter = new CostumeAdapter(this, favorites);
            rvFavorites.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste si l'utilisateur revient en arrière après une suppression
        loadFavorites();
    }
}
