package com.example.multilabmob.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.Etat;
import com.example.multilabmob.Models.ObjetMission;
import com.example.multilabmob.R;

import java.util.List;

public class ObjetMissionAdapter extends RecyclerView.Adapter<ObjetMissionAdapter.ViewHolder> {

    private final List<ObjetMission> missions;

    public ObjetMissionAdapter(List<ObjetMission> missions) {
        this.missions = missions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objet_mission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObjetMission mission = missions.get(position);

        holder.textViewNom.setText(mission.getObjetPredifini().getNom());
        holder.editTextCause.setText(mission.getCause());
        holder.checkBoxFinished.setChecked(mission.getEtat() == Etat.FINIS);

        // Update the cause in the model when the user types
        holder.editTextCause.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mission.setCause(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Update the Etat in the model when the checkbox is toggled
        holder.checkBoxFinished.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mission.setEtat(isChecked ? Etat.FINIS : Etat.NONFINI);
        });
    }

    @Override
    public int getItemCount() {
        return missions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNom;
        EditText editTextCause;
        CheckBox checkBoxFinished;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNom = itemView.findViewById(R.id.textViewNom);
            editTextCause = itemView.findViewById(R.id.editTextCause);
            checkBoxFinished = itemView.findViewById(R.id.checkBoxFinished);
        }
    }
}
