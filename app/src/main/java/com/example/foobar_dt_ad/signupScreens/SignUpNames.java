package com.example.foobar_dt_ad.signupScreens;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.foobar_dt_ad.R;
/**
 * Activity for signing up with user names.
 */
public class SignUpNames extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_names);

        // Initialize views
        Button cont = findViewById(R.id.continueNames);
        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);
        TextView emptyNamesMSG = findViewById(R.id.emptyNamesMSG);
        ImageButton btnDark = findViewById(R.id.btnDark);

        // Register for activity result
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                // Return the entered names and password to the previous activity
                                Intent back = new Intent();
                                back.putExtra("firstName",firstNameField.getText().toString());
                                back.putExtra("lastName",lastNameField.getText().toString());
                                back.putExtra("password",data.getStringExtra("password"));
                                setResult(3,back);
                                finish();
                            }
                        }
                    }
                });

        // Continue button click listener
        cont.setOnClickListener(v -> {
            Intent i = new Intent(this, SignUpPasswords.class);
            if (!firstNameField.getText().toString().equals("") && !lastNameField.getText().toString().equals("")) {
                emptyNamesMSG.setVisibility(View.INVISIBLE);
                someActivityResultLauncher.launch(i);
            }
            else {
                emptyNamesMSG.setVisibility(View.VISIBLE);
            }
        });

        // Dark mode button click listener
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // In dark mode
            btnDark.setImageResource(R.drawable.ic_dark_mode);
        } else {
            // In light mode
            btnDark.setImageResource(R.drawable.ic_light_mode);
        }

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
