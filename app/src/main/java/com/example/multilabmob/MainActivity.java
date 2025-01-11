package com.example.multilabmob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the userId from the Intent that started MainActivity
        int userId = getIntent().getIntExtra("userId", -1);

        Button addOrderButton = findViewById(R.id.buttonAddOrder);
        Button viewOrdersButton = findViewById(R.id.buttonViewOrders);

        addOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddOrdreActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        viewOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowOrdersActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }
}
