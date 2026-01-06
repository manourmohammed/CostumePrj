package com.example.costumeapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.costumeapp.model.Costume;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère la liste des costumes favoris/réservés localement sur l'appareil.
 * Utilise SharedPreferences pour persister les données sous forme de JSON (via
 * GSON).
 */
public class FavoritesManager {
    private static final String PREF_NAME = "favorites_prefs";
    private static final String KEY_FAVORITES = "favorite_costumes";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public FavoritesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Ajoute un costume à la liste locale si non présent.
     */
    public void addFavorite(Costume costume) {
        List<Costume> favorites = getFavorites();
        if (!isFavorite(costume.getId())) {
            favorites.add(costume);
            saveFavorites(favorites);
        }
    }

    /**
     * Supprime un costume par son ID.
     */
    public void removeFavorite(int costumeId) {
        List<Costume> favorites = getFavorites();
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getId() == costumeId) {
                favorites.remove(i);
                break;
            }
        }
        saveFavorites(favorites);
    }

    /**
     * Vérifie si un costume est déjà dans les favoris.
     */
    public boolean isFavorite(int costumeId) {
        List<Costume> favorites = getFavorites();
        for (Costume c : favorites) {
            if (c.getId() == costumeId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Récupère la liste complète des favoris déserialisée.
     */
    public List<Costume> getFavorites() {
        String json = sharedPreferences.getString(KEY_FAVORITES, null);
        if (json == null) {
            return new ArrayList<>();
        }
        // Utilisation de TypeToken pour gérer la déserialisation de listes génériques
        Type type = new TypeToken<List<Costume>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Sauvegarde la liste en la convertissant en chaîne JSON.
     */
    private void saveFavorites(List<Costume> favorites) {
        String json = gson.toJson(favorites);
        sharedPreferences.edit().putString(KEY_FAVORITES, json).apply();
    }
}
