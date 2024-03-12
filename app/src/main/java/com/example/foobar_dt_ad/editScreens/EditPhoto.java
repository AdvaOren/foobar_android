package com.example.foobar_dt_ad.editScreens;

import static com.example.foobar_dt_ad.editScreens.EditNames.FROM_EDIT_NAMES;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foobar_dt_ad.R;

import java.io.ByteArrayOutputStream;
/**
 * Activity for editing user photo.
 */
public class EditPhoto extends AppCompatActivity {

    private static final int GALLERY = 14;
    private static final int CAMERA = 15;
    private int flag = 0;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        // Initialize views
        Button createNewUser = findViewById(R.id.EmailScreenContinue);
        ImageButton cameraBtn = findViewById(R.id.cameraBtn);
        ImageButton galleryBtn = findViewById(R.id.galleryBtn);
        ImageView profileImage = findViewById(R.id.profileImage);

        // Register for activity result
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == FROM_EDIT_NAMES) {
                            // Handle result from EditNames activity
                            Intent data = result.getData();
                            if (data != null) {
                                Intent back = new Intent();
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
                            // Handle result from camera or gallery
                            Intent data = result.getData();
                            if (data != null) {
                                if (flag == GALLERY)
                                    profileImage.setImageURI(data.getData());
                                else if (flag == CAMERA) {
                                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                                    profileImage.setImageBitmap(photo);
                                }
                            }
                        }
                    }
                });

        // Retrieve user photo from intent
        Intent data = getIntent();
        Bundle extras = data.getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        profileImage.setImageBitmap(img);

        // Set click listeners for buttons
        createNewUser.setOnClickListener(v -> {
            Intent i = new Intent(this, EditNames.class);
            i.putExtra("firstName", data.getStringExtra("firstName"));
            i.putExtra("lastName", data.getStringExtra("lastName"));
            i.putExtra("password", data.getStringExtra("password"));
            someActivityResultLauncher.launch(i);
        });

        galleryBtn.setOnClickListener(v -> {
            // Launch gallery to select image
            Intent iGallery = new Intent(Intent.ACTION_PICK);
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            flag = GALLERY;
            someActivityResultLauncher.launch(iGallery);
        });

        cameraBtn.setOnClickListener(v -> {
            // Launch camera to take a photo
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            flag = CAMERA;
            someActivityResultLauncher.launch(cameraIntent);
        });
    }
}
