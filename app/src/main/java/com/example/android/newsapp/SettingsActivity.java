package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Inflates the given XML resource and adds the preference hierarchy to the current
            // preference hierarchy.
            addPreferencesFromResource(R.xml.fragment_preference);

            // Display the current value of the favorite sport preference in its 'summary'
            Preference favorite_sport = findPreference(getString(R.string.preference_favorite_sport_key));
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(favorite_sport.getContext());
            String summaryValue = sharedPreferences.getString(favorite_sport.getKey(), "");
            favorite_sport.setSummary(summaryValue);

            // Update the summary when the favorite sport preference changes
            favorite_sport.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary((String) newValue);
                    return true;
                }
            });
        }
    }
}
