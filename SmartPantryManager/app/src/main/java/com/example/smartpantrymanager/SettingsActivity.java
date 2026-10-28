package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFERENCES_NAME =
            "smart_pantry_preferences";

    public static final String KEY_EXPIRY_ALERTS =
            "expiry_alerts_enabled";

    private SwitchMaterial switchExpiryAlerts;
    private TextView textExpiryAlertStatus;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        MaterialButton buttonBackFromSettings = findViewById(R.id.buttonBackFromSettings);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        textExpiryAlertStatus = findViewById(R.id.textExpiryAlertStatus);

        preferences = getSharedPreferences(
                PREFERENCES_NAME,
                MODE_PRIVATE
        );

        buttonBackFromSettings.setOnClickListener(
                view -> finish()
        );

        boolean alertsEnabled = preferences.getBoolean(
                KEY_EXPIRY_ALERTS,
                false
        );

        switchExpiryAlerts.setChecked(
                alertsEnabled
        );

        updateAlertStatus(
                alertsEnabled
        );

        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    preferences
                            .edit()
                            .putBoolean(
                                    KEY_EXPIRY_ALERTS,
                                    isChecked
                            )
                            .apply();

                    updateAlertStatus(isChecked);
                }
        );


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateAlertStatus(boolean alertsEnabled) {
        if (alertsEnabled) {
            textExpiryAlertStatus.setText(
                    "Alerts are on"
            );

            textExpiryAlertStatus.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.pantry_green
                    )
            );
        } else {
            textExpiryAlertStatus.setText(
                    "Alerts are off"
            );

            textExpiryAlertStatus.setTextColor(
                    ContextCompat.getColor(
                         this,
                         R.color.pantry_orange
                    )
            );

        }
    }
}