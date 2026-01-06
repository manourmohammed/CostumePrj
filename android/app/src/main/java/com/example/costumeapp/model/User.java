package com.example.costumeapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Modèle représentant un utilisateur du système.
 * Utilisé pour l'inscription, la connexion et le stockage en session.
 */
public class User {

    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role; // "admin" ou "user"

    /**
     * Constructeur complet pour l'inscription.
     */
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructeur allégé pour la connexion (Login).
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}
