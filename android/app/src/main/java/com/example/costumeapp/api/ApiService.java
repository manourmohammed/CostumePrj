package com.example.costumeapp.api;

import com.example.costumeapp.model.LoginResponse;
import com.example.costumeapp.model.User;
import com.example.costumeapp.model.Costume; // Will create later
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface ApiService {
        // --- Authentification ---

        /**
         * Endpoint pour la connexion.
         * Envoie un objet JSON {email, password} dans le corps de la requête.
         * Retourne un token JWT et les infos utilisateur.
         */
        @POST("login")
        Call<LoginResponse> login(@Body User user);

        /**
         * Endpoint pour l'inscription.
         * Utilise FormUrlEncoded pour envoyer les données comme un formulaire HTML
         * classique.
         * 
         * @param role : "admin" ou "user"
         */
        @POST("register")
        Call<LoginResponse> register(
                        @retrofit2.http.Field("name") String name,
                        @retrofit2.http.Field("email") String email,
                        @retrofit2.http.Field("password") String password,
                        @retrofit2.http.Field("role") String role);

        // --- Gestion des Costumes ---

        /**
         * Récupère la liste de tous les costumes.
         * Nécessite le Header Authorization avec le token Bearer.
         */
        @GET("costumes")
        Call<List<Costume>> getCostumes(@Header("Authorization") String token);

        /**
         * Création d'un costume avec image (Multipart).
         * 
         * @Part : Chaque champ doit être converti en RequestBody.
         * @Part MultipartBody.Part : Contient le fichier image.
         */
        @Multipart
        @POST("costumes")
        Call<Costume> createCostumeMultipart(
                        @Header("Authorization") String token,
                        @Part("name") RequestBody name,
                        @Part("description") RequestBody description,
                        @Part("price") RequestBody price,
                        @Part("quantite") RequestBody quantite,
                        @Part("date_debut") RequestBody dateDebut,
                        @Part("date_fin") RequestBody dateFin,
                        @Part MultipartBody.Part image);

        /**
         * Mise à jour d'un costume (Multipart).
         * Note: Laravel ne supporte pas bien PUT en Multipart direct,
         * on utilise souvent POST avec le champ _method="PUT".
         */
        @Multipart
        @POST("costumes/{id}")
        Call<Costume> updateCostumeMultipart(
                        @Header("Authorization") String token,
                        @Path("id") int id,
                        @Part("_method") RequestBody method, // Astuce Laravel : envoyer "PUT" ici
                        @Part("name") RequestBody name,
                        @Part("description") RequestBody description,
                        @Part("price") RequestBody price,
                        @Part("quantite") RequestBody quantite,
                        @Part("date_debut") RequestBody dateDebut,
                        @Part("date_fin") RequestBody dateFin,
                        @Part MultipartBody.Part image);

        /**
         * Suppression d'un costume par son ID.
         */
        @retrofit2.http.DELETE("costumes/{id}")
        Call<Void> deleteCostume(@Header("Authorization") String token, @Path("id") int id);
}
