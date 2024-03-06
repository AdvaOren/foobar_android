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

                                /*members.add(new Member(data.getStringExtra("email"),
                                        data.getStringExtra("firstName"),
                                        data.getStringExtra("lastName"),
                                        data.getStringExtra("password")));
                                Bundle extras = data.getExtras();
                                byte[] byteArray = extras.getByteArray("picture");
                                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/
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
            if (!jwt.equals("")) {
                Intent i = new Intent(activity, FeedScreen.class);
                i.putExtra("jwt", jwt);
                i.putExtra("id", current.get_id());
                progressBar.setVisibility(View.GONE);
                startActivity(i);
            }
        });

        logIn.setOnClickListener(v -> {
            /*Member member = null;
            for (Member m : members) {
                if (m.equals(new Member(email.getText().toString(), ",", "", ""))) {
                    member = m;
                }
            }*/
            memberVM.getMemberByEmail(email.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
                /*//check if the member exists
            //if (current != null) {
                //check if the the password is wrong
                if (!current.getPassword().equals(password.getText().toString())) {
                    password.setText("");
                    progressBar.setVisibility(View.GONE);
                    wrongPassword.setVisibility(View.VISIBLE);

                //move to feed screen
                //} else {
                wrongPassword.setVisibility(View.INVISIBLE);
                invalidEmail.setVisibility(View.INVISIBLE);
                Intent i = new Intent(this, FeedScreen.class);
                i.putExtra("jwt", memberVM.getJWT(current.get_id()));
                i.putExtra("id", current.get_id());
                    /*i.putExtra("firstName", member.getFirstName());
                    i.putExtra("lastName", member.getLastName());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    i.putExtra("picture", imageInByte);
                progressBar.setVisibility(View.GONE);
                checked = false;
                startActivity(i);
                //}
                //case that email is not exists
            } else {
                email.setText("");
                invalidEmail.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }*/
        });


        signUp.setOnClickListener(v -> {
                    Intent i = new Intent(this, SignUpScreen.class);
                    someActivityResultLauncher.launch(i);
                }
        );


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