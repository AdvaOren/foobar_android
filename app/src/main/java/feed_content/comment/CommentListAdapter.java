package feed_content.comment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foobar_dt_ad.R;

//This class is adapter to a simple list
public class CommentListAdapter extends ArrayAdapter<Comment> {
    private LayoutInflater inflater;
    private Context context;
    private CommentModel commentModel;
    private Bitmap userImg;

    /**
     * This function is a c'tor
     * @param context the context from the activity that create the adapter
     * @param cm comment model to handle data
     * @param userImg user img
     */
    public CommentListAdapter(@NonNull Context context, CommentModel cm,Bitmap userImg) {
        super(context, R.layout.comment_list_item,cm.get());
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.commentModel = cm;
        this.userImg = userImg;
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
        Comment comment = getItem(position);
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
        imageView.setImageBitmap(userImg);
        userName.setText(comment.getName());
        text.setText(comment.getText());

        //handle the edit button clicked
        edit.setOnClickListener(v -> {
            //the case that edit pressed on the first time
            if (edit.getTag().equals("notEdit")) {
                edit.setTag("editing");
                edit.setImageResource(R.drawable.ic_send);
                text.setFocusableInTouchMode(true);
                text.setBackgroundColor(Color.parseColor("#00ffff"));
            }
            //the user end to edit and click again
            else {
                //check that there is a content to the comment
                if (text.getText().toString().equals("")) {
                    commentModel.remove(comment.getId());
                    notifyDataSetChanged();
                    return;
                }
                //disable the option of edit
                edit.setTag("notEdit");
                edit.setImageResource(R.drawable.ic_edit);
                text.setFocusable(false);
                text.setBackgroundColor(Color.parseColor("#FFF1DD"));
                commentModel.edit(comment.getId(),text.getText().toString());
                notifyDataSetChanged();
            }
        });

        //handle the delete button clicked
        del.setOnClickListener(v -> {
            commentModel.remove(comment.getId());
            notifyDataSetChanged();
        });

        return convertView;
    }
}