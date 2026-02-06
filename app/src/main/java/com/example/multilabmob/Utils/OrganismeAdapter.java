package com.example.multilabmob.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.Organisme;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganismeAdapter extends RecyclerView.Adapter<OrganismeAdapter.OrganismeViewHolder> {

    private List<Organisme> organismeList;
    private List<Organisme> organismeListFull; // For search backup
    private Context context;
    private OrganismeActionListener actionListener;

    // Callback for fragment to refresh after delete
    public interface OrganismeActionListener {
        void onOrganismeDeleted();
    }

    public OrganismeAdapter(List<Organisme> organismeList, OrganismeActionListener listener) {
        this.organismeList = new ArrayList<>(organismeList);
        this.organismeListFull = new ArrayList<>(organismeList);
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public OrganismeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organisme, parent, false);
        context = parent.getContext();
        return new OrganismeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganismeViewHolder holder, int position) {
        Organisme organisme = organismeList.get(position);
        holder.textViewLibelle.setText(organisme.getLibelle());

        // Delete button
        holder.buttonDelete.setOnClickListener(v -> deleteOrganisme(organisme, position));

        // Update button
        holder.buttonUpdate.setOnClickListener(v -> showUpdateOrganismeDialog(organisme));
    }

    @Override
    public int getItemCount() {
        return organismeList.size();
    }

    // Filter logic for SearchView
    public void filter(String query) {
        organismeList.clear();
        if (query.isEmpty()) {
            organismeList.addAll(organismeListFull);
        } else {
            for (Organisme org : organismeListFull) {
                if (org.getLibelle().toLowerCase().contains(query.toLowerCase())) {
                    organismeList.add(org);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Update both lists when new data arrives
    public void updateList(List<Organisme> newList) {
        organismeList.clear();
        organismeListFull.clear();
        organismeList.addAll(newList);
        organismeListFull.addAll(newList);
        notifyDataSetChanged();
    }

    // Show dialog to update an organisme
    private void showUpdateOrganismeDialog(Organisme organisme) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_organisme, null);
        builder.setView(dialogView);

        EditText etLibelle = dialogView.findViewById(R.id.editTextLibelle);
        etLibelle.setText(organisme.getLibelle());

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedLibelle = etLibelle.getText().toString().trim();
            organisme.setLibelle(updatedLibelle);

            RetrofitClient.getInstance().getApi().updateOrganisme(organisme.getId(), organisme)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                notifyDataSetChanged();
                                Toast.makeText(context, "Organisme updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to update organisme", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Delete an organisme
    private void deleteOrganisme(Organisme organisme, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Organisme")
                .setMessage("Are you sure you want to delete this organisme?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    RetrofitClient.getInstance().getApi().deleteOrganisme(organisme.getId())
                            .enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if (response.isSuccessful()) {
                                        organismeList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, organismeList.size());
                                        Toast.makeText(context, "Organisme deleted successfully", Toast.LENGTH_SHORT).show();

                                        if (actionListener != null) {
                                            actionListener.onOrganismeDeleted();
                                        }
                                    } else {
                                        Toast.makeText(context, "Failed to delete organisme", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ViewHolder
    public static class OrganismeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLibelle;
        Button buttonUpdate, buttonDelete;

        public OrganismeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLibelle = itemView.findViewById(R.id.textViewLibelle);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdateOrganisme);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteOrganisme);
        }
    }
}
