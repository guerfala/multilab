package com.example.multilabmob;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.Mission;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.Utils.MissionAdapterUser;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Button;
import android.widget.EditText;

public class ShowMissionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MissionAdapterUser missionAdapterUser;
    private ProgressBar progressBar;
    private TextView noMissionsTextView;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_missions);

        recyclerView = findViewById(R.id.recyclerViewMissions);
        progressBar = findViewById(R.id.progressBar);
        noMissionsTextView = findViewById(R.id.noMissionsTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userId = getIntent().getIntExtra("userId", -1);

        if (userId != -1) {
            fetchMissions(userId);
        } else {
            Toast.makeText(this, "User ID is missing!", Toast.LENGTH_SHORT).show();
        }

        EditText kilometerEditText = findViewById(R.id.kilometerEditText);
        Button submitKilometersButton = findViewById(R.id.submitKilometersButton);

        submitKilometersButton.setOnClickListener(v -> {
            String kilometersInput = kilometerEditText.getText().toString().trim();

            if (kilometersInput.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer le kilométrage.", Toast.LENGTH_SHORT).show();
            } else {
                float kilometers = Float.parseFloat(kilometersInput);  // ✅ Correct way to parse float values
                submitKilometers(userId, kilometers);
            }
        });
    }

    private void fetchMissions(int userId) {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApi().getMissionsByUser(userId).enqueue(new Callback<List<Mission>>() {
            @Override
            public void onResponse(Call<List<Mission>> call, Response<List<Mission>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Mission> missions = response.body();
                    if (!missions.isEmpty()) {
                        missionAdapterUser = new MissionAdapterUser(missions, ShowMissionsActivity.this, userId);
                        recyclerView.setAdapter(missionAdapterUser);
                        noMissionsTextView.setVisibility(View.GONE);
                    } else {
                        noMissionsTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ShowMissionsActivity.this, "Failed to load missions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Mission>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ShowMissionsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    private void submitKilometers(int userId, float kilometers) {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApi().submitStartKilometers(userId, kilometers).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ShowMissionsActivity.this, "Kilométrage enregistré avec succès!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShowMissionsActivity.this, "Erreur lors de l'enregistrement.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ShowMissionsActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
