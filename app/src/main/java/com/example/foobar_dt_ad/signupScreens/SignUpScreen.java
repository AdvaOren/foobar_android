package com.example.foobar_dt_ad.signupScreens;

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
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foobar_dt_ad.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SignUpScreen extends AppCompatActivity {
    private List<String> emails = new ArrayList<>();
    private static final int GALLERY = 14;
    private static final int CAMERA = 15;
    private int flag = 0;
    private boolean uploadedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        Button createNewUser = findViewById(R.id.EmailScreenContinue);
        EditText emailField = findViewById(R.id.newEmailfield);
        TextView emptyEmailMSG = findViewById(R.id.emptyEmailMSG);
        ImageButton cameraBtn = findViewById(R.id.cameraBtn);
        ImageButton galleryBtn = findViewById(R.id.galleryBtn);
        ImageView profileImage = findViewById(R.id.profileImage);
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 3) {
                            Intent data = result.getData();
                            if (data != null) {

                                Intent back = new Intent();
                                back.putExtra("email", emailField.getText().toString());
                                back.putExtra("firstName", data.getStringExtra("firstName"));
                                back.putExtra("lastName", data.getStringExtra("lastName"));
                                back.putExtra("password", data.getStringExtra("password"));
                                BitmapDrawable bitmapDrawable = ((BitmapDrawable) profileImage.getDrawable());
                                Bitmap bitmap = bitmapDrawable.getBitmap();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] imageInByte = stream.toByteArray();
                                back.putExtra("picture", imageInByte);
                                setResult(Activity.RESULT_OK, back);
                                finish();
                            }
                        } else if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                if (flag == GALLERY)
                                    profileImage.setImageURI(data.getData());
                                else if (flag == CAMERA) {
                                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                                    profileImage.setImageBitmap(photo);
                                }

                                uploadedImage = true;
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
                            if (!uploadedImage) {
                                Toast.makeText(
                                        this,
                                        "Please add picture!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                someActivityResultLauncher.launch(i);
                            }
                        }

                    } else {
                        Toast.makeText(
                                this,
                                "Email already exists!",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        galleryBtn.setOnClickListener(v ->
        {
            Intent iGallery = new Intent(Intent.ACTION_PICK);
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            flag = GALLERY;
            someActivityResultLauncher.launch(iGallery);
        });

        cameraBtn.setOnClickListener(v ->
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            flag = CAMERA;
            someActivityResultLauncher.launch(cameraIntent);
        });
    }
}