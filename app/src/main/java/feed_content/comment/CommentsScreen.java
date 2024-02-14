package feed_content.comment;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foobar_dt_ad.R;

import java.util.ArrayList;

//This is the screen of the comments
public class CommentsScreen extends AppCompatActivity {

    private ListView listView;
    private CommentModel commentModel;
    private CommentListAdapter adapter;
    private String firstName;
    private String lastName;
    private Bitmap img;

    public final static int BACK_FROM_COMMENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_screen);
        commentModel = new CommentModel();

        //create comment model
        Intent intent = getIntent();
        ArrayList<Comment> comments= intent.getParcelableArrayListExtra("comments");
        commentModel.setComments(comments);

        //get user data from intent
        firstName = intent.getStringExtra("firstName").toString();
        lastName = intent.getStringExtra("lastName").toString();
        Bundle extras = intent.getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //create list view
        listView = findViewById(R.id.list_view);
        adapter = new CommentListAdapter(getApplicationContext(), commentModel,img);
        listView.setAdapter(adapter);

        //add title to the screen
        TextView title = findViewById(R.id.titleComScreen);
        title.setText(firstName + " "+ lastName + "'s comment section");

        //handle the case of adding new comment
        ImageButton btnAdd = findViewById(R.id.btnAddCom);
        EditText newCom = findViewById(R.id.addCom);
        btnAdd.setOnClickListener(v -> {
            String text = newCom.getText().toString();
            if (text.equals(""))
                return;
            commentModel.add(text,firstName,lastName);
            newCom.setText("");
            adapter.notifyDataSetChanged();
        });


        // Create a callback for the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("comments",commentModel.get());
                intent.putExtra("numComments",String.valueOf(commentModel.get().size()));
                intent.putExtra("id",getIntent().getIntExtra("id",-1));
                intent.putExtra("key", "value"); // Add your data here
                setResult(BACK_FROM_COMMENT, intent);
                finish();

            }
        };
        // Add the callback to the back button dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

    }
}