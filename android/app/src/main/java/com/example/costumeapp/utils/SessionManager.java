package com.example.costumeapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gère la session utilisateur (Token JWT, Rôle, Infos profil).
 * Permet de garder l'utilisateur connecté même après la fermeture de l'app.
 */
public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "CostumeAppPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_NAME = "userName";
    private static final String KEY_EMAIL = "userEmail";

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Sauvegarde du token d'authentification.
     */
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Sauvegarde du rôle (admin ou user).
     */
    public void saveRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "user");
    }

    /**
     * Sauvegarde des infos de base de l'utilisateur.
     */
    public void saveUser(String name, String email) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    /**
     * Supprime toutes les données de session (Déconnexion).
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
