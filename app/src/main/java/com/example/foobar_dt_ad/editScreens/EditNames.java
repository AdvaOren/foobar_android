package com.example.foobar_dt_ad.editScreens;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.foobar_dt_ad.R;
/**
 * Activity for editing user names.
 */
public class EditNames extends AppCompatActivity {
    public static final int FROM_EDIT_NAMES = 3;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_names);

        // Initialize views
        Button cont = findViewById(R.id.continueNames);
        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);

        // Register for activity result
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Intent back = new Intent();
                                back.putExtra("firstName", firstNameField.getText().toString());
                                back.putExtra("lastName", lastNameField.getText().toString());
                                back.putExtra("password", data.getStringExtra("password"));
                                setResult(FROM_EDIT_NAMES, back);
                                finish();
                            }
                        }
                    }
                });

        // Retrieve intent data
        Intent data = getIntent();
        String firstName = data.getStringExtra("firstName");
        String lastName = data.getStringExtra("lastName");

        // Set text fields with received data
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);

        // Handle continue button click
        cont.setOnClickListener(v -> {
            Intent i = new Intent(this, EditPassword.class);
            if (!firstNameField.getText().toString().isEmpty() && !lastNameField.getText().toString().isEmpty()) {
                i.putExtra("password", data.getStringExtra("password"));
                someActivityResultLauncher.launch(i);
            } else {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_LONG).show();
            }
        });

        // Enable dark mode
        darkMode();
    }

    /**
     * Enable dark mode for the activity.
     */
    private void darkMode() {
        ImageButton btnDark = findViewById(R.id.btnDark);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // In dark mode
            btnDark.setImageResource(R.drawable.ic_dark_mode);
        } else {
            // In light mode
            btnDark.setImageResource(R.drawable.ic_light_mode);
        }

        // Toggle dark mode when button is clicked
        btnDark.setOnClickListener(v -> {
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                // Switch to dark mode
                btnDark.setImageResource(R.drawable.ic_light_mode);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Switch to light mode
                btnDark.setImageResource(R.drawable.ic_dark_mode);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate(); // Recreate the activity to apply the new theme
        });
    }
}
