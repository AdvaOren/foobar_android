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

public class EditNames extends AppCompatActivity {
    public static final int FROM_EDIT_NAMES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_names);
        Button cont = findViewById(R.id.continueNames);
        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);
        //TextView emptyNamesMSG = findViewById(R.id.emptyNamesMSG);

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Intent back = new Intent();
                                back.putExtra("firstName",firstNameField.getText().toString());
                                back.putExtra("lastName",lastNameField.getText().toString());
                                back.putExtra("password",data.getStringExtra("password"));
                                setResult(FROM_EDIT_NAMES,back);
                                finish();
                            }
                        }
                    }
                });

        Intent data = getIntent();
        String firstName = data.getStringExtra("firstName");
        String lastName = data.getStringExtra("lastName");
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);

        cont.setOnClickListener(v -> {
            Intent i = new Intent(this, EditPassword.class);
            if (!firstNameField.getText().toString().equals("") && !lastNameField.getText().toString().equals("")) {
                //emptyNamesMSG.setVisibility(View.INVISIBLE);
                i.putExtra("password",data.getStringExtra("password"));
                someActivityResultLauncher.launch(i);
            }
            else {
                Toast.makeText(this, "All fields requires!", Toast.LENGTH_LONG).show();

                //emptyNamesMSG.setVisibility(View.VISIBLE);
            }
        });

        darkMode();

    }

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