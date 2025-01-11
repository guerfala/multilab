package com.example.multilabmob;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.Etat;
import com.example.multilabmob.Models.ObjetMission;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.Utils.ObjetMissionAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettleOrdreActivity extends AppCompatActivity {

    private RecyclerView recyclerViewObjetMissions;
    private ObjetMissionAdapter adapter;
    private List<ObjetMission> objetMissions = new ArrayList<>();
    private Button buttonSaveSettledOrdre;

    private int ordreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_ordre);

        recyclerViewObjetMissions = findViewById(R.id.recyclerViewObjetMissions);
        buttonSaveSettledOrdre = findViewById(R.id.buttonSaveSettledOrdre);

        recyclerViewObjetMissions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObjetMissionAdapter(objetMissions);
        recyclerViewObjetMissions.setAdapter(adapter);

        ordreId = getIntent().getIntExtra("ordreId", -1);
        fetchObjetMissions(ordreId);

        buttonSaveSettledOrdre.setOnClickListener(v -> saveSettledOrdre());
    }

    private void fetchObjetMissions(int ordreId) {
        RetrofitClient.getInstance().getApi().getObjetMissions(ordreId).enqueue(new Callback<List<ObjetMission>>() {
            @Override
            public void onResponse(Call<List<ObjetMission>> call, Response<List<ObjetMission>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetMissions.clear();
                    objetMissions.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SettleOrdreActivity.this, "Failed to load missions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ObjetMission>> call, Throwable t) {
                Toast.makeText(SettleOrdreActivity.this, "Error fetching missions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSettledOrdre() {
        for (ObjetMission mission : objetMissions) {
            if (mission.getEtat() == null) {
                mission.setEtat(Etat.NONFINI); // Ensure unchecked missions are set to NONFINI
            }
        }

        RetrofitClient.getInstance().getApi().settleOrdre(ordreId, objetMissions).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettleOrdreActivity.this, "Ordre settled successfully", Toast.LENGTH_SHORT).show();
                    // Notify ShowOrdersActivity to refresh
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(SettleOrdreActivity.this, "Failed to settle ordre", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SettleOrdreActivity.this, "Error settling ordre", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
