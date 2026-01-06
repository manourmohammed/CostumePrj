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

import com.example.costumeapp.R;
import com.example.costumeapp.api.ApiService;
import com.example.costumeapp.api.RetrofitClient;
import com.example.costumeapp.model.Costume;
import com.example.costumeapp.ui.auth.LoginActivity;
import com.example.costumeapp.utils.Constants;
import com.example.costumeapp.utils.SessionManager;
import com.example.costumeapp.utils.FileUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import android.util.Log;
import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Formulaire d'ajout et de modification de costume.
 * Gère le choix d'image, la saisie des dates via calendrier et l'upload
 * multipart.
 */
public class AdminActivity extends AppCompatActivity {

    private EditText etName, etDescription, etPrice, etDateDebut, etDateFin, etQuantite;
    private MaterialButton btnAdd, btnBack, btnLogout;
    private MaterialCardView cardImagePicker;
    private ImageView ivCostumePreview;
    private LinearLayout layoutPlaceholder;

    private ApiService apiService;
    private SessionManager sessionManager;

    private String localImageUri = "";
    private String remoteImageUrl = "";

    private int costumeId = -1;
    private boolean isEditMode = false;

    // Registre pour la sélection d'image dans la galerie du téléphone
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    localImageUri = uri.toString();
                    layoutPlaceholder.setVisibility(View.GONE);

                    // Aperçu immédiat de l'image sélectionnée
                    Picasso.get()
                            .load(uri)
                            .centerCrop()
                            .resize(800, 800)
                            .onlyScaleDown()
                            .into(ivCostumePreview);

                    Toast.makeText(this, "Image sélectionnée", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Liaison des vues
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etDateDebut = findViewById(R.id.etDateDebut);
        etDateFin = findViewById(R.id.etDateFin);
        etQuantite = findViewById(R.id.etQuantite);

        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        cardImagePicker = findViewById(R.id.cardImagePicker);
        ivCostumePreview = findViewById(R.id.ivCostumePreview);
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder);

        // MODE ÉDITION : Si un ID est passé en intent, on pré-remplit le formulaire
        if (getIntent().hasExtra("costume_id")) {
            isEditMode = true;
            costumeId = getIntent().getIntExtra("costume_id", -1);

            etName.setText(getIntent().getStringExtra("costume_name"));
            etDescription.setText(getIntent().getStringExtra("costume_desc"));
            etPrice.setText(String.valueOf(getIntent().getDoubleExtra("costume_price", 0)));
            etQuantite.setText(String.valueOf(getIntent().getIntExtra("costume_quantite", 0)));
            etDateDebut.setText(getIntent().getStringExtra("costume_date_debut"));
            etDateFin.setText(getIntent().getStringExtra("costume_date_fin"));

            remoteImageUrl = getIntent().getStringExtra("costume_image");

            if (remoteImageUrl != null && !remoteImageUrl.isEmpty()) {
                // Utilisation d'un objet temporaire pour formater l'URL (logic complexe)
                Costume temp = new Costume(
                        getIntent().getStringExtra("costume_name"),
                        getIntent().getStringExtra("costume_desc"),
                        remoteImageUrl,
                        getIntent().getDoubleExtra("costume_price", 0),
                        getIntent().getStringExtra("costume_date_debut"),
                        getIntent().getStringExtra("costume_date_fin"),
                        getIntent().getIntExtra("costume_quantite", 0));

                String fullUrl = temp.getFormattedImageUrl();
                layoutPlaceholder.setVisibility(View.GONE);

                Picasso.get()
                        .load(fullUrl)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(ivCostumePreview);
            }
            btnAdd.setText("Mettre à jour");
        }

        // Configuration des événements
        cardImagePicker.setOnClickListener(v -> pickImage.launch("image/*"));
        etDateDebut.setOnClickListener(v -> showDatePickerDialog(etDateDebut));
        etDateFin.setOnClickListener(v -> showDatePickerDialog(etDateFin));

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnAdd.setOnClickListener(v -> handleSave());
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Rassemble les données et lance l'appel API Multipart (Texte + Image).
     */
    private void handleSave() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantiteStr = etQuantite.getText().toString().trim();
        String dDebut = etDateDebut.getText().toString().trim();
        String dFin = etDateFin.getText().toString().trim();

        // Validation basique
        if (name.isEmpty() || priceStr.isEmpty() || quantiteStr.isEmpty()) {
            Toast.makeText(this, "Champs obligatoires manquants", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + sessionManager.getToken();

        // Conversion des textes en RequestBody (requis pour @Multipart Retrofit)
        RequestBody namePart = text(name);
        RequestBody descPart = text(description);
        RequestBody pricePart = text(priceStr);
        RequestBody quantitePart = text(quantiteStr);
        RequestBody dDebutPart = text(dDebut);
        RequestBody dFinPart = text(dFin);

        MultipartBody.Part imagePart = null;

        // Préparation du fichier image si sélectionné
        if (!localImageUri.isEmpty()) {
            File file = FileUtils.getFileFromUri(this, Uri.parse(localImageUri));
            if (file != null) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), fileBody);
            }
        }

        Call<Costume> call;

        if (isEditMode) {
            // Pour l'UPDATE, Laravel nécessite souvent un POST avec un champ "_method" à
            // "PUT"
            RequestBody methodPart = text("PUT");
            call = apiService.updateCostumeMultipart(
                    token, costumeId, methodPart,
                    namePart, descPart, pricePart,
                    quantitePart, dDebutPart, dFinPart, imagePart);
        } else {
            // Création simple
            if (imagePart == null) {
                Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show();
                return;
            }

            call = apiService.createCostumeMultipart(
                    token, namePart, descPart, pricePart,
                    quantitePart, dDebutPart, dFinPart, imagePart);
        }

        call.enqueue(new Callback<Costume>() {
            @Override
            public void onResponse(Call<Costume> call, Response<Costume> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminActivity.this,
                            isEditMode ? "Mis à jour avec succès" : "Ajout réussi",
                            Toast.LENGTH_SHORT).show();
                    finish(); // Fermer l'activité après succès
                } else {
                    Toast.makeText(AdminActivity.this, "Erreur serveur : " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Costume> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Aide pour créer un RequestBody texte simple.
     */
    private RequestBody text(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value == null ? "" : value);
    }

    /**
     * Affiche un sélecteur de date (Calendrier) et remplit le champ EditText
     * associé.
     */
    private void showDatePickerDialog(EditText editText) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = year + "-" +
                    String.format(Locale.getDefault(), "%02d", month + 1) + "-" +
                    String.format(Locale.getDefault(), "%02d", day);
            editText.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}
