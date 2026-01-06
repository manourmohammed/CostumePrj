package com.example.costumeapp.utils;

/**
 * Constantes globales de l'application.
 * Définit les adresses serveurs pour l'API et le stockage d'images.
 */
public class Constants {
    // 10.0.2.2 est l'adresse spéciale pour accéder au localhost du PC depuis
    // l'émulateur Android.
    // Pour un test sur appareil physique, il faut mettre l'IP locale du PC (ex:
    // 192.168.1.15).
    public static final String SERVER_IP = "10.0.2.2";
    public static final String BASE_URL = "http://" + SERVER_IP + ":8000";

    // Point d'entrée des requêtes API Retrofit
    public static final String API_URL = BASE_URL + "/api/";

    // Racine URL pour l'affichage des images stockées sur le serveur Laravel
    public static final String STORAGE_URL = BASE_URL + "/storage/costumes/";
}
