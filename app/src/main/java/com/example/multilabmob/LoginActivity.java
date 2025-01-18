package com.example.multilabmob;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.multilabmob.Models.User;
import com.example.multilabmob.Network.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "NOTIFICATIONS";
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ✅ Request Notification Permission on Login Screen
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "✅ Notification Permission Granted!");
                    } else {
                        Log.e(TAG, "❌ Notification Permission Denied!");
                    }
                });

        checkAndRequestNotificationPermission();

        EditText username = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

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

                        if ("Login successful".equals(message)) {
                            int userId = responseBody.has("id") ? responseBody.get("id").getAsInt() : -1;
                            String role = responseBody.has("role") ? responseBody.get("role").getAsString() : "";

                            if (userId != -1) {
                                String nom = responseBody.has("nom") ? responseBody.get("nom").getAsString() : "Utilisateur";
                                String prenom = responseBody.has("prenom") ? responseBody.get("prenom").getAsString() : "";

                                Toast.makeText(LoginActivity.this, "Bienvenue " + nom + " " + prenom, Toast.LENGTH_SHORT).show();

                                // ✅ Save FCM Token for Logged-in User
                                saveUserFcmToken(userId);

                                if ("admin".equalsIgnoreCase(role)) {
                                    Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                                    startActivity(intent);
                                } else {
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
                    Log.e("LOGIN_ERROR", "❌ Login API Error: " + t.getMessage(), t);
                    Toast.makeText(LoginActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ✅ Check and request notification permission
    private void checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "✅ Notification Permission Already Granted!");
        } else {
            Log.d(TAG, "📢 Requesting Notification Permission...");
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    // ✅ Save FCM Token to Server for the Logged-in User
    private void saveUserFcmToken(int userId) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FCM", "❌ Failed to get FCM Token", task.getException());
                return;
            }

            String fcmToken = task.getResult();
            Log.d("FCM", "✅ User " + userId + " FCM Token: " + fcmToken);

            RetrofitClient.getInstance().getApi().updateUserFcmToken(userId, fcmToken).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("FCM", "✅ FCM Token saved successfully for User " + userId);
                    } else {
                        Log.e("FCM", "❌ Failed to save FCM Token for User " + userId);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("FCM", "❌ API request failed while saving FCM Token", t);
                }
            });
        });
    }
}
