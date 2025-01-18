package com.example.multilabmob;

import android.app.Application;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // ✅ Initialize Firebase globally for ALL users (admin & normal users)
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FCM", "❌ Failed to get Firebase token", task.getException());
                return;
            }

            String token = task.getResult();
            Log.d("FCM", "✅ Global Firebase Token: " + token);
        });
    }
}
