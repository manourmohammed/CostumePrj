package com.example.costumeapp.model;

/**
 * Modèle de réponse pour les appels Login et Register.
 * Contient le token JWT et les informations de l'utilisateur.
 */
public class LoginResponse {
    private String access_token; // Le token reçu du serveur
    private String token_type; // Généralement "Bearer"
    private User user; // Détails du compte utilisateur

    public String getAccessToken() {
        return access_token;
    }

    public String getTokenType() {
        return token_type;
    }

    public User getUser() {
        return user;
    }
}
