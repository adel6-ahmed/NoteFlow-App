package com.example.taskreminderapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // Add SettingsFragment to Activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }

        // Setup BottomNavigationView for navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setSelectedNavItem(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                // Stay in SettingsActivity
                return true;
            }
            return false;
        });
    }

    @Override
    protected void setSelectedNavItem(BottomNavigationView bottomNavigationView) {
        // Highlight the active settings item
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);
            initializeDarkModePreference();
            initializeLogoutPreference();
        }

        private void initializeDarkModePreference() {
            SwitchPreferenceCompat darkModePref = findPreference("dark_mode");
            if (darkModePref != null) {
                darkModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isDarkModeEnabled = (boolean) newValue;
                    AppCompatDelegate.setDefaultNightMode(
                            isDarkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                    );
                    requireActivity().getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("dark_mode", isDarkModeEnabled).apply();
                    Toast.makeText(getContext(), "Dark Mode: " + (isDarkModeEnabled ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
        }

        private void initializeLogoutPreference() {
            Preference logoutPref = findPreference("logout");
            if (logoutPref != null) {
                logoutPref.setOnPreferenceClickListener(preference -> {
                    requireActivity().getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("isLoggedIn", false).apply();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
        }
    }
}
