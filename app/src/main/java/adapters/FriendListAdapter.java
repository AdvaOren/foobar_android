package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foobar_dt_ad.R;

import java.util.ArrayList;

import entities.Friend;
import viewmodels.FriendViewModel;

/**
 * Adapter for the list of friends.
 */
public class FriendListAdapter extends ArrayAdapter<Friend> {
    private final LayoutInflater inflater;
    private final Context context;
    private final FriendViewModel friendVM;
    private final String requested;

    /**
     * Constructor for the FriendListAdapter.
     *
     * @param context   The context.
     * @param friendVM  The FriendViewModel.
     * @param requested The requested user ID.
     */
    public FriendListAdapter(@NonNull Context context, FriendViewModel friendVM, String requested) {
        super(context, R.layout.friend_ask, new ArrayList<>());
        this.friendVM = friendVM;
        this.context = context;
        this.requested = requested;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.friend_ask, parent, false);
        }

        // Set up views
        TextView friendName = convertView.findViewById(R.id.friendName);
        ImageButton btnAccept = convertView.findViewById(R.id.btnAccept);
        ImageButton btnReject = convertView.findViewById(R.id.btnReject);
        friendName.setText(curr.getRequesterName());

        // Set onClickListeners for accept and reject buttons
        btnAccept.setOnClickListener(v -> {
            friendVM.acceptFriend(curr);
            remove(curr);
        });

        btnReject.setOnClickListener(v -> {
            friendVM.rejectFriend(curr);
            remove(curr);
        });

        return convertView;
    }
}

