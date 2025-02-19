package com.example.multilabmob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.multilabmob.Models.OrdreAdd;
import com.example.multilabmob.Models.Organisme;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrdreActivity extends AppCompatActivity {

    private Spinner spinnerOrganisme;
    private ListView listViewObjects;
    private Button buttonSaveOrder;

    private List<Organisme> organismesList = new ArrayList<>();
    private List<ObjetPredifini> objetsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<Integer> selectedObjetIds = new ArrayList<>();

    private String organismeName;
    private String missionDate;
    private ArrayList<Integer> missionObjetIds;
    private int userId;
    private int missionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        spinnerOrganisme = findViewById(R.id.spinnerOrganisme);
        listViewObjects = findViewById(R.id.listViewObjects);
        buttonSaveOrder = findViewById(R.id.buttonSaveOrder);

        // ✅ Retrieve data from Intent
        organismeName = getIntent().getStringExtra("organisme");
        missionDate = getIntent().getStringExtra("date");
        missionObjetIds = getIntent().getIntegerArrayListExtra("objets");
        userId = getIntent().getIntExtra("userId", -1);
        missionId = getIntent().getIntExtra("missionId", -1);

        fetchOrganismes();
        fetchObjets();

        buttonSaveOrder.setOnClickListener(v -> {
            if (selectedObjetIds.isEmpty()) {
                Toast.makeText(this, "Veuillez sélectionner des objets", Toast.LENGTH_SHORT).show();
                return;
            }

            saveOrder();
        });
    }

    private void fetchOrganismes() {
        RetrofitClient.getInstance().getApi().getOrganismes().enqueue(new Callback<List<Organisme>>() {
            @Override
            public void onResponse(Call<List<Organisme>> call, Response<List<Organisme>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    organismesList = response.body();

                    ArrayAdapter<Organisme> spinnerAdapter = new ArrayAdapter<>(
                            AddOrdreActivity.this,
                            android.R.layout.simple_spinner_item,
                            organismesList
                    );
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerOrganisme.setAdapter(spinnerAdapter);

                    autoSelectOrganisme();
                } else {
                    Toast.makeText(AddOrdreActivity.this, "Erreur de chargement des organismes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Organisme>> call, Throwable t) {
                Toast.makeText(AddOrdreActivity.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchObjets() {
        RetrofitClient.getInstance().getApi().getObjets().enqueue(new Callback<List<ObjetPredifini>>() {
            @Override
            public void onResponse(Call<List<ObjetPredifini>> call, Response<List<ObjetPredifini>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetsList = response.body();

                    List<String> objetNames = new ArrayList<>();
                    for (ObjetPredifini objet : objetsList) {
                        objetNames.add(objet.getNom());
                    }

                    adapter = new ArrayAdapter<>(AddOrdreActivity.this,
                            android.R.layout.simple_list_item_multiple_choice, objetNames);
                    listViewObjects.setAdapter(adapter);

                    listViewObjects.setOnItemClickListener((parent, view, position, id) -> {
                        int objetId = objetsList.get(position).getId();
                        if (listViewObjects.isItemChecked(position)) {
                            if (!selectedObjetIds.contains(objetId)) {
                                selectedObjetIds.add(objetId);
                            }
                        } else {
                            selectedObjetIds.remove((Integer) objetId);
                        }
                    });

                    autoSelectObjects();
                } else {
                    Toast.makeText(AddOrdreActivity.this, "Erreur de chargement des objets", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ObjetPredifini>> call, Throwable t) {
                Toast.makeText(AddOrdreActivity.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoSelectOrganisme() {
        for (int i = 0; i < organismesList.size(); i++) {
            if (organismesList.get(i).getLibelle().equalsIgnoreCase(organismeName)) {
                spinnerOrganisme.setSelection(i);
                break;
            }
        }
    }

    private void autoSelectObjects() {
        for (int i = 0; i < objetsList.size(); i++) {
            if (missionObjetIds != null && missionObjetIds.contains(objetsList.get(i).getId())) {
                listViewObjects.setItemChecked(i, true);
                if (!selectedObjetIds.contains(objetsList.get(i).getId())) {
                    selectedObjetIds.add(objetsList.get(i).getId());
                }
            }
        }
    }

    private void saveOrder() {
        if (missionId == -1 || userId == -1) {
            Toast.makeText(this, "Mission ou utilisateur non trouvé", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Create an OrdreAdd object with the selected organism and objects
        OrdreAdd ordreAdd = new OrdreAdd();
        ordreAdd.setOrganisme(organismeName);
        ordreAdd.setMissionId(missionId);
        ordreAdd.setUserId(userId);
        ordreAdd.setObjetMissionIds(selectedObjetIds);

        // ✅ Make the API call to create the ordre with the linked mission and user
        RetrofitClient.getInstance().getApi().createOrdre(ordreAdd).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddOrdreActivity.this, "Ordre created successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddOrdreActivity.this, ShowMissionsActivity.class);
                    intent.putExtra("userId", userId); // Pass missionId if ShowMissionActivity uses it
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close AddOrdreActivity after redirection
                } else {
                    Toast.makeText(AddOrdreActivity.this, "Failed to create ordre", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                Toast.makeText(AddOrdreActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
