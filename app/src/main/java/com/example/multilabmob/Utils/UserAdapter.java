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
    private List<User> filteredList;
    private Context context;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
        this.filteredList = new ArrayList<>(userList);
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
        User user = filteredList.get(position);

        // ✅ Check if data is actually binding to views
        Log.d("ADAPTER_BIND", "Binding user: " + user.getUsername());

        holder.textViewUsername.setText(user.getUsername());
        holder.textViewRole.setText(user.getRole());

        holder.buttonDelete.setOnClickListener(v -> deleteUser(user.getId(), position));
        holder.buttonUpdate.setOnClickListener(v -> showUpdateUserDialog(user, position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (User user : userList) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void deleteUser(int userId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    RetrofitClient.getInstance().getApi().deleteUser(userId).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                userList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
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

    private void showUpdateUserDialog(User user, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        EditText etUsername = dialogView.findViewById(R.id.editTextUsername);
        EditText etPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText etNom = dialogView.findViewById(R.id.editTextNom);
        EditText etPrenom = dialogView.findViewById(R.id.editTextPrenom);
        Spinner spRole = dialogView.findViewById(R.id.spinnerRole);

        etUsername.setText(user.getUsername());
        etNom.setText(user.getNom());
        etPrenom.setText(user.getPrenom());

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

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewRole;
        Button buttonDelete, buttonUpdate;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }
}
