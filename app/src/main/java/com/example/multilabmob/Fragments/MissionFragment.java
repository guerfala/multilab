package com.example.multilabmob.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.Mission;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Models.Organisme;
import com.example.multilabmob.Models.User;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.R;
import com.example.multilabmob.Utils.MissionAdapter;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MissionFragment extends Fragment {

    private Button buttonAddMission, buttonPickDateFilter, buttonFilterMissions;
    private TextView textViewSelectedDateFilter;
    private Spinner spinnerUserFilter;
    private RecyclerView recyclerViewMissions;
    private MissionAdapter missionAdapter; // Assumes you have a MissionAdapter for the RecyclerView
    private List<Mission> missions = new ArrayList<>();
    private TextView textViewKilometrage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        buttonAddMission = view.findViewById(R.id.buttonAddMission);
        buttonPickDateFilter = view.findViewById(R.id.buttonPickDateFilter);
        buttonFilterMissions = view.findViewById(R.id.buttonFilterMissions);
        textViewSelectedDateFilter = view.findViewById(R.id.textViewSelectedDateFilter);
        spinnerUserFilter = view.findViewById(R.id.spinnerUserFilter);
        recyclerViewMissions = view.findViewById(R.id.recyclerViewMissions);

        textViewKilometrage = view.findViewById(R.id.textViewKilometrage);

        // Setup RecyclerView
        recyclerViewMissions.setLayoutManager(new LinearLayoutManager(requireContext()));
        missionAdapter = new MissionAdapter(missions, this::cancelMission);
        recyclerViewMissions.setAdapter(missionAdapter);

        buttonAddMission.setOnClickListener(v -> openAddMissionDialog());

        // Setup user filter spinner
        fetchUsersForSpinner(spinnerUserFilter);

        // Handle date picker for filter
        buttonPickDateFilter.setOnClickListener(v -> openDatePicker(textViewSelectedDateFilter));

        // Handle filter button click
        buttonFilterMissions.setOnClickListener(v -> {
            String selectedDate = textViewSelectedDateFilter.getText().toString();
            User selectedUser = (User) spinnerUserFilter.getSelectedItem();
            if (selectedDate.isEmpty() || selectedUser == null) {
                Toast.makeText(requireContext(), "Please select a date and user to filter.", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchMissionsByDateAndUser(selectedDate, selectedUser.getId());
            fetchKilometrageByDateAndUser(selectedDate, selectedUser.getId());
        });

        return view;
    }

    private void openAddMissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_mission, null);

        Spinner spinnerOrganisme = dialogView.findViewById(R.id.spinnerOrganisme);
        Spinner spinnerUsers = dialogView.findViewById(R.id.spinnerUsers);
        ListView listViewObjects = dialogView.findViewById(R.id.listViewObjects);
        TextView textViewSelectedDate = dialogView.findViewById(R.id.textViewSelectedDate);
        Button buttonSelectDate = dialogView.findViewById(R.id.buttonSelectDate);
        Button buttonAddMission = dialogView.findViewById(R.id.buttonAddMission);

        // Fetch the list of organisms for the dropdown
        fetchOrganismesForSpinner(spinnerOrganisme);

        // Fetch the list of users for the dropdown
        fetchUsersForSpinner(spinnerUsers);

        // Fetch the list of ObjetPredifini for selection
        List<ObjetPredifini> objetsList = new ArrayList<>();
        List<Integer> selectedObjetIds = new ArrayList<>();
        fetchObjetsForListView(listViewObjects, objetsList, selectedObjetIds);

        // Handle date selection
        buttonSelectDate.setOnClickListener(v -> openDatePicker(textViewSelectedDate));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Handle the "Add Mission" button click
        buttonAddMission.setOnClickListener(v -> {
            Log.d("MISSION_DIALOG", "✅ Add Mission button clicked!");

            int selectedOrganismePosition = spinnerOrganisme.getSelectedItemPosition();
            if (selectedOrganismePosition < 0) {
                Toast.makeText(requireContext(), "Please select an Organisme", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedOrganisme = ((Organisme) spinnerOrganisme.getSelectedItem()).getLibelle();

            User selectedUser = (User) spinnerUsers.getSelectedItem();
            String selectedDate = textViewSelectedDate.getText().toString();

            if (selectedUser == null || selectedDate.isEmpty() || selectedObjetIds.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Log Mission Creation
            Log.d("MISSION_DIALOG", "Creating mission with Organisme: " + selectedOrganisme
                    + ", User: " + selectedUser.getUsername()
                    + ", Date: " + selectedDate
                    + ", Objects: " + selectedObjetIds);

            fetchUserFcmToken(selectedUser.getId(), fcmToken -> {
                if (fcmToken == null || fcmToken.isEmpty()) {
                    Log.e("FCM_DEBUG", "⚠️ WARNING: No FCM Token found for user " + selectedUser.getUsername());
                    Toast.makeText(requireContext(), "Error: No FCM Token found for user", Toast.LENGTH_SHORT).show();
                    return;
                }

                Mission mission = new Mission();
                mission.setOrganisme(selectedOrganisme);
                mission.setUser(selectedUser);
                mission.setDate(selectedDate);
                mission.setObjets(selectedObjetIds);
                mission.setFcmToken(fcmToken);  // ✅ Set the correct FCM token

                Log.d("FCM_DEBUG", "🚀 Sending mission with FCM Token: " + fcmToken);
                addMissionToServer(mission, dialog);
            });

        });

        dialog.show();
    }

    private void fetchOrganismesForSpinner(Spinner spinner) {
        RetrofitClient.getInstance().getApi().getOrganismes().enqueue(new Callback<List<Organisme>>() {
            @Override
            public void onResponse(Call<List<Organisme>> call, Response<List<Organisme>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Organisme> organismes = response.body();
                    ArrayAdapter<Organisme> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, organismes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Failed to load Organismes.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Organisme>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading Organismes.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openDatePicker(TextView textViewSelectedDate) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            textViewSelectedDate.setText(selectedDate); // Format the date to "yyyy-MM-dd"
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void fetchUsersForSpinner(Spinner spinner) {
        RetrofitClient.getInstance().getApi().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    ArrayAdapter<User> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, users);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Failed to load users.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchObjetsForListView(ListView listView, List<ObjetPredifini> objetsList, List<Integer> selectedObjetIds) {
        RetrofitClient.getInstance().getApi().getObjets().enqueue(new Callback<List<ObjetPredifini>>() {
            @Override
            public void onResponse(Call<List<ObjetPredifini>> call, Response<List<ObjetPredifini>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetsList.clear();
                    objetsList.addAll(response.body());

                    List<String> objetNames = new ArrayList<>();
                    for (ObjetPredifini objet : objetsList) {
                        objetNames.add(objet.getNom());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, objetNames);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        if (listView.isItemChecked(position)) {
                            selectedObjetIds.add(objetsList.get(position).getId());
                        } else {
                            selectedObjetIds.remove((Integer) objetsList.get(position).getId());
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Failed to load objects.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ObjetPredifini>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading objects.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMissionToServer(Mission mission, AlertDialog dialog) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FCM", "❌ Fetching FCM token failed", task.getException());
                return;
            }

            // ✅ Get the FCM token for the assigned user
            String fcmToken = task.getResult();
            mission.setFcmToken(fcmToken);

            Log.d("MISSION_API", "🔥 Sending mission: " + mission.toString() + " to user: " + mission.getUser().getUsername());

            RetrofitClient.getInstance().getApi().addMission(mission).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("MISSION_API", "✅ Mission assigned successfully: " + response.body());

                        String message = response.body().get("message");
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    } else {
                        Log.e("MISSION_API", "❌ Response error: " + response.errorBody());
                        Toast.makeText(requireContext(), "Failed to assign mission. Try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Log.e("MISSION_API", "❌ API request failed", t);
                }
            });
        });
    }


    private void fetchMissionsByDateAndUser(String date, int userId) {
        RetrofitClient.getInstance().getApi().getMissionsByDateAndUser(date, userId).enqueue(new Callback<List<Mission>>() {
            @Override
            public void onResponse(Call<List<Mission>> call, Response<List<Mission>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    missions.clear();
                    missions.addAll(response.body());
                    missionAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "No missions found for the selected date and user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Mission>> call, Throwable t) {
                // Log the failure message
                System.out.println("Debug: Error: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(requireContext(), "Error fetching missions: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelMission(Mission mission, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Mission")
                .setMessage("Are you sure you want to cancel this mission?")
                .setPositiveButton("Yes", (dialog, which) -> deleteMissionFromServer(mission, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteMissionFromServer(Mission mission, int position) {
        RetrofitClient.getInstance().getApi().deleteMission(mission.getId()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    missions.remove(position);
                    missionAdapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "Mission cancelled successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to cancel mission. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchKilometrageByDateAndUser(String date, int userId) {
        RetrofitClient.getInstance().getApi().getKilometrageByUserAndDate(userId, date)
                .enqueue(new Callback<Float>() {
                    @Override
                    public void onResponse(Call<Float> call, Response<Float> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            float kilometrage = response.body();
                            textViewKilometrage.setText("Kilometrage: " + kilometrage + " km");
                        } else {
                            textViewKilometrage.setText("Kilometrage: No data");
                        }
                    }

                    @Override
                    public void onFailure(Call<Float> call, Throwable t) {
                        textViewKilometrage.setText("Error fetching kilometrage");
                    }
                });
    }

    private void fetchUserFcmToken(int userId, FcmTokenCallback callback) {
        Log.d("FCM_DEBUG", "Fetching FCM token for userId: " + userId);

        RetrofitClient.getInstance().getApi().getUserFcmToken(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String fcmToken = response.body().string().trim();  // ✅ Read response as plain text
                        Log.d("FCM_DEBUG", "✅ Retrieved FCM Token for userId " + userId + ": " + fcmToken);
                        callback.onTokenReceived(fcmToken);
                    } else {
                        Log.e("FCM_DEBUG", "❌ Failed to retrieve FCM Token for userId " + userId);
                        callback.onTokenReceived(null);
                    }
                } catch (Exception e) {
                    Log.e("FCM_DEBUG", "❌ Exception while parsing FCM Token", e);
                    callback.onTokenReceived(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FCM_DEBUG", "❌ API Call Failed: " + t.getMessage());
                callback.onTokenReceived(null);
            }
        });
    }


    interface FcmTokenCallback {
        void onTokenReceived(String token);
    }


}
