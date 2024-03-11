package com.example.foobar_dt_ad;

import static com.example.foobar_dt_ad.DeleteAccount.DEAD;
import static com.example.foobar_dt_ad.DeleteAccount.FROM_DELETE_SCREEN;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foobar_dt_ad.editScreens.EditPhoto;

import java.io.ByteArrayOutputStream;

import adapters.FriendListAdapter;
import adapters.PostListAdapter;
import entities.Friend;
import entities.Member;
import viewmodels.FriendViewModel;
import viewmodels.MemberViewModel;
import viewmodels.PostsViewModel;

public class UserScreen extends AppCompatActivity {

    private PostsViewModel postVM;
    private MemberViewModel memberVM;
    private FriendViewModel friendVM;
    private ListView lstFriends;
    private Button btnFriendship;
    private TextView userName;
    private String requested;
    private String loginUserId;
    public static final int BACK_FROM_USER = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);
        ImageView avatar = findViewById(R.id.avatarPic);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView lstPosts = findViewById(R.id.lstPosts);
        btnFriendship = findViewById(R.id.btnFriendship);
        Intent fromFeed = getIntent();
        requested = fromFeed.getStringExtra("id");
        loginUserId = fromFeed.getStringExtra("loginUserId");
        String jwtToken = fromFeed.getStringExtra("jwt");
        String jwt;
        if (jwtToken.charAt(0) == '"')
            jwt = jwtToken.substring(1,jwtToken.length()-1);
        else
            jwt = jwtToken;
        refreshLayout.setRefreshing(true);

        postVM = new ViewModelProvider(this).get(PostsViewModel.class);
        postVM.initializePostViewModel(this, jwt, memberVM);

        memberVM = new ViewModelProvider(this).get(MemberViewModel.class);
        memberVM.initializeMemberViewModel(this);
        memberVM.updateToken(this, jwt);

        friendVM = new ViewModelProvider(this).get(FriendViewModel.class);
        friendVM.initializeFriendViewModel(jwt, requested);

        Member loginUser = memberVM.getMemberQuick(loginUserId);
        Member currentMember = memberVM.getMemberQuick(requested);

        // Initialize RecyclerView
        final PostListAdapter adapter = new PostListAdapter(this, this, postVM,
                loginUser, memberVM, jwt);
        lstPosts.setAdapter(adapter);
        lstPosts.setLayoutManager(new LinearLayoutManager(this));
        postVM.getPostsByUser(requested,loginUserId).observe(this, posts -> {
            adapter.setPosts(posts);
            if (posts.size() > 0)
                refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        });

        //set the user pic
        if (loginUser != null) {
            avatar.setImageBitmap(currentMember.getImgBitmap());
            userName = findViewById(R.id.usernameField);
            String username = currentMember.getFirstName() + " " + currentMember.getLastName();
            userName.setText(username);
        }


        lstFriends = findViewById(R.id.lstFriends);
        if (loginUserId != null  && loginUserId.equals(requested)) {
            lstFriends.setVisibility(View.VISIBLE);
            btnFriendship.setEnabled(false);
            btnFriendship.setTextColor(ContextCompat.getColor(this, R.color.dayC));
            btnFriendship.setText(R.string.friendship_requests);
            setFriendsList(requested);
        } else {
            lstFriends.setVisibility(View.INVISIBLE);
            btnFriendship.setVisibility(View.VISIBLE);
            friendVM.getTextForBtn(loginUserId).observe(this, text -> btnFriendship.setText(text));
        }

        // refreshing the posts
        refreshLayout.setOnRefreshListener(() -> postVM.reloadByUser(requested,loginUserId));

        btnFriendship.setOnClickListener(v -> {
            if (btnFriendship.getText().toString().equals(Friend.NOT_FRIENDS)) {
                friendVM.askToBeFriend(loginUserId);
            }
        });

        // Create a callback for the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent();
                intent.putExtra("userId",loginUserId);
                setResult(BACK_FROM_USER, intent);
                finish();

            }
        };
        // Add the callback to the back button dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);


        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                String firstName = data.getStringExtra("firstName");
                                String lastName = data.getStringExtra("lastName");
                                String password = data.getStringExtra("password");
                                Bundle extras = data.getExtras();
                                byte[] byteArray = extras.getByteArray("picture");
                                Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                currentMember.setImgBitmap(img);
                                currentMember.setFirstName(firstName);
                                currentMember.setLastName(lastName);
                                currentMember.setPassword(password);
                                memberVM.updateUser(currentMember);
                                userName.setText(firstName + " "+ lastName);
                                avatar.setImageBitmap(img);
                            }
                        } else if (result.getResultCode() == FROM_DELETE_SCREEN) {
                            Intent data = result.getData();
                            if (data != null) {
                                boolean toDelete = data.getBooleanExtra("toDelete",false);
                                if (toDelete) {
                                    memberVM.delete(currentMember);
                                    postVM.deleteUser(currentMember.get_id());
                                    Intent intent = new Intent();
                                    setResult(DEAD,intent);
                                    finish();
                                }
                            }
                        }
                    }
                });

        ImageButton btnMenu = findViewById(R.id.btnMenu);

        // Set OnClickListener for the button
        btnMenu.setOnClickListener(v -> {
            // Create a PopupMenu anchored to the button
            PopupMenu popupMenu = new PopupMenu(this, btnMenu);

            // Inflate the menu resource file
            popupMenu.getMenuInflater().inflate(R.menu.menu_file, popupMenu.getMenu());

            // Set OnMenuItemClickListener for handling menu item clicks
            popupMenu.setOnMenuItemClickListener(item -> {
                // Handle menu item clicks here
                if (item.getItemId() == R.id.friendsItem) {
                    Intent intent = new Intent(UserScreen.this, FriendsScreen.class);
                    intent.putExtra("id",requested);
                    intent.putExtra("loginUserId",loginUserId);
                    intent.putExtra("jwt",jwt);
                    String name = currentMember.getFirstName() + " " + currentMember.getLastName();
                    intent.putExtra("name",name);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.editItem) {
                    // Show the "Edit" menu item
                    Intent intent = createIntentForEdit(currentMember);
                    activityLauncher.launch(intent);
                    return true;
                }
                if (item.getItemId() == R.id.deleteItem) {
                    // Show the "Delete" menu item
                    Intent intent = new Intent(this, DeleteAccount.class);
                    activityLauncher.launch(intent);
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popupMenu.show();

            // Manually hide/show menu items based on conditions
            Menu menu = popupMenu.getMenu();
            MenuItem editMenuItem = menu.findItem(R.id.editItem);
            MenuItem deleteMenuItem = menu.findItem(R.id.deleteItem);

            if (requested.equals(loginUserId)) {
                // Show the menu items
                editMenuItem.setVisible(true);
                deleteMenuItem.setVisible(true);
            } else {
                // Hide the menu items
                editMenuItem.setVisible(false);
                deleteMenuItem.setVisible(false);
            }
        });

    }

    private Intent createIntentForEdit(Member member) {
        Intent intent = new Intent(this, EditPhoto.class);
        intent.putExtra("firstName",member.getFirstName());
        intent.putExtra("lastName",member.getLastName());
        intent.putExtra("password",member.getPassword());
        Bitmap bitmap = member.getImgBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        intent.putExtra("picture", imageInByte);
        return intent;
    }

    private void setFriendsList(String requested) {
        FriendListAdapter adapter = new FriendListAdapter(this, friendVM, requested);
        lstFriends.setAdapter(adapter);
        friendVM.getAsk().observe(this, friends -> {
            if (friends.size() > 0) {
                adapter.clear();
                adapter.addAll(friends);
                adapter.notifyDataSetChanged();
            }
        });
    }
}