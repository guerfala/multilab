package com.example.multilabmob.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjetPredifiniAdapter extends RecyclerView.Adapter<ObjetPredifiniAdapter.ViewHolder> implements Filterable {
    private List<ObjetPredifini> objetsList;
    private List<ObjetPredifini> objetsListFull; // Backup list for filtering
    private Context context;

    public ObjetPredifiniAdapter(List<ObjetPredifini> objetsList, Context context) {
        this.objetsList = objetsList;
        this.objetsListFull = new ArrayList<>(objetsList); // Copy original list
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objet_predifini, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObjetPredifini objet = objetsList.get(position);
        holder.textViewName.setText(objet.getNom());

        holder.buttonUpdate.setOnClickListener(v -> showUpdateDialog(objet, position));
        holder.buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog(objet, position));
    }

    @Override
    public int getItemCount() {
        return objetsList.size();
    }

    // Implement filter logic
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ObjetPredifini> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(objetsListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (ObjetPredifini item : objetsListFull) {
                        if (item.getNom().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                objetsList.clear();
                objetsList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    private void showDeleteConfirmationDialog(ObjetPredifini objet, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Objet")
                .setMessage("Are you sure you want to delete this objet?")
                .setPositiveButton("Delete", (dialog, which) -> deleteObjetInDatabase(objet.getId(), position))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void deleteObjetInDatabase(int objetId, int position) {
        RetrofitClient.getInstance().getApi().deleteObjetPredifini(objetId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    objetsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, objetsList.size());
                    Toast.makeText(context, "Objet deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete objet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error deleting objet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(ObjetPredifini objet, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Objet");

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_add_objet, null);
        EditText input = viewInflated.findViewById(R.id.editTextObjetNameDialog);
        input.setText(objet.getNom());

        builder.setView(viewInflated);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedName = input.getText().toString().trim();
            if (!updatedName.isEmpty()) {
                updateObjetInDatabase(objet.getId(), updatedName, position);
            } else {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateObjetInDatabase(int objetId, String updatedName, int position) {
        ObjetPredifini updatedObjet = new ObjetPredifini(objetId, updatedName);

        RetrofitClient.getInstance().getApi().updateObjetPredifini(objetId, updatedObjet).enqueue(new Callback<ObjetPredifini>() {
            @Override
            public void onResponse(Call<ObjetPredifini> call, Response<ObjetPredifini> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetsList.set(position, response.body());
                    notifyDataSetChanged();
                    Toast.makeText(context, "Objet updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to update objet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjetPredifini> call, Throwable t) {
                Toast.makeText(context, "Error updating objet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        Button buttonUpdate, buttonDelete;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewObjetName);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
