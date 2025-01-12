package com.example.multilabmob;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multilabmob.Models.Ordre;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Network.RetrofitClient;
import com.example.multilabmob.Utils.OrdreAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanelActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrdreAdapter adapter;
    private List<Ordre> orders = new ArrayList<>();
    private TextView selectedDateText;
    private Button selectDateButton, addObjetButton, fetchOrdersButton;
    private EditText objetNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        selectedDateText = findViewById(R.id.selectedDateText);
        selectDateButton = findViewById(R.id.selectDateButton);
        addObjetButton = findViewById(R.id.addObjetButton);
        fetchOrdersButton = findViewById(R.id.fetchOrdersButton);
        objetNameInput = findViewById(R.id.editTextObjetName);
        Button addUserButton = findViewById(R.id.buttonAddUser);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrdreAdapter(this, orders);
        recyclerViewOrders.setAdapter(adapter);

        // Default to today's date
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        selectedDateText.setText(today);

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                selectedDateText.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        fetchOrdersButton.setOnClickListener(v -> fetchOrdersByDay(selectedDateText.getText().toString()));

        addObjetButton.setOnClickListener(v -> addObjetPredifini(objetNameInput.getText().toString()));

        addUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, AddUserActivity.class);
            startActivity(intent);
        });
    }

    private void fetchOrdersByDay(String date) {
        RetrofitClient.getInstance().getApi().getOrdersByDayForAdmin(date).enqueue(new Callback<List<Ordre>>() {
            @Override
            public void onResponse(Call<List<Ordre>> call, Response<List<Ordre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminPanelActivity.this, "No orders found for the selected day.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ordre>> call, Throwable t) {
                Toast.makeText(AdminPanelActivity.this, "Error fetching orders.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addObjetPredifini(String objetName) {
        if (objetName.isEmpty()) {
            Toast.makeText(this, "Please enter a name for the objet.", Toast.LENGTH_SHORT).show();
            return;
        }

        ObjetPredifini newObjet = new ObjetPredifini();
        newObjet.setNom(objetName);

        RetrofitClient.getInstance().getApi().addObjetPredifini(newObjet).enqueue(new Callback<ObjetPredifini>() {
            @Override
            public void onResponse(Call<ObjetPredifini> call, Response<ObjetPredifini> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPanelActivity.this, "Objet added successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminPanelActivity.this, "Failed to add objet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjetPredifini> call, Throwable t) {
                Toast.makeText(AdminPanelActivity.this, "Error adding objet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
