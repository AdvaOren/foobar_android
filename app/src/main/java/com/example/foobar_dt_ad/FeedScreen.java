package com.example.foobar_dt_ad;

import static feed_content.add_post.AddPost.ADD;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.io.IOException;
import java.io.InputStream;

import feed_content.post.PostListAdapter;
import feed_content.post.PostsViewModel;
import feed_content.add_post.AddPost;


//This activity is the feed activity that presents all the posts
public class FeedScreen extends AppCompatActivity {

    private PostsViewModel viewModel;
    private Bitmap userPic;
    private String lastName;
    private String firstName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_screen);
        viewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        viewModel.initializePostViewModel(this);

        /*Intent fromLoginI = getIntent();
        firstName = fromLoginI.getStringExtra("firstName");
        lastName = fromLoginI.getStringExtra("lastName");
        Bundle extras = fromLoginI.getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        userPic = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/
        firstName = "temp";
        lastName = "temp2";
        try {
            InputStream inputStream = this.getAssets().open("water.jpeg");
            userPic = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //set the user pic
        ImageView avatar = findViewById(R.id.avatarPicMenu);
        avatar.setImageBitmap(userPic);

        // Initialize RecyclerView
        RecyclerView lstPosts = findViewById(R.id.lstPosts);
        final PostListAdapter adapter = new PostListAdapter(this,this,viewModel,userPic,firstName,lastName);
        lstPosts.setAdapter(adapter);
        lstPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter.setPosts(viewModel.get());

        // Register activity result launcher for AddPost activity use for get image
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //get the data of the new post from the add post screen
                        if (result.getResultCode() == ADD) {
                            Intent data = result.getData();
                            if (data != null) {
                                String title = data.getStringExtra("title");
                                String content = data.getStringExtra("content");
                                Bundle extras = data.getExtras();
                                byte[] byteArray = extras.getByteArray("picture");
                                String date = data.getStringExtra("date");
                                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                viewModel.add(title,content,bmp,userPic,firstName,lastName,date);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        // call to add post screen
        ImageButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPost.class);
            intent.putExtra("type",String.valueOf(ADD));
            activityResultLauncher.launch(intent);
        });

        // Disable the refreshing animation
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
        });
    }
}