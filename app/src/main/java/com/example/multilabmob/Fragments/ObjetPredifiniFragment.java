package com.example.multilabmob.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjetPredifiniFragment extends Fragment {

    private EditText editTextObjetName;
    private Button buttonAddObjet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objet_predifini, container, false);

        editTextObjetName = view.findViewById(R.id.editTextObjetName);
        buttonAddObjet = view.findViewById(R.id.buttonAddObjet);

        buttonAddObjet.setOnClickListener(v -> addObjetPredifini());

        return view;
    }

    private void addObjetPredifini() {
        String objetName = editTextObjetName.getText().toString().trim();

        if (objetName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a name for the objet.", Toast.LENGTH_SHORT).show();
            return;
        }

        ObjetPredifini newObjet = new ObjetPredifini();
        newObjet.setNom(objetName);

        RetrofitClient.getInstance().getApi().addObjetPredifini(newObjet).enqueue(new Callback<ObjetPredifini>() {
            @Override
            public void onResponse(Call<ObjetPredifini> call, Response<ObjetPredifini> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Objet added successfully.", Toast.LENGTH_SHORT).show();
                    editTextObjetName.setText(""); // Clear input field after successful addition
                } else {
                    Toast.makeText(requireContext(), "Failed to add objet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjetPredifini> call, Throwable t) {
                Toast.makeText(requireContext(), "Error adding objet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
