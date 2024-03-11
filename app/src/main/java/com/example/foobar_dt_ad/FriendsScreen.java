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

public class FriendsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_screen);

        Intent fromFeed = getIntent();
        String userId = fromFeed.getStringExtra("id");
        String loginUserId = fromFeed.getStringExtra("loginUserId");
        String jwtToken = fromFeed.getStringExtra("jwt");
        String name = fromFeed.getStringExtra("name");

        FriendViewModel friendVM = new ViewModelProvider(this).get(FriendViewModel.class);
        friendVM.initializeFriendViewModel(jwtToken, userId);

        TextView title = findViewById(R.id.title);
        title.setText(name + "'s friends");

        ListView lstFriends = findViewById(R.id.list_view);
        MyFriendsListAdapter adapter = new MyFriendsListAdapter(this);
        lstFriends.setAdapter(adapter);
        friendVM.getMyFriends(loginUserId).observe(this, adapter::addAll);

        ImageButton btnLogOut = findViewById(R.id.btnLogoutFriends);
        btnLogOut.setOnClickListener(v -> finish());
    }
}