package com.example.multilabmob;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.multilabmob.Fragments.MissionFragment;
import com.example.multilabmob.Fragments.ObjetPredifiniFragment;
import com.example.multilabmob.Fragments.OrdersFragment;
import com.example.multilabmob.Fragments.UsersFragment;
import com.example.multilabmob.Fragments.OrganismeFragment;
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
            } else if (item.getItemId() == R.id.menu_mission) {
                selectedFragment = new MissionFragment();
            } else if (item.getItemId() == R.id.menu_organisme) {
                selectedFragment = new OrganismeFragment();
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
