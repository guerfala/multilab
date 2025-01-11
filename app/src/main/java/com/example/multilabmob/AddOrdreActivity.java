package com.example.multilabmob;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.multilabmob.Models.OrdreAdd;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrdreActivity extends AppCompatActivity {

    private EditText editTextOrganisme;
    private ListView listViewObjects;
    private Button buttonSaveOrder;

    private List<ObjetPredifini> objetsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<Integer> selectedObjetIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        editTextOrganisme = findViewById(R.id.editTextOrganisme);
        listViewObjects = findViewById(R.id.listViewObjects);
        buttonSaveOrder = findViewById(R.id.buttonSaveOrder);

        fetchObjets();

        int userId = getIntent().getIntExtra("userId", -1);

        buttonSaveOrder.setOnClickListener(v -> {
            String organisme = editTextOrganisme.getText().toString();

            if (organisme.isEmpty() || selectedObjetIds.isEmpty()) {
                Toast.makeText(AddOrdreActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            OrdreAdd ordreAdd = new OrdreAdd(organisme, selectedObjetIds);
            saveOrder(ordreAdd, userId);
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

                    adapter = new ArrayAdapter<>(AddOrdreActivity.this, android.R.layout.simple_list_item_multiple_choice, objetNames);
                    listViewObjects.setAdapter(adapter);

                    listViewObjects.setOnItemClickListener((parent, view, position, id) -> {
                        if (listViewObjects.isItemChecked(position)) {
                            selectedObjetIds.add(objetsList.get(position).getId());
                        } else {
                            selectedObjetIds.remove((Integer) objetsList.get(position).getId());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ObjetPredifini>> call, Throwable t) {
                Toast.makeText(AddOrdreActivity.this, "Erreur de récupération des objets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveOrder(OrdreAdd ordreAdd, int userId) {
        RetrofitClient.getInstance().getApi().createOrdre(ordreAdd, userId).enqueue(new Callback<OrdreAdd>() {
            @Override
            public void onResponse(Call<OrdreAdd> call, Response<OrdreAdd> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddOrdreActivity.this, "Ordre ajouté avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddOrdreActivity.this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrdreAdd> call, Throwable t) {
                Toast.makeText(AddOrdreActivity.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}