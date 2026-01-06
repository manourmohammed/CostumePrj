package com.example.costumeapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import com.example.costumeapp.utils.Constants;

public class Costume implements Serializable {
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("price")
    private double price;

    @SerializedName("date_debut")
    private String dateDebut;

    @SerializedName("date_fin")
    private String dateFin;

    @SerializedName("quantite")
    private int quantite;

    public Costume(String name, String description, String imageUrl, double price, String dateDebut, String dateFin,
            int quantite) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Retourne l'URL brute stockée en base de données
    public String getImageUrl() {
        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.equals("null")) {
            return null;
        }
        return imageUrl;
    }

    /**
     * Méthode CRITIQUE pour l'affichage des images.
     * Cette méthode nettoie et formate l'URL de l'image pour s'assurer qu'elle est
     * accessible
     * depuis l'émulateur Android ou un appareil réel.
     */
    public String getFormattedImageUrl() {
        String url = getImageUrl();
        // Si aucune image n'est définie, on retourne vide (l'UI affichera un
        // placeholder)
        if (url == null || url.isEmpty() || url.equals("null"))
            return "";

        // CAS 1 : L'URL est déjà complète (commence par http/https)
        if (url.toLowerCase().startsWith("http")) {
            // CORRECTION EMULATEUR :
            // L'émulateur Android ne connait pas "localhost" (qui est lui-même).
            // Il faut remplacer "localhost" ou "127.0.0.1" par "10.0.2.2" (l'IP de l'hôte
            // vu par l'émulateur).
            if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", Constants.SERVER_IP);
            } else if (url.contains("localhost")) {
                url = url.replace("localhost", Constants.SERVER_IP);
            }
            return url;
        }

        // CAS 2 : C'est une image locale temporaire (avant upload, sélectionnée depuis
        // la galerie)
        if (url.toLowerCase().startsWith("content://") || url.toLowerCase().startsWith("file://")) {
            return url;
        }

        // CAS 3 : C'est un chemin relatif venant du serveur (ex:
        // "public/costumes/image.jpg")
        // Nettoyage agressif pour ne garder que le nom de fichier final

        // 1. Supprimer les slashes de début
        while (url.startsWith("/")) {
            url = url.substring(1);
        }

        // 2. Supprimer le préfixe "public/" s'il existe (insensible à la casse)
        if (url.toLowerCase().startsWith("public/")) {
            url = url.substring(7);
        }

        // 3. Supprimer le préfixe "storage/" s'il existe
        if (url.toLowerCase().startsWith("storage/")) {
            url = url.substring(8);
        }

        // 4. Supprimer le dossier "costumes/" car on l'ajoute déjà dans
        // Constants.STORAGE_URL
        if (url.toLowerCase().startsWith("costumes/")) {
            url = url.substring(9);
        }

        // 5. Un dernier nettoyage des slashes au cas où
        while (url.startsWith("/")) {
            url = url.substring(1);
        }

        if (url.isEmpty())
            return "";

        // Construction de l'URL finale propre :
        // http://10.0.2.2:8000/storage/costumes/mon_image.jpg
        String finalUrl = Constants.STORAGE_URL + url;
        android.util.Log.d("IMAGE_DEBUG", "Original: [" + getImageUrl() + "] -> Formatted: [" + finalUrl + "]");
        return finalUrl;
    }

    public double getPrice() {
        return price;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public int getQuantite() {
        return quantite;
    }
}
