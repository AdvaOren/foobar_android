package com.example.foobar_dt_ad;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.foobar_dt_ad.signupScreens.SignUpScreen;

import entities.Member;
import viewmodels.MemberViewModel;


public class MainActivity extends AppCompatActivity {
    //private List<Member> members = new ArrayList<>();
    private MemberViewModel memberVM;
    private Member current;
    private String jwtToken;
    private String loginEmail;


    //private  Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signUp = findViewById(R.id.signUpB);
        Button logIn = findViewById(R.id.logInB);
        EditText email = findViewById(R.id.usernameField);
        EditText password = findViewById(R.id.passwordField);
        TextView invalidEmail = findViewById(R.id.invalidEmail);
        TextView wrongPassword = findViewById(R.id.wrongPassword);
        Activity activity = this;
        jwtToken = "";


        memberVM = new ViewModelProvider(this).get(MemberViewModel.class);
        memberVM.initializeMemberViewModel(this);
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bundle extras = data.getExtras();
                                byte[] byteArray = extras.getByteArray("picture");
                                Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                                Member member = new Member(data.getStringExtra("email"),
                                        data.getStringExtra("firstName"),
                                        data.getStringExtra("lastName"),
                                        data.getStringExtra("password"),
                                        image);
                                memberVM.addMember(member);
                            }
                        }
                    }
                });

        darkMode();
        ProgressBar progressBar = findViewById(R.id.progressBar);

        memberVM.getCurrentMember().observe(this, member -> {
            //the case of wrong email or password
            if (member == null) {
                password.setText("");
                wrongPassword.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            //first click or got answer from view model
            else if (current == null || !current.equals(member)) {
                current = member;
                if (!(current.getEmail().equals("") && current.getPassword().equals(""))) {
                    if (!current.getPassword().equals(password.getText().toString())) {
                        password.setText("");
                        wrongPassword.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    wrongPassword.setVisibility(View.INVISIBLE);

                    //move to feed screen
                    memberVM.getJWT(current);

                }
            }
        });

        memberVM.getJwt().observe(this, jwt -> {
            if (jwt == null) {
                password.setText("");
                wrongPassword.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            if (!jwt.equals("")) {
                jwtToken = jwt;
                loginEmail = current.getEmail();
                moveToFeed(progressBar);
            }
        });

        logIn.setOnClickListener(v -> {
            memberVM.getMemberByEmail(email.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
            if (!jwtToken.equals("") && loginEmail.equals(email.getText().toString())) {
                moveToFeed(progressBar);
            }
        });


        signUp.setOnClickListener(v -> {
                    Intent i = new Intent(this, SignUpScreen.class);
                    someActivityResultLauncher.launch(i);
                }
        );
    }

    private void moveToFeed(ProgressBar progressBar) {
        Intent i = new Intent(this, FeedScreen.class);
        i.putExtra("jwt", jwtToken);
        i.putExtra("id", current.get_id());
        progressBar.setVisibility(View.GONE);
        jwtToken = "";
        current = null;
        startActivity(i);
    }

    private void darkMode() {
        ImageButton btnDark = findViewById(R.id.btnDark);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        TextView forgotPass = findViewById(R.id.forgotPass);

        //set dark / light mode to the activity
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // In dark mode
            btnDark.setImageResource(R.drawable.ic_dark_mode);
            forgotPass.setTextColor(getResources().getColor(R.color.darkC));
        } else {
            // In light mode
            btnDark.setImageResource(R.drawable.ic_light_mode);
            forgotPass.setTextColor(getResources().getColor(R.color.dayC));
        }

        //set dark mode button click listen
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