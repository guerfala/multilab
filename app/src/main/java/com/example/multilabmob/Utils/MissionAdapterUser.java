package com.example.multilabmob.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.AddOrdreActivity;
import com.example.multilabmob.Models.Mission;
import com.example.multilabmob.R;
import java.util.ArrayList;
import java.util.List;

public class MissionAdapterUser extends RecyclerView.Adapter<MissionAdapterUser.MissionViewHolder> {

    private List<Mission> missionList;
    private Context context;
    private int userId;

    public MissionAdapterUser(List<Mission> missionList, Context context, int userId) {
        this.missionList = missionList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission_user, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Mission mission = missionList.get(position);
        holder.missionNameUser.setText("Mission: " + (mission.getOrganisme() != null ? mission.getOrganisme() : "N/A"));
        holder.missionDateUser.setText("Date: " + (mission.getDate() != null ? mission.getDate() : "N/A"));

        // ✅ Extract object IDs correctly from the mission model
        ArrayList<Integer> objetIds = new ArrayList<>(mission.getObjets()); // Only pass IDs

        holder.startMissionButtonUser.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddOrdreActivity.class);
            intent.putExtra("missionId", mission.getId());
            intent.putExtra("userId", mission.getUser().getId());
            intent.putExtra("organisme", mission.getOrganisme());
            intent.putExtra("date", mission.getDate());
            intent.putIntegerArrayListExtra("objets", objetIds); // ✅ Correctly pass object IDs

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    public static class MissionViewHolder extends RecyclerView.ViewHolder {
        TextView missionNameUser, missionDateUser;
        Button startMissionButtonUser;

        public MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            missionNameUser = itemView.findViewById(R.id.missionNameUser);
            missionDateUser = itemView.findViewById(R.id.missionDateUser);
            startMissionButtonUser = itemView.findViewById(R.id.startMissionButtonUser);
        }
    }
}
