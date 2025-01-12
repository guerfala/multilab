package com.example.multilabmob.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.multilabmob.Models.User;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersFragment extends Fragment {

    private EditText editTextUsername, editTextPassword, editTextNom, editTextPrenom;
    private Spinner spinnerRole;
    private Button buttonAddUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextNom = view.findViewById(R.id.editTextNom);
        editTextPrenom = view.findViewById(R.id.editTextPrenom);
        spinnerRole = view.findViewById(R.id.spinnerRole);
        buttonAddUser = view.findViewById(R.id.buttonAddUser);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        buttonAddUser.setOnClickListener(v -> addUser());

        return view;
    }

    private void addUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nom = editTextNom.getText().toString().trim();
        String prenom = editTextPrenom.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

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
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().has("message") ? response.body().get("message").getAsString() : "User added successfully";
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    clearFields();
                } else if (response.code() == 409) {
                    Toast.makeText(requireContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to add user. Server response: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clearFields() {
        editTextUsername.setText("");
        editTextPassword.setText("");
        editTextNom.setText("");
        editTextPrenom.setText("");
        spinnerRole.setSelection(0);
    }
}
