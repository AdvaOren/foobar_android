package com.example.foobar_dt_ad;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.foobar_dt_ad.signupScreens.SignUpScreen;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Member> members = new ArrayList<>();

    private  Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                                Bundle extras = data.getExtras();
                                byte[] byteArray = extras.getByteArray("picture");
                                String date = data.getStringExtra("date");
                                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                            }
                        }
                    }
                });

        TextView forgotPass = findViewById(R.id.forgotPass);
        ImageButton btnDark = findViewById(R.id.btnDark);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // In dark mode
            btnDark.setImageResource(R.drawable.ic_dark_mode);
            forgotPass.setTextColor(getResources().getColor(R.color.darkC));
        } else {
            // In light mode
            btnDark.setImageResource(R.drawable.ic_light_mode);
            forgotPass.setTextColor(getResources().getColor(R.color.dayC));

        }

        logIn.setOnClickListener(v -> {
            Intent i = new Intent(this, FeedScreen.class);
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
                    i.putExtra("firstName", member.getFirstName());
                    i.putExtra("lastName", member.getLastName());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    i.putExtra("picture", imageInByte);
                    startActivity(i);
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