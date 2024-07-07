package com.example.foobar_dt_ad.editScreens;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.foobar_dt_ad.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Activity for editing user password.
 */
public class EditPassword extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        // Initialize views
        Button createNewUser = findViewById(R.id.createNewUserB);
        EditText password = findViewById(R.id.newPWfield1);
        EditText verPW = findViewById(R.id.newPWfield2);
        TextView invalidPW = findViewById(R.id.passwordInvalidMSG);
        TextView dontMatch = findViewById(R.id.passwordDosentMatchMSG);

        // Retrieve password from intent
        Intent data = getIntent();
        String passwordText = data.getStringExtra("password");
        password.setText(passwordText);
        verPW.setText(passwordText);

        // Handle button click
        createNewUser.setOnClickListener(v -> {
            if (isValidPassword(password.getText().toString())) {
                if (verPW.getText().toString().equals(password.getText().toString())) {
                    // Passwords match, send result back
                    invalidPW.setVisibility(View.INVISIBLE);
                    dontMatch.setVisibility(View.INVISIBLE);
                    Intent back = new Intent();
                    back.putExtra("password", password.getText().toString());
                    setResult(Activity.RESULT_OK, back);
                    finish();
                } else {
                    // Passwords don't match
                    invalidPW.setVisibility(View.INVISIBLE);
                    dontMatch.setVisibility(View.VISIBLE);
                }
            } else {
                // Invalid password format
                invalidPW.setVisibility(View.VISIBLE);
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

    /**
     * Check if the password meets the required format.
     * @param password The password to check.
     * @return True if the password is valid, false otherwise.
     */
    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
