package com.example.foobar_dt_ad;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import adapters.CommentListAdapter;
import entities.Comment;
import viewmodels.CommentViewModel;

//This is the screen of the comments
public class CommentsScreen extends AppCompatActivity {

    private ListView listView;
    private CommentViewModel commentVM;
    private CommentListAdapter adapter;

    public final static int BACK_FROM_COMMENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_screen);


        //get user data from intent
        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId").toString();
        String jwt = intent.getStringExtra("jwt").toString();
        String jwtToken = jwt.substring(1,jwt.length()-1);
        String userId = intent.getStringExtra("userId").toString();
        String firstName = intent.getStringExtra("firstName").toString();
        String lastName = intent.getStringExtra("lastName").toString();
        byte[] imgString = intent.getByteArrayExtra("picture");
        Bitmap bitmap= BitmapFactory.decodeByteArray(imgString, 0, imgString.length);

        commentVM = new ViewModelProvider(this).get(CommentViewModel.class);
        commentVM.initializeCommentViewModel(postId, jwtToken);
        //create list view
        listView = findViewById(R.id.list_view);
        adapter = new CommentListAdapter(this, commentVM, userId);
        listView.setAdapter(adapter);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        commentVM.get().observe(this, comments -> {
            if (comments.size() > 0) {
                adapter.clear();
                adapter.addAll(comments);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
        commentVM.getAll();

        //handle the case of adding new comment
        ImageButton btnAdd = findViewById(R.id.btnAddCom);
        EditText newCom = findViewById(R.id.addCom);
        btnAdd.setOnClickListener(v -> {
            String text = newCom.getText().toString();
            if (text.equals(""))
                return;
            Comment comment = new Comment("",text,userId,bitmap,firstName,lastName,postId);
            commentVM.addComment(comment);
            newCom.setText("");
            adapter.addOneChange();
            adapter.notifyDataSetChanged();
        });


        // Create a callback for the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent();
                intent.putExtra("numChanges",adapter.getNumOfChanges());
                intent.putExtra("userId",userId);
                intent.putExtra("postId",postId);
                setResult(BACK_FROM_COMMENT, intent);
                finish();

            }
        };
        // Add the callback to the back button dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        ImageButton btnDark = findViewById(R.id.btnDark);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // In dark mode
            btnDark.setImageResource(R.drawable.ic_dark_mode);
        } else {
            // In light mode
            btnDark.setImageResource(R.drawable.ic_light_mode);
        }

        btnDark.setOnClickListener(v -> {
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate(); // Recreate the activity to apply the new theme
        });

    }
}