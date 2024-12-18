package com.example.taskreminderapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // تحميل الإعدادات من ملف XML المخصص
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        // تهيئة التفضيلات المختلفة
        initializeDarkModePreference();
        initializeLogoutPreference();
    }


    private void initializeDarkModePreference() {
        Log.d("SettingsFragment", "Initializing dark mode preference...");
        SwitchPreferenceCompat darkModePref = findPreference("dark_mode");
        if (darkModePref != null) {
            darkModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDarkModeEnabled = (boolean) newValue;
                Log.d("SettingsFragment", "Dark mode changed: " + isDarkModeEnabled);
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
        Log.d("SettingsFragment", "Initializing logout preference...");
        Preference logoutPref = findPreference("logout");
        if (logoutPref != null) {
            logoutPref.setOnPreferenceClickListener(preference -> {
                Log.d("SettingsFragment", "Logging out...");
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
