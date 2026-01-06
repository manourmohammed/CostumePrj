package com.example.costumeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.example.costumeapp.R;
import com.example.costumeapp.model.Costume;
import com.example.costumeapp.utils.Constants;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import android.util.Log;

/**
 * Adaptateur pour la liste des costumes dans l'interface d'administration.
 * Il inclut des boutons pour modifier et supprimer chaque costume.
 */
public class AdminCostumeAdapter extends RecyclerView.Adapter<AdminCostumeAdapter.AdminViewHolder> {
    private Context context;
    private List<Costume> costumeList;
    private OnCostumeActionListener listener;

    /**
     * Interface permettant de déléguer les actions (clics sur Edit/Delete)
     * à l'activité qui utilise l'adaptateur.
     */
    public interface OnCostumeActionListener {
        void onEdit(Costume costume);

        void onDelete(Costume costume);
    }

    public AdminCostumeAdapter(Context context, List<Costume> costumeList, OnCostumeActionListener listener) {
        this.context = context;
        this.costumeList = costumeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Chargement du layout spécifique à l'admin (avec boutons d'action)
        View view = LayoutInflater.from(context).inflate(R.layout.item_costume_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Costume costume = costumeList.get(position);

        // Remplissage des champs texte
        holder.tvName.setText(costume.getName());
        holder.tvPrice.setText("$" + costume.getPrice());
        holder.tvId.setText("#" + costume.getId());
        holder.tvDescription.setText(costume.getDescription());
        holder.tvQuantity.setText("Stock: " + costume.getQuantite());
        holder.tvDates.setText(costume.getDateDebut() + " au " + costume.getDateFin());

        // Gestion de l'image avec Picasso (sans cache pour l'admin car les images
        // changent souvent)
        String fullUrl = costume.getFormattedImageUrl();
        if (fullUrl != null && !fullUrl.isEmpty()) {
            final String finalUrl = fullUrl;
            Picasso.get()
                    .load(fullUrl)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.error)
                    .into(holder.ivCostumeImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("PICASSO_DEBUG", "Admin Success: " + finalUrl);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("PICASSO_DEBUG", "Admin Failed: " + finalUrl);
                        }
                    });
        } else {
            Picasso.get()
                    .load(R.drawable.ic_profile_placeholder)
                    .into(holder.ivCostumeImage);
        }

        // Configuration des boutons (Edit / Delete)
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(costume));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(costume));
    }

    @Override
    public int getItemCount() {
        return costumeList.size();
    }

    /**
     * ViewHolder : Conteneur des vues pour un élément de la liste.
     */
    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvId, tvDescription, tvQuantity, tvDates;
        ImageView ivCostumeImage;
        MaterialButton btnEdit, btnDelete;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvId = itemView.findViewById(R.id.tvId);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvDates = itemView.findViewById(R.id.tvDates);
            ivCostumeImage = itemView.findViewById(R.id.ivCostumeImage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
