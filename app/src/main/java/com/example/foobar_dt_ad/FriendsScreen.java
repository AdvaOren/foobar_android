package com.example.foobar_dt_ad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import adapters.MyFriendsListAdapter;
import viewmodels.FriendViewModel;
/**
 * Activity for displaying a user's friends list.
 */
public class FriendsScreen extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_screen);

        // Extracting data from the intent
        Intent fromFeed = getIntent();
        String userId = fromFeed.getStringExtra("id");
        String loginUserId = fromFeed.getStringExtra("loginUserId");
        String jwtToken = fromFeed.getStringExtra("jwt");
        String name = fromFeed.getStringExtra("name");

        // Initializing the FriendViewModel
        FriendViewModel friendVM = new ViewModelProvider(this).get(FriendViewModel.class);
        friendVM.initializeFriendViewModel(jwtToken, userId);

        // Setting the title
        TextView title = findViewById(R.id.title);
        title.setText(name + "'s friends");

        // Setting up the ListView and adapter for displaying friends
        ListView lstFriends = findViewById(R.id.list_view);
        MyFriendsListAdapter adapter = new MyFriendsListAdapter(this);
        lstFriends.setAdapter(adapter);
        // Observing changes in the friends list and updating the adapter
        friendVM.getMyFriends(loginUserId).observe(this, adapter::addAll);

        // Setting up the logout button
        ImageButton btnLogOut = findViewById(R.id.btnLogoutFriends);
        btnLogOut.setOnClickListener(v -> finish());
    }
}
