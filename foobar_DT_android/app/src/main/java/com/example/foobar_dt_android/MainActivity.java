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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Member> members = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        members.add(new Member("a", ",", "", "a"));
        Button signUp = findViewById(R.id.signUpB);
        Button logIn = findViewById(R.id.logInB);
        EditText username = findViewById(R.id.usernameField);
        EditText password = findViewById(R.id.passwordField);
        TextView invalidEmail = findViewById(R.id.invalidEmail);
        TextView wrongPassword = findViewById(R.id.wrongPassword);
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                members.add(new Member(data.getStringExtra("email"),
                                        data.getStringExtra("firstName"),
                                        data.getStringExtra("lastName"),
                                        data.getStringExtra("password")));
                            }
                        }
                    }
                });
        logIn.setOnClickListener(v -> {
            Intent i = new Intent();
            Member member = null;
            for (Member m : members) {
                if (m.equals(new Member(username.getText().toString(), ",", "", ""))) {
                    member = m;
                }
            }
            if (member != null) {
                if (!member.getPassword().equals(password.getText().toString())) {
                    password.setText("");
                    wrongPassword.setVisibility(View.VISIBLE);
                } else {
                    wrongPassword.setVisibility(View.INVISIBLE);
                    invalidEmail.setVisibility(View.INVISIBLE);
                }
            } else {
                username.setText("");
                invalidEmail.setVisibility(View.VISIBLE);
            }
        });


        signUp.setOnClickListener(v -> {
                    Intent i = new Intent(this, SignUpScreen.class);
                    someActivityResultLauncher.launch(i);
                }
        );

    }
}