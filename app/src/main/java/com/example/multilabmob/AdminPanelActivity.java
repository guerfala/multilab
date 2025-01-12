package com.example.multilabmob;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.multilabmob.Fragments.ObjetPredifiniFragment;
import com.example.multilabmob.Fragments.OrdersFragment;
import com.example.multilabmob.Fragments.UsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        BottomNavigationView bottomNavigationView = findViewById(R.id.adminBottomNavigation);

        // Load OrderFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new OrdersFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.menu_order) {
                selectedFragment = new OrdersFragment();
            } else if (item.getItemId() == R.id.menu_user) {
                selectedFragment = new UsersFragment();
            } else if (item.getItemId() == R.id.menu_objet_predifini) {
                selectedFragment = new ObjetPredifiniFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}
