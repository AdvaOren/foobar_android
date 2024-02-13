package com.example.foobar_dt_android;

import static java.security.AccessController.getContext;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SignUpScreen extends AppCompatActivity {
    private List<String> emails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        Button createNewUser = findViewById(R.id.EmailScreenContinue);
        EditText emailField = findViewById(R.id.newEmailfield);
        TextView emptyEmailMSG = findViewById(R.id.emptyEmailMSG);
        emails.add("a");
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Intent back = new Intent();
                                back.putExtra("email", emailField.getText().toString());
                                back.putExtra("firstName", data.getStringExtra("firstName"));
                                back.putExtra("lastName", data.getStringExtra("lastName"));
                                back.putExtra("password", data.getStringExtra("password"));
                                setResult(Activity.RESULT_OK, back);
                                finish();
                            }
                        }
                    }
                });
        createNewUser.setOnClickListener(v -> {
                    Intent i = new Intent(this, SignUpNames.class);
                    if (!emails.contains(emailField.getText().toString())) {
                        if (emailField.getText().toString().equals("")) {
                            emptyEmailMSG.setVisibility(View.VISIBLE);
                        } else {
                            emptyEmailMSG.setVisibility(View.INVISIBLE);
                            emails.add(emailField.getText().toString());
                            someActivityResultLauncher.launch(i);
                        }
                    } else {
                        Toast.makeText(
                                this,
                                "email already exists!",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}