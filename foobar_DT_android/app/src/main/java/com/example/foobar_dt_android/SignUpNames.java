package com.example.foobar_dt_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpNames extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_names);
        Button cont = findViewById(R.id.continueNames);
        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);
        TextView emptyNamesMSG = findViewById(R.id.emptyNamesMSG);

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
                                setResult(3,back);
                                finish();
                            }
                        }
                    }
                });

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
    }
}