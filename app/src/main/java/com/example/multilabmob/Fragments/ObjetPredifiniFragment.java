package com.example.multilabmob.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;
import com.example.multilabmob.Utils.ObjetPredifiniAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjetPredifiniFragment extends Fragment {
    private RecyclerView recyclerView;
    private ObjetPredifiniAdapter adapter;
    private List<ObjetPredifini> objetsList = new ArrayList<>();
    private SearchView searchView;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objet_predifini, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewObjets);
        searchView = view.findViewById(R.id.searchViewObjets);
        fabAdd = view.findViewById(R.id.fabAddObjet);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ObjetPredifiniAdapter(objetsList, getContext());
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddObjetDialog());

        loadObjets();

        return view;
    }

    private void loadObjets() {
        RetrofitClient.getInstance().getApi().getObjets().enqueue(new Callback<List<ObjetPredifini>>() {
            @Override
            public void onResponse(Call<List<ObjetPredifini>> call, Response<List<ObjetPredifini>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetsList.clear();
                    objetsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<ObjetPredifini>> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading objets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddObjetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Objet");

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_objet, (ViewGroup) getView(), false);
        EditText input = viewInflated.findViewById(R.id.editTextObjetNameDialog);

        builder.setView(viewInflated);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String objetName = input.getText().toString().trim();
            if (!objetName.isEmpty()) {
                addObjetToDatabase(objetName);
            } else {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addObjetToDatabase(String objetName) {
        ObjetPredifini newObjet = new ObjetPredifini();
        newObjet.setNom(objetName);

        RetrofitClient.getInstance().getApi().addObjetPredifini(newObjet).enqueue(new Callback<ObjetPredifini>() {
            @Override
            public void onResponse(Call<ObjetPredifini> call, Response<ObjetPredifini> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetsList.add(response.body());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Objet added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add objet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjetPredifini> call, Throwable t) {
                Toast.makeText(getContext(), "Error adding objet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
