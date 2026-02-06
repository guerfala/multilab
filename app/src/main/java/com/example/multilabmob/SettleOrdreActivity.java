package com.example.multilabmob;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.ObjetMissionUpdateDTO;
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
        if (ordreId == -1) {
            Toast.makeText(this, "Error: No ordreId provided!", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if ordreId is missing
        } else {
            Log.d("ORDRE_ID", "Received ordreId: " + ordreId);
        }

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
        // Transform each ObjetMission into a ObjetMissionUpdateDTO
        List<ObjetMissionUpdateDTO> dtoList = new ArrayList<>();
        for (ObjetMission mission : objetMissions) {
            // Set a default value if etat is null
            if (mission.getEtat() == null) {
                mission.setEtat(Etat.NONFINI);
            }
            ObjetMissionUpdateDTO dto = new ObjetMissionUpdateDTO();
            dto.setId(mission.getId());
            dto.setEtat(mission.getEtat());
            dto.setCause(mission.getCause());
            dtoList.add(dto);
        }

        RetrofitClient.getInstance().getApi().settleOrdre(ordreId, dtoList).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettleOrdreActivity.this, "Ordre settled successfully", Toast.LENGTH_SHORT).show();
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
