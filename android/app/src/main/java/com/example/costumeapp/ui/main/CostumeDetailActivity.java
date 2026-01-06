package com.example.costumeapp.ui.main;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.example.costumeapp.R;
import com.example.costumeapp.model.Costume;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Activité affichant les détails complets d'un costume.
 * Inclut un compte à rebours (Countdown) pour la période de disponibilité.
 */
public class CostumeDetailActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvName, tvPrice, tvDescription, tvQuantity, tvDateDebut, tvDateFin, tvCountdown;
    private Costume costume;
    private CountDownTimer countDownTimer;
    private com.example.costumeapp.utils.FavoritesManager favoritesManager;
    private com.google.android.material.button.MaterialButton btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costume_detail);

        // Récupération de l'objet Costume passé via l'Intent
        costume = (Costume) getIntent().getSerializableExtra("costume");
        if (costume == null) {
            finish();
            return;
        }

        favoritesManager = new com.example.costumeapp.utils.FavoritesManager(this);

        initViews();
        setupToolbar();
        displayDetails();
        startCountdown();

        // Si le costume est déjà dans les favoris/réservés, on verrouille le bouton
        if (favoritesManager.isFavorite(costume.getId())) {
            btnOrder.setText("DÉJÀ RÉSERVÉ");
            btnOrder.setEnabled(false);
            btnOrder.setAlpha(0.6f);
        }

        btnOrder.setOnClickListener(v -> {
            // Ajout aux favoris locaux
            favoritesManager.addFavorite(costume);
            android.widget.Toast.makeText(this, "Costume réservé avec succès !", android.widget.Toast.LENGTH_SHORT)
                    .show();
            // Redirection vers l'écran des favoris
            startActivity(new android.content.Intent(this, FavoritesActivity.class));
        });
    }

    private void initViews() {
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvQuantity = findViewById(R.id.tvDetailQuantity);
        tvDateDebut = findViewById(R.id.tvDetailDateDebut);
        tvDateFin = findViewById(R.id.tvDetailDateFin);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Bouton de retour
            getSupportActionBar().setTitle("");
        }
    }

    /**
     * Remplit les champs textes et charge l'image principale.
     */
    private void displayDetails() {
        tvName.setText(costume.getName());
        tvPrice.setText("$" + String.format(Locale.US, "%.2f", costume.getPrice()));
        tvDescription.setText(costume.getDescription());
        tvQuantity.setText(costume.getQuantite() + " pièces restantes");
        tvDateDebut.setText(costume.getDateDebut());
        tvDateFin.setText(costume.getDateFin());

        Picasso.get()
                .load(costume.getFormattedImageUrl())
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(ivDetailImage);
    }

    /**
     * Gère la logique du compte à rebours dynamique.
     */
    private void startCountdown() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(costume.getDateDebut());
            Date endDate = sdf.parse(costume.getDateFin());
            long currentTime = System.currentTimeMillis();
            long endTime = endDate.getTime();
            long startTime = startDate != null ? startDate.getTime() : 0;

            long diff;
            String statusPrefix = "";

            if (currentTime < startTime) {
                // Événement futur
                diff = startTime - currentTime;
                statusPrefix = "DÉBUTE DANS: ";
                tvCountdown.setTextColor(getResources().getColor(R.color.fashion_accent));
            } else if (currentTime < endTime) {
                // Événement en cours
                diff = endTime - currentTime;
                tvCountdown.setTextColor(getResources().getColor(R.color.fashion_primary));
            } else {
                // Événement passé
                diff = 0;
            }

            if (diff > 0) {
                final String prefix = statusPrefix;
                countDownTimer = new CountDownTimer(diff, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Calcul des jours, heures, minutes, secondes
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24;
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d",
                                days, hours, minutes, seconds);
                        tvCountdown.setText(prefix + timeLeft);
                    }

                    @Override
                    public void onFinish() {
                        tvCountdown.setText("EXPIRED");
                        tvCountdown.setTextColor(getResources().getColor(R.color.fashion_error));
                    }
                }.start();
            } else {
                tvCountdown.setText("EXPIRED");
                tvCountdown.setTextColor(getResources().getColor(R.color.fashion_error));
            }
        } catch (Exception e) {
            Log.e("TIMER_ERROR", "Error parsing date", e);
            tvCountdown.setText("--:--:--:--");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libération des ressources du timer pour éviter les fuites de mémoire
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Gère le bouton Retour dans la barre d'outils
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
