package com.example.foobar_dt_ad.signupScreens;

import android.app.Activity;
import android.content.Intent;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foobar_dt_ad.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for signing up with email, name, password, and profile picture.
 */
public class SignUpScreen extends AppCompatActivity {

    // Constants for startActivityForResult
    private static final int GALLERY = 14;
    private static final int CAMERA = 15;

    // Flag to track if an image has been uploaded
    private boolean uploadedImage = false;

    // List to store registered emails
    private List<String> emails = new ArrayList<>();
    private int flag = 0;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        // Initialize views
        Button createNewUser = findViewById(R.id.EmailScreenContinue);
        EditText emailField = findViewById(R.id.newEmailfield);
        TextView emptyEmailMSG = findViewById(R.id.emptyEmailMSG);
        ImageButton cameraBtn = findViewById(R.id.cameraBtn);
        ImageButton galleryBtn = findViewById(R.id.galleryBtn);
        ImageView profileImage = findViewById(R.id.profileImage);

        // Activity result launcher to handle startActivityForResult
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 3) {
                            Intent data = result.getData();
                            if (data != null) {
                                // Prepare data to pass back to the previous activity
                                Intent back = new Intent();
                                back.putExtra("email", emailField.getText().toString());
                                back.putExtra("firstName", data.getStringExtra("firstName"));
                                back.putExtra("lastName", data.getStringExtra("lastName"));
                                back.putExtra("password", data.getStringExtra("password"));
                                // Convert profile image to byte array
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
                                // Set profile image based on the selected source
                                if (flag == GALLERY)
                                    profileImage.setImageURI(data.getData());
                                else if (flag == CAMERA) {
                                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                                    profileImage.setImageBitmap(photo);
                                }
                                uploadedImage = true; // Mark image as uploaded
                            }
                        }
                    }
                });

        // Create new user button click listener
        createNewUser.setOnClickListener(v -> {
            String enteredEmail = emailField.getText().toString();
            if (!emails.contains(enteredEmail)) {
                if (enteredEmail.equals("")) {
                    // Show error message if email field is empty
                    emptyEmailMSG.setVisibility(View.VISIBLE);
                } else {
                    // Proceed with user creation if email is valid and not already registered
                    emptyEmailMSG.setVisibility(View.INVISIBLE);
                    emails.add(enteredEmail);
                    if (!uploadedImage) {
                        // Show toast message if no profile image is uploaded
                        Toast.makeText(
                                this,
                                "Please add a picture!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // Launch the next activity for entering names
                        Intent i = new Intent(this, SignUpNames.class);
                        someActivityResultLauncher.launch(i);
                    }
                }
            } else {
                // Show toast message if email is already registered
                Toast.makeText(
                        this,
                        "Email already exists!",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Gallery button click listener
        galleryBtn.setOnClickListener(v -> {
            Intent iGallery = new Intent(Intent.ACTION_PICK);
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            flag = GALLERY;
            someActivityResultLauncher.launch(iGallery);
        });

        // Camera button click listener
        cameraBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            flag = CAMERA;
            someActivityResultLauncher.launch(cameraIntent);
        });
    }
}
