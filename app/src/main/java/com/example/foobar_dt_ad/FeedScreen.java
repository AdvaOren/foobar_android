package com.example.foobar_dt_ad;

import static com.example.foobar_dt_ad.AddPostScreen.ADD;
import static com.example.foobar_dt_ad.DeleteAccount.DEAD;
import static com.example.foobar_dt_ad.UserScreen.BACK_FROM_USER;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import adapters.PostListAdapter;
import entities.Member;
import entities.Post;
import viewmodels.MemberViewModel;
import viewmodels.PostsViewModel;


//This activity is the feed activity that presents all the posts
public class FeedScreen extends AppCompatActivity {

    private PostsViewModel postVM;
    private MemberViewModel memberVM;
    private Member currentMember;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_screen);
        ImageView avatar = findViewById(R.id.avatarPicMenu);
        RecyclerView lstPosts = findViewById(R.id.lstPosts);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);

        //get info from the login screen
        Intent fromLoginI = getIntent();
        String id = fromLoginI.getStringExtra("id");
        String jwt = fromLoginI.getStringExtra("jwt");

        refreshLayout.setRefreshing(true);

        //create member view model
        memberVM = new ViewModelProvider(this).get(MemberViewModel.class);
        memberVM.initializeMemberViewModel(this);
        memberVM.updateToken(this, jwt);

        //create post view model
        postVM = new ViewModelProvider(this).get(PostsViewModel.class);
        postVM.initializePostViewModel(this, jwt,memberVM);

        // Initialize RecyclerView
        final PostListAdapter adapter = new PostListAdapter(this, this, postVM,
                currentMember, memberVM, jwt);
        lstPosts.setAdapter(adapter);
        lstPosts.setLayoutManager(new LinearLayoutManager(this));
        postVM.getAll(id).observe(this, posts -> {
            adapter.setPosts(posts);
            if (posts.size() > 0)
                refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        });

        currentMember = memberVM.getMemberQuick(id);
        //set the user pic
        if (currentMember != null) {
            avatar.setImageBitmap(currentMember.getImgBitmap());
            adapter.setMember(currentMember);
        }


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
                                String content = data.getStringExtra("content");
                                Bundle extras = data.getExtras();
                                byte[] byteArray = extras.getByteArray("picture");
                                String date = data.getStringExtra("date");
                                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                postVM.addPost(currentMember.get_id(),new Post(content,bmp,date,currentMember.get_id()));
                                adapter.notifyDataSetChanged();
                            }
                          //get data from user screen
                        } else if (result.getResultCode() == BACK_FROM_USER) {
                            currentMember = memberVM.getMemberQuick(currentMember.get_id());
                            avatar.setImageBitmap(currentMember.getImgBitmap());
                          //the case the user delete himself
                        } else if(result.getResultCode() == DEAD) {
                            finish();
                        }
                    }
                });

        // call to add post screen
        ImageButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPostScreen.class);
            intent.putExtra("type",String.valueOf(ADD));
            activityResultLauncher.launch(intent);
        });

        // refreshing the posts
        refreshLayout.setOnRefreshListener(() -> {
            postVM.reload(currentMember.get_id());
        });

        //logout the app
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            finish();
        });

        //enter user screen
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserScreen.class);
            intent.putExtra("loginUserId",currentMember.get_id());
            intent.putExtra("id",currentMember.get_id());
            intent.putExtra("jwt",jwt);
            activityResultLauncher.launch(intent);
        });

        darkMode();
    }

    /**
     * Enable dark mode for the activity.
     */
    private void darkMode() {
        ImageButton btnDark = findViewById(R.id.btnDark);
        LinearLayout menu = findViewById(R.id.menu);
        //set the dark / light on the activity
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // In light mode
            btnDark.setImageResource(R.drawable.ic_dark_mode);
            menu.setBackgroundColor(getResources().getColor(R.color.menuLight));
        } else {
            // In dark mode
            btnDark.setImageResource(R.drawable.ic_light_mode);
            menu.setBackgroundColor(getResources().getColor(R.color.menuDark));
        }

        //handle dark mode button click
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