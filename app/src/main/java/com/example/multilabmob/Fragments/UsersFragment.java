package com.example.multilabmob.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.User;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;
import com.example.multilabmob.Utils.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.*;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private SearchView searchView;
    private FloatingActionButton fabAddUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView = view.findViewById(R.id.searchViewUsers);
        fabAddUser = view.findViewById(R.id.fabAddUser);

        userAdapter = new UserAdapter(userList, this::loadUsers); // ✅ Auto-refresh after delete
        recyclerView.setAdapter(userAdapter);

        loadUsers(); // ✅ Load users after adapter is set

        fabAddUser.setOnClickListener(v -> showAddUserDialog());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.filter(newText);
                return false;
            }
        });

        return view;
    }

    // ✅ Load users from API and refresh RecyclerView
    public void loadUsers() {
        RetrofitClient.getInstance().getApi().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Error loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Show Add User Dialog
    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        EditText etUsername = dialogView.findViewById(R.id.editTextUsername);
        EditText etPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText etNom = dialogView.findViewById(R.id.editTextNom);
        EditText etPrenom = dialogView.findViewById(R.id.editTextPrenom);
        Spinner spRole = dialogView.findViewById(R.id.spinnerRole);

        // ✅ Populate the Spinner with roles
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Admin", "Commercial"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String nom = etNom.getText().toString().trim();
            String prenom = etPrenom.getText().toString().trim();
            String role = spRole.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPwd(password);
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setRole(role);

            RetrofitClient.getInstance().getApi().addUser(newUser).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                        loadUsers(); // ✅ Refresh list after adding
                    } else {
                        Toast.makeText(requireContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
