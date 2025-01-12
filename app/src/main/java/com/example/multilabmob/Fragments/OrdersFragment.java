package com.example.multilabmob.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.multilabmob.R;
import com.example.multilabmob.Utils.AdminOrdreAdapter;
import com.example.multilabmob.Utils.OrdreAdapter;
import com.example.multilabmob.Models.Ordre;
import com.example.multilabmob.Network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private AdminOrdreAdapter adapter;
    private List<Ordre> orders = new ArrayList<>();
    private TextView selectedDateText;
    private Button selectDateButton, fetchOrdersButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        selectedDateText = view.findViewById(R.id.selectedDateText);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        fetchOrdersButton = view.findViewById(R.id.fetchOrdersButton);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdminOrdreAdapter(orders);
        recyclerViewOrders.setAdapter(adapter);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        selectedDateText.setText(today);

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                selectedDateText.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        fetchOrdersButton.setOnClickListener(v -> fetchOrdersByDay(selectedDateText.getText().toString()));

        return view;
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
                    Toast.makeText(requireContext(), "No orders found for the selected day.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ordre>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error fetching orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
