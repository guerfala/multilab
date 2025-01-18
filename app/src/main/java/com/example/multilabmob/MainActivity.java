package com.example.multilabmob;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        // ✅ Initialize notification permission request
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d("NOTIFICATIONS", "✅ Notification Permission Granted!");
                    } else {
                        Log.e("NOTIFICATIONS", "❌ Notification Permission Denied!");
                    }
                });

        // ✅ Check if permission is granted, if not request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
