package com.example.foobar_dt_ad;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foobar_dt_ad.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

//This is the activity of the adding of new post. it's also used fro edit post
public class AddPostScreen extends AppCompatActivity {
    public static final int EDIT = 1;
    public static final int ADD = 2;
    private static final int GALLERY = 14;
    private static final int CAMERA = 15;

    private boolean uploadedImage;
    private ImageView imageGallery;
    private EditText titleEditor;
    private EditText contentEditor;
    private TextView invalid;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //get the elements of the screen
        uploadedImage = false;
        imageGallery = findViewById(R.id.addImage);
        titleEditor = findViewById(R.id.addTitle);
        contentEditor = findViewById(R.id.addContent);
        invalid = findViewById(R.id.invalidAddPost);
        imageGallery.setImageResource(android.R.color.transparent);

        //change the screen to 'Edit screen' if needed
        if (getIntent().getStringExtra("type").equals(String.valueOf(EDIT))) {
            handleGraphicEdit();
        }

        // get image from gallery or camera
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                //the case we take the image from gallery
                                if (flag == GALLERY)
                                    imageGallery.setImageURI(data.getData());
                                //the case we take the image from camera
                                else if (flag == CAMERA) {
                                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                                    imageGallery.setImageBitmap(photo);
                                }
                                //a flag that we have image for the post
                                uploadedImage = true;
                            }
                        }
                    }
                });

        //take image from gallery
        ImageButton btnGallery = findViewById(R.id.btnAddImage);
        btnGallery.setOnClickListener(v -> {
            Intent iGallery = new Intent(Intent.ACTION_PICK);
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            flag = GALLERY;
            someActivityResultLauncher.launch(iGallery);
        });

        //take image from camera
        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(v ->{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            flag = CAMERA;
            someActivityResultLauncher.launch(cameraIntent);
        });

        //handle the share button clicked
        Button btnAdd = findViewById(R.id.btnAddPost);
        btnAdd.setOnClickListener(v -> {
            if (!fillAll())
                return;

            Intent intent = intentToRetrieve();
            if (getIntent().getStringExtra("type").equals(String.valueOf(EDIT)))
                setResult(EDIT, intent);
            else
                setResult(ADD,intent);
            finish();
        });
    }

    /**
     * The function make an intent that contain all the data of the post
     * @return the intent
     */
    private Intent intentToRetrieve() {
        Intent intent = new Intent();
        String title = titleEditor.getText().toString();
        String content = contentEditor.getText().toString();
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageGallery.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("picture", imageInByte);
        intent.putExtra("date", getTodayDate());
        intent.putExtra("id", getIntent().getStringExtra("id"));
        return intent;
    }

    /**
     * The function check if all the fields of the post are filled
     * @return if fill all or not
     */
    private boolean fillAll(){
        String title = titleEditor.getText().toString();
        String content = contentEditor.getText().toString();
        if (!uploadedImage || title.equals("") || content.equals("")) {
            invalid.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
    /**
     * This function change the screen to fit to edit situation
     */
    private void handleGraphicEdit(){
        //get the data of the post we edit and present it on the screen
        Intent i = getIntent();
        titleEditor.setText(i.getStringExtra("title"));
        contentEditor.setText(i.getStringExtra("content"));
        Bundle extras = i.getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        TextView textView = findViewById(R.id.titleAddPost);
        textView.setText("Edit Post");
        imageGallery.setImageBitmap(bmp);
        uploadedImage = true;
    }

    /**
     * This function return a string of today date
     * @return the date
     */
    private String getTodayDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }
}