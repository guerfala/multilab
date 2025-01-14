package com.example.multilabmob.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.Ordre;
import com.example.multilabmob.R;

import java.util.List;

public class AdminOrdreAdapter extends RecyclerView.Adapter<AdminOrdreAdapter.OrderViewHolder> {

    private List<Ordre> orders;

    public AdminOrdreAdapter(List<Ordre> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Ordre ordre = orders.get(position);

        holder.textViewOrderTitle.setText("Order: " + ordre.getOrganisme());
        holder.textViewOrderStatus.setText("Status: " + ordre.getStatus());
        holder.textViewOrderDate.setText("Date Start: " + ordre.getDateDebut());
        holder.textViewOrderDateFin.setText("Date End: " + (ordre.getDateFin() != null ? ordre.getDateFin() : "N/A"));
        holder.textViewOrderUser.setText("User: " + ordre.getUsername());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderTitle;
        TextView textViewOrderStatus;
        TextView textViewOrderDate;
        TextView textViewOrderDateFin;
        TextView textViewOrderUser;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderTitle = itemView.findViewById(R.id.textViewOrderTitle);
            textViewOrderStatus = itemView.findViewById(R.id.textViewOrderStatus);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderDateFin = itemView.findViewById(R.id.textViewOrderDateFin);
            textViewOrderUser = itemView.findViewById(R.id.textViewOrderUser);
        }
    }
}
