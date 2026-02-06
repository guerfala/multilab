package com.example.multilabmob.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.User;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.*;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private List<User> userListFull;

    private Context context;
    private UserActionListener userActionListener; // ✅ Callback to refresh UsersFragment

    // Callback interface for refreshing users after deletion
    public interface UserActionListener {
        void onUserDeleted();
    }

    public UserAdapter(List<User> userList, UserActionListener listener) {
        this.userList = userList;
        this.userListFull = new ArrayList<>(userList);
        this.userActionListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        context = parent.getContext();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewUsername.setText(user.getUsername());
        holder.textViewRole.setText(user.getRole());

        holder.buttonDelete.setOnClickListener(v -> deleteUser(user, position));
        holder.buttonUpdate.setOnClickListener(v -> showUpdateUserDialog(user));
        holder.buttonStats.setOnClickListener(v -> showStats(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filter(String query) {
        userList.clear(); // Clear the displayed list

        if (query.isEmpty()) {
            // If search query is empty, show the entire list
            userList.addAll(userListFull);
        } else {
            // Otherwise, loop through the backup list
            for (User user : userListFull) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    userList.add(user);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void updateList(List<User> newList) {
        userList.clear();
        userListFull.clear();
        userList.addAll(newList);
        userListFull.addAll(newList);
        notifyDataSetChanged();
    }

    private void deleteUser(User user, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    RetrofitClient.getInstance().getApi().deleteUser(user.getId()).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                userList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, userList.size());

                                Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();

                                // 🔥 Trigger callback to refresh the list in UsersFragment
                                if (userActionListener != null) {
                                    userActionListener.onUserDeleted();
                                }
                            } else {
                                Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
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

    private void showUpdateUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        EditText etUsername = dialogView.findViewById(R.id.editTextUsername);
        EditText etNom = dialogView.findViewById(R.id.editTextNom);
        EditText etPrenom = dialogView.findViewById(R.id.editTextPrenom);
        Spinner spRole = dialogView.findViewById(R.id.spinnerRole);

        etUsername.setText(user.getUsername());
        etNom.setText(user.getNom());
        etPrenom.setText(user.getPrenom());

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                new String[]{"Admin", "Commercial"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        int rolePosition = roleAdapter.getPosition(user.getRole());
        spRole.setSelection(rolePosition != -1 ? rolePosition : 0);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedUsername = etUsername.getText().toString().trim();
            String updatedNom = etNom.getText().toString().trim();
            String updatedPrenom = etPrenom.getText().toString().trim();
            String updatedRole = spRole.getSelectedItem().toString();

            user.setUsername(updatedUsername);
            user.setNom(updatedNom);
            user.setPrenom(updatedPrenom);
            user.setRole(updatedRole);

            RetrofitClient.getInstance().getApi().updateUser(user.getId(), user).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        notifyDataSetChanged();
                        Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show();
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

    private void showStats(User user) {
        RetrofitClient.getInstance().getApi().getOrderStatsForUser(user.getId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject stats = response.body();
                    // Build a message using stats. Default to zero if a key is missing.
                    int realise = stats.has("REALISE") ? stats.get("REALISE").getAsInt() : 0;
                    int nonRealise = stats.has("NONREALISE") ? stats.get("NONREALISE").getAsInt() : 0;
                    int enCours = stats.has("ENCOURS") ? stats.get("ENCOURS").getAsInt() : 0;

                    String message = "REALISE: " + realise + "\n"
                            + "NONREALISE: " + nonRealise + "\n"
                            + "ENCOURS: " + enCours;

                    new AlertDialog.Builder(context)
                            .setTitle("Order Stats for " + user.getUsername())
                            .setMessage(message)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Toast.makeText(context, "Failed to retrieve stats", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewRole;
        Button buttonStats, buttonDelete, buttonUpdate;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
            buttonStats = itemView.findViewById(R.id.buttonStats);
        }
    }
}