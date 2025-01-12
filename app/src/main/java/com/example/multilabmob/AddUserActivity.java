package com.example.multilabmob;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.multilabmob.Models.User;
import com.example.multilabmob.Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword, editTextNom, editTextPrenom;
    private Spinner spinnerRole;
    private Button buttonAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNom = findViewById(R.id.editTextNom);
        editTextPrenom = findViewById(R.id.editTextPrenom);
        spinnerRole = findViewById(R.id.spinnerRole);
        buttonAddUser = findViewById(R.id.buttonAddUser);

        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String nom = editTextNom.getText().toString();
                String prenom = editTextPrenom.getText().toString();
                String role = spinnerRole.getSelectedItem().toString();

                if (username.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                    Toast.makeText(AddUserActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPwd(password);
                newUser.setNom(nom);
                newUser.setPrenom(prenom);
                newUser.setRole(role);

                addUserToDatabase(newUser);
            }
        });
    }

    private void addUserToDatabase(User user) {
        RetrofitClient.getInstance().getApi().addUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddUserActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 409) {
                    Toast.makeText(AddUserActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddUserActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AddUserActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
