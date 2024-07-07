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

/**
 * Adapter for the list of friends.
 */
public class MyFriendsListAdapter extends ArrayAdapter<Friend> {
    private final LayoutInflater inflater;
    private final Context context;

    /**
     * Constructor for the MyFriendsListAdapter.
     *
     * @param context The context.
     */
    public MyFriendsListAdapter(@NonNull Context context) {
        super(context, R.layout.friend_list_item, new ArrayList<>());
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    /**
     * Returns the view for a specific position in the list.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The view corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the current friend
        Friend curr = getItem(position);
        if (curr == null)
            return new View(context);
        // Inflate a new view if convertView is null
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.friend_list_item, parent, false);
        }

        // Set up views
        ImageView imageView = convertView.findViewById(R.id.avatarPic);
        TextView userName = convertView.findViewById(R.id.usernameField);

        // Set the image bitmap and username
        imageView.setImageBitmap(curr.getImgBitmap());
        userName.setText(curr.getRequesterName());

        return convertView;
    }
}

