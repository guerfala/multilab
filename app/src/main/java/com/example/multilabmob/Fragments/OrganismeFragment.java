package com.example.multilabmob.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.Organisme;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;
import com.example.multilabmob.Utils.OrganismeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganismeFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrganismeAdapter organismeAdapter;
    private List<Organisme> organismeList = new ArrayList<>();
    private SearchView searchView;
    private FloatingActionButton fabAddOrganisme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organisme, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOrganismes);
        searchView = view.findViewById(R.id.searchViewOrganismes);
        fabAddOrganisme = view.findViewById(R.id.fabAddOrganisme);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Create adapter with callback to refresh after deletion
        organismeAdapter = new OrganismeAdapter(organismeList, () -> loadOrganismes());
        recyclerView.setAdapter(organismeAdapter);

        // Load data initially
        loadOrganismes();

        // Add new organisme
        fabAddOrganisme.setOnClickListener(v -> showAddOrganismeDialog());

        // Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                organismeAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                organismeAdapter.filter(newText);
                return false;
            }
        });

        return view;
    }

    private void loadOrganismes() {
        RetrofitClient.getInstance().getApi().getOrganismes()
                .enqueue(new Callback<List<Organisme>>() {
                    @Override
                    public void onResponse(Call<List<Organisme>> call, Response<List<Organisme>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            organismeAdapter.updateList(response.body());
                        } else {
                            Toast.makeText(requireContext(), "Failed to load organismes", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Organisme>> call, Throwable t) {
                        Log.e("API_FAILURE", "Error: " + t.getMessage(), t);
                        Toast.makeText(requireContext(), "Error loading organismes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddOrganismeDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_organisme, null);
        builder.setView(dialogView);

        // The dialog layout has just one EditText: editTextLibelle
        final EditText etLibelle = dialogView.findViewById(R.id.editTextLibelle);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String libelle = etLibelle.getText().toString().trim();
            if (libelle.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in the libelle", Toast.LENGTH_SHORT).show();
                return;
            }

            Organisme newOrganisme = new Organisme();
            newOrganisme.setLibelle(libelle);

            // Call API to add
            RetrofitClient.getInstance().getApi().addOrganisme(newOrganisme)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Organisme added successfully", Toast.LENGTH_SHORT).show();
                                loadOrganismes(); // Refresh list
                            } else {
                                Toast.makeText(requireContext(), "Failed to add organisme", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
