package com.example.multilabmob;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.Models.Ordre;
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

public class ShowOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrdreAdapter adapter;
    private List<Ordre> orders = new ArrayList<>();
    private TextView selectedDateText;
    private Button selectDateButton;
    public static final int REQUEST_CODE_SETTLE_ORDER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrdreAdapter(this, orders);
        recyclerViewOrders.setAdapter(adapter);

        selectedDateText = findViewById(R.id.selectedDateText);
        selectDateButton = findViewById(R.id.selectDateButton);

        int userId = getIntent().getIntExtra("userId", -1);

        // Show orders for today by default
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        selectedDateText.setText(today);
        fetchOrdersByDay(today, userId);

        // Show DatePickerDialog on button click
        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                selectedDateText.setText(selectedDate);
                fetchOrdersByDay(selectedDate, userId);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void fetchOrdersByDay(String date, int userId) {
        RetrofitClient.getInstance().getApi().getOrdersByDay(date, userId).enqueue(new Callback<List<Ordre>>() {
            @Override
            public void onResponse(Call<List<Ordre>> call, Response<List<Ordre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ShowOrdersActivity.this, "No orders found for the selected day.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ordre>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                Toast.makeText(ShowOrdersActivity.this, "Error fetching orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTLE_ORDER && resultCode == RESULT_OK) {
            // Refresh the orders after settling
            String selectedDate = selectedDateText.getText().toString();
            int userId = getIntent().getIntExtra("userId", -1);
            fetchOrdersByDay(selectedDate, userId);
        }
    }

}
