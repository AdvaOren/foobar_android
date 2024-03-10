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

public class FriendListAdapter extends ArrayAdapter<Friend> {
    private final LayoutInflater inflater;
    private final Context context;
    private final FriendViewModel friendVM;
    private final String requested;

    public FriendListAdapter(@NonNull Context context, FriendViewModel friendVM, String requested) {
        super(context, R.layout.friend_ask,new ArrayList<>());
        this.friendVM = friendVM;
        this.context = context;
        this.requested = requested;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.friend_ask, parent, false);
        }

        TextView friendName = convertView.findViewById(R.id.friendName);
        ImageButton btnAccept = convertView.findViewById(R.id.btnAccept);
        ImageButton btnReject = convertView.findViewById(R.id.btnReject);
        friendName.setText(curr.getRequesterName());

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
