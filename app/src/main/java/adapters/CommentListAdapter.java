package adapters;


import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.foobar_dt_ad.R;

import java.util.ArrayList;

import entities.Comment;
import viewmodels.CommentViewModel;

//This class is adapter to a the comment list
public class CommentListAdapter extends ArrayAdapter<Comment> {
    private final LayoutInflater inflater;
    private final Context context;
    private final CommentViewModel commentVM;
    private final String userId;
    private int numOfChanges;

    /**
     * This function is a c'tor
     * @param context the context from the activity that create the adapter
     * @param cm comment model to handle data
     * @param userId user id
     */
    public CommentListAdapter(@NonNull Context context, CommentViewModel cm, String userId) {
        super(context, R.layout.comment_list_item,new ArrayList<>());
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.commentVM = cm;
        this.userId = userId;
        numOfChanges = 0;
    }

    /**
     * This function handle the items in the list
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return the view
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //The current comment
        Comment curr = getItem(position);
        if (curr == null)
            return new View(context);
        //make place to a new comment
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.comment_list_item, parent, false);
        }

        //get the elements in the screen
        ImageView imageView = convertView.findViewById(R.id.imgComment);
        TextView userName = convertView.findViewById(R.id.userNameComment);
        EditText text = convertView.findViewById(R.id.textComment);
        ImageButton edit = convertView.findViewById(R.id.btnComEdit);
        ImageButton del = convertView.findViewById(R.id.btnComDel);

        //set the user of the comment
        imageView.setImageBitmap(curr.getImg());
        userName.setText(curr.getFirstName()+ " "+ curr.getLastName());
        text.setText(curr.getText());

        //handle the edit button clicked
        edit.setOnClickListener(v -> {
            //the case that edit pressed on the first time
            handleEdit(edit,text,curr);
        });

        //handle the delete button clicked
        del.setOnClickListener(v -> {
            //commentViewModel.remove(comment.getId());
            commentVM.deleteComment(curr);
            numOfChanges--;
            notifyDataSetChanged();
        });

        //allow to edit and delete only if this comment is belong to the current user
        if (!userId.equals(curr.getUserId())) {
            edit.setVisibility(View.INVISIBLE);
            del.setVisibility(View.INVISIBLE);
        }
        else {
            edit.setVisibility(View.VISIBLE);
            del.setVisibility(View.VISIBLE);
        }

        // Determine the current night mode
        LinearLayout commentLayout = convertView.findViewById(R.id.commentLayout);
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // Light mode
            text.setTextColor(context.getResources().getColor(R.color.darkC));
            commentLayout.setBackgroundColor(context.getResources().getColor(R.color.dayC));
        } else {
            // Dark mode
            text.setTextColor(context.getResources().getColor(R.color.dayC));
            commentLayout.setBackgroundColor(context.getResources().getColor(R.color.lightDark));
        }

        return convertView;
    }

    /**
     * handle the  edit of comment
     *
     * @param edit edit button
     * @param text text of comment
     * @param curr current comment
     */
    private void handleEdit(ImageButton edit,EditText text, Comment curr) {
        if (edit.getTag().equals("notEdit")) {
            edit.setTag("editing");
            edit.setImageResource(R.drawable.ic_send);
            text.setFocusableInTouchMode(true);
            edit.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.buttonLight));
            text.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.buttonLight));
        }
        //the user end to edit and click again
        else {
            //check that there is a content to the comment
            if (text.getText().toString().equals("")) {
                commentVM.deleteComment(curr);
                numOfChanges--;
                notifyDataSetChanged();
                return;
            }
            //disable the option of edit
            edit.setTag("notEdit");
            edit.setImageResource(R.drawable.ic_edit);
            edit.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.transparent));
            text.setFocusable(false);
            text.setBackgroundTintList(ContextCompat.getColorStateList(context,R.color.transparent));
            curr.setText(text.getText().toString());
            commentVM.updateComment(curr);
            notifyDataSetChanged();
        }
    }

    public int getNumOfChanges() {
        return numOfChanges;
    }

    public void addOneChange() {
        numOfChanges++;
    }
}