package com.example.costumeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.costumeapp.R;
import com.example.costumeapp.model.Costume;
import com.example.costumeapp.ui.main.CostumeDetailActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Adaptateur standard pour afficher les costumes dans la liste principale.
 */
public class CostumeAdapter extends RecyclerView.Adapter<CostumeAdapter.CostumeViewHolder> {
    private Context context;
    private List<Costume> costumeList;

    public CostumeAdapter(Context context, List<Costume> costumeList) {
        this.context = context;
        this.costumeList = costumeList;
    }

    /**
     * Met à jour la liste complète et rafraîchit l'affichage.
     */
    public void setCostumes(List<Costume> newCostumes) {
        this.costumeList = newCostumes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CostumeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Chargement du layout de l'item individuel
        View view = LayoutInflater.from(context).inflate(R.layout.item_costume, parent, false);
        return new CostumeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CostumeViewHolder holder, int position) {
        Costume costume = costumeList.get(position);

        // Affichage des informations brutes
        holder.tvName.setText(costume.getName());
        holder.tvPrice.setText("$" + costume.getPrice());
        holder.tvId.setText("#" + costume.getId());
        holder.tvDescription.setText(costume.getDescription());
        holder.tvQuantity.setText("Quantité: " + costume.getQuantite());
        holder.tvDates.setText("Du " + costume.getDateDebut() + " au " + costume.getDateFin());

        // Chargement de l'image via Picasso avec l'URL formatée
        String fullUrl = costume.getFormattedImageUrl();
        if (fullUrl != null && !fullUrl.isEmpty()) {
            Picasso.get()
                    .load(fullUrl)
                    // On désactive le cache pour voir les modifications immédiatement en dev
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.error)
                    .into(holder.ivCostumeImage);
        } else {
            Picasso.get()
                    .load(R.drawable.ic_profile_placeholder)
                    .into(holder.ivCostumeImage);
        }

        // Redirection vers l'écran de détails lors du clic sur un costume
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CostumeDetailActivity.class);
            intent.putExtra("costume", costume); // Passage de l'objet complet (Serializable)
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return costumeList != null ? costumeList.size() : 0;
    }

    /**
     * ViewHolder simple pour les éléments de la liste.
     */
    public static class CostumeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvId, tvDescription, tvQuantity, tvDates;
        ImageView ivCostumeImage;

        public CostumeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvId = itemView.findViewById(R.id.tvId);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvDates = itemView.findViewById(R.id.tvDates);
            ivCostumeImage = itemView.findViewById(R.id.ivCostumeImage);
        }
    }
}
