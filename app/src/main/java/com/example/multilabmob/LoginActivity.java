package com.example.multilabmob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.multilabmob.Models.User;
import com.example.multilabmob.Network.RetrofitClient;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                User loginUser = new User(user, pass);

                RetrofitClient.getInstance().getApi().login(loginUser).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject responseBody = response.body();
                            String message = responseBody.has("message") ? responseBody.get("message").getAsString() : "";

                            if (message.equals("Login successful")) {
                                int userId = responseBody.has("id") ? responseBody.get("id").getAsInt() : -1;
                                String role = responseBody.has("role") ? responseBody.get("role").getAsString() : "";

                                if (userId != -1) {
                                    String nom = responseBody.has("nom") ? responseBody.get("nom").getAsString() : "Utilisateur";
                                    String prenom = responseBody.has("prenom") ? responseBody.get("prenom").getAsString() : "";

                                    Toast.makeText(LoginActivity.this, "Bienvenue " + nom + " " + prenom, Toast.LENGTH_SHORT).show();

                                    if ("admin".equalsIgnoreCase(role)) {
                                        // Redirect to Admin Panel
                                        Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Redirect to Main Activity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("userId", userId);
                                        startActivity(intent);
                                    }

                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Erreur: ID utilisateur introuvable", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("LOGIN_ERROR", t.getMessage(), t);
                        Toast.makeText(LoginActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
