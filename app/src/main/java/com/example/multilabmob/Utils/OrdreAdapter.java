package com.example.multilabmob.Utils;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.ObjetMission;
import com.example.multilabmob.Models.Ordre;
import com.example.multilabmob.R;
import com.example.multilabmob.SettleOrdreActivity;
import com.example.multilabmob.ShowOrdersActivity;

import java.util.List;

public class OrdreAdapter extends RecyclerView.Adapter<OrdreAdapter.OrderViewHolder> {

    private List<Ordre> orders;
    private Activity parentActivity;

    public OrdreAdapter(Activity parentActivity, List<Ordre> orders) {
        this.parentActivity = parentActivity;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Ordre ordre = orders.get(position);

        holder.textViewOrderTitle.setText(ordre.getOrganisme());
        holder.textViewOrderStatus.setText("Status: " + ordre.getStatus());
        holder.textViewOrderDate.setText("Date Debut: " + ordre.getDateDebut());

        // Afficher uniquement le nom des objets de mission
        if (ordre.getObjetMissions() != null && !ordre.getObjetMissions().isEmpty()) {
            StringBuilder objets = new StringBuilder("Objets: ");
            for (ObjetMission objet : ordre.getObjetMissions()) {
                // Vérifier si l'objet prédéfini existe et afficher son nom
                if (objet.getObjetPredifini() != null) {
                    objets.append(objet.getObjetPredifini().getNom()).append(", ");
                }
            }

            // Si des objets ont été ajoutés, supprimer la dernière virgule
            if (objets.length() > 7) {  // "Objets: " fait 7 caractères
                objets.setLength(objets.length() - 2); // Supprimer la dernière virgule et l'espace
            } else {
                objets.append("Aucun objet");
            }

            holder.textViewOrderObjects.setText(objets.toString());
        } else {
            holder.textViewOrderObjects.setText("Objets: Aucun");
        }

        if (ordre.getStatus().equals("REALISE")) {
            holder.buttonSettleOrder.setVisibility(View.GONE);
        } else {
            holder.buttonSettleOrder.setVisibility(View.VISIBLE);
            holder.buttonSettleOrder.setOnClickListener(v -> {
                Intent intent = new Intent(parentActivity, SettleOrdreActivity.class);
                intent.putExtra("ordreId", ordre.getId());
                parentActivity.startActivityForResult(intent, ShowOrdersActivity.REQUEST_CODE_SETTLE_ORDER);
            });
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderTitle;
        TextView textViewOrderStatus;
        TextView textViewOrderDate;
        TextView textViewOrderObjects;
        Button buttonSettleOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderTitle = itemView.findViewById(R.id.textViewOrderTitle);
            textViewOrderStatus = itemView.findViewById(R.id.textViewOrderStatus);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderObjects = itemView.findViewById(R.id.textViewOrderObjects);
            buttonSettleOrder = itemView.findViewById(R.id.buttonSettleOrdre);
        }
    }
}
