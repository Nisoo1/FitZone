package com.anis.fitzone.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anis.fitzone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            showFragment(new DashboardFragment());
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == com.anis.fitzone.R.id.nav_dashboard) {
                showFragment(new DashboardFragment());
                return true;
            } else if (id == com.anis.fitzone.R.id.nav_programs) {
                showFragment(new ProgramsFragment());
                return true;
            } else if (id == com.anis.fitzone.R.id.nav_nutrition) {
                showFragment(new NutritionFragment());
                return true;
            } else if (id == com.anis.fitzone.R.id.nav_profile) {
                showFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(com.anis.fitzone.R.id.fragmentContainer, fragment)
                .commit();
    }

    public void goToTab(int menuItemId) {
        binding.bottomNav.setSelectedItemId(menuItemId);
    }
}
