package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foobar_dt_ad.R;

import java.util.ArrayList;

import entities.Friend;

public class MyFriendsListAdapter extends ArrayAdapter<Friend> {
    private final LayoutInflater inflater;
    private final Context context;

    public MyFriendsListAdapter(@NonNull Context context) {
        super(context, R.layout.friend_list_item, new ArrayList<>());
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //The current comment
        Friend curr = getItem(position);
        if (curr == null)
            return new View(context);
        //make place to a new comment
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.friend_list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.avatarPic);
        TextView userName = convertView.findViewById(R.id.usernameField);

        imageView.setImageBitmap(curr.getImgBitmap());
        userName.setText(curr.getRequesterName());

        return convertView;
    }
}
