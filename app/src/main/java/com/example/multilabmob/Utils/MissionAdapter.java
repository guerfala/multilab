package com.example.multilabmob.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.Mission;
import com.example.multilabmob.R;
import android.widget.Button;

import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionViewHolder> {

    private List<Mission> missions;
    private OnMissionCancelListener onMissionCancelListener;

    public MissionAdapter(List<Mission> missions, OnMissionCancelListener onMissionCancelListener) {
        this.missions = missions;
        this.onMissionCancelListener = onMissionCancelListener;
    }

    public MissionAdapter(List<Mission> missions) {
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Mission mission = missions.get(position);

        holder.textViewMissionOrganisme.setText("Organisme: " + mission.getOrganisme());
        holder.textViewMissionDate.setText("Date: " + mission.getDate());
        holder.textViewMissionUser.setText("User: " + mission.getUser().getUsername());

        holder.buttonCancelMission.setOnClickListener(v -> {
            if (onMissionCancelListener != null) {
                onMissionCancelListener.onCancel(mission, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return missions.size();
    }

    static class MissionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMissionOrganisme;
        TextView textViewMissionDate;
        TextView textViewMissionUser;
        Button buttonCancelMission;

        public MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMissionOrganisme = itemView.findViewById(R.id.textViewMissionOrganisme);
            textViewMissionDate = itemView.findViewById(R.id.textViewMissionDate);
            textViewMissionUser = itemView.findViewById(R.id.textViewMissionUser);
            buttonCancelMission = itemView.findViewById(R.id.buttonCancelMission);
        }
    }

    public interface OnMissionCancelListener {
        void onCancel(Mission mission, int position);
    }
}

