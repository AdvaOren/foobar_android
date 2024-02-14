package feed_content.post;

import static feed_content.add_post.AddPost.EDIT;
import static feed_content.comment.CommentsScreen.BACK_FROM_COMMENT;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foobar_dt_ad.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import feed_content.add_post.AddPost;
import feed_content.comment.Comment;
import feed_content.comment.CommentsScreen;

//Adapter for recycle view
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    //This class is a view holder for post
    class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final ImageView postPic;
        private final Button likes;
        private final Button comments;
        private final Button share;

        private final TextView firstName;
        private final TextView lastName;
        private final TextView date;
        private final TextView id;
        private final ImageView userPic;

        private final ImageButton delete;
        private final ImageButton edit;
        private final ImageButton link;
        private final ImageButton email;
        private final ConstraintLayout postLayout;

        /**
         * This function is a constructor
         * @param itemView the view
         */
        private PostViewHolder(View itemView) {
            super(itemView);

            //get the elements in the screen
            title = itemView.findViewById(R.id.postTitle);
            content = itemView.findViewById(R.id.postContent);
            postPic = itemView.findViewById(R.id.postPic);
            likes = itemView.findViewById(R.id.btnLike);
            comments = itemView.findViewById(R.id.btnComment);
            share = itemView.findViewById(R.id.btnShare);
            firstName = itemView.findViewById(R.id.firstName);
            lastName = itemView.findViewById(R.id.lastName);
            userPic = itemView.findViewById(R.id.avatarPic);
            date = itemView.findViewById(R.id.postDate);
            id = itemView.findViewById(R.id.postId);
            delete = itemView.findViewById(R.id.btnPostDel);
            edit = itemView.findViewById(R.id.btnPostEdit);
            link = itemView.findViewById(R.id.shareLink);
            email = itemView.findViewById(R.id.shareEmail);
            postLayout = itemView.findViewById(R.id.postLayout);
            }
    }

    private final LayoutInflater mInflater;
    private List<Post> posts;
    private PostsViewModel viewModel;
    private FragmentActivity activity;
    private ActivityResultLauncher<Intent> intentLauncher;
    private String firstName;
    private String lastName;
    private Bitmap userPic;

    //This is a constructor for the class
    public PostListAdapter(FragmentActivity activity, Context context, PostsViewModel viewModel,
                           Bitmap userPic, String firstName, String lastName) {
        mInflater=LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.activity = activity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userPic = userPic;

        //get result from another activity
        intentLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //get the data from the edit post screen
                        if (result.getResultCode() == EDIT) {
                            Intent data = result.getData();
                            if (data != null) {
                                String title = data.getStringExtra("title");
                                String content = data.getStringExtra("content");
                                Bundle extras = data.getExtras();
                                byte[] byteArray = extras.getByteArray("picture");
                                String date = data.getStringExtra("date");
                                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                int id = Integer.parseInt(data.getStringExtra("id").toString());
                                viewModel.edit(id,title,content,date,bmp);
                                notifyDataSetChanged();
                            }
                        }
                        //get the data from the comments screen
                        else if (result.getResultCode() == BACK_FROM_COMMENT) {
                            Intent data = result.getData();
                            if (data != null) {
                                ArrayList<Comment> comments= data.getParcelableArrayListExtra("comments");
                                int id = data.getIntExtra("id",-1);
                                viewModel.updateComments(id,comments);
                                notifyDataSetChanged();
                            }
                        }
                    }
                }
        );
    }

    /**
     * This function create place for new post
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return the new place
     */
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.post_layout,parent,false);
        return new PostViewHolder(itemView);
    }

    /**
     * This function handle the post before it's present on screen
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        if (posts != null) {
            Post current = posts.get(position);
            initHolder(holder,current);

            //handle the case the user delete post
            holder.delete.setOnClickListener(v -> {
                viewModel.delete(Integer.parseInt(holder.id.getText().toString()));
                setPosts(viewModel.get());
                notifyDataSetChanged();
            });

            //handle the case the user edit post
            holder.edit.setOnClickListener(v -> {
                editPost(holder);
            });

            //transfer the user to the comment screen
            holder.comments.setOnClickListener(v -> {
                Intent i = new Intent(mInflater.getContext(), CommentsScreen.class);
                i.putExtra("numComments",String.valueOf(current.getCommentList().size()));
                i.putParcelableArrayListExtra("comments",new ArrayList<>(current.getCommentList()));
                i.putExtra("firstName",firstName);
                i.putExtra("lastName",lastName);
                i.putExtra("id",current.getId());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                userPic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                i.putExtra("picture", imageInByte);
                intentLauncher.launch(i);
            });

            //handle the case the user clicked on the share button
            holder.share.setOnClickListener(v -> {
                viewModel.setShareClicked(current.getId());
                if (current.isShareClicked()) {
                    holder.link.setVisibility(View.VISIBLE);
                    holder.email.setVisibility(View.VISIBLE);
                }
                else {
                    holder.link.setVisibility(View.INVISIBLE);
                    holder.email.setVisibility(View.INVISIBLE);
                }
            });

            //handle the case the user click like
            holder.likes.setOnClickListener(v -> {
                likePost(current,holder);
            });

            int currentNightMode = mInflater.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                // Light mode
                holder.postLayout.setBackgroundResource(R.color.dayC);
                holder.title.setTextColor(mInflater.getContext().getResources().getColor(R.color.darkC));
                holder.content.setTextColor(mInflater.getContext().getResources().getColor(R.color.darkC));
                holder.firstName.setTextColor(mInflater.getContext().getResources().getColor(R.color.darkC));
                holder.lastName.setTextColor(mInflater.getContext().getResources().getColor(R.color.darkC));
                holder.date.setTextColor(mInflater.getContext().getResources().getColor(R.color.darkC));
                holder.likes.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.buttonLight));
                holder.share.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.buttonLight));
                holder.comments.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.buttonLight));
            } else {
                // Dark mode
                holder.postLayout.setBackgroundResource(R.color.darkC);
                holder.title.setTextColor(mInflater.getContext().getResources().getColor(R.color.dayC));
                holder.content.setTextColor(mInflater.getContext().getResources().getColor(R.color.dayC));
                holder.firstName.setTextColor(mInflater.getContext().getResources().getColor(R.color.dayC));
                holder.lastName.setTextColor(mInflater.getContext().getResources().getColor(R.color.dayC));
                holder.date.setTextColor(mInflater.getContext().getResources().getColor(R.color.dayC));
                holder.likes.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.buttonDark));
                holder.share.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.buttonDark));
                holder.comments.setBackgroundColor(mInflater.getContext().getResources().getColor(R.color.buttonDark));
            }
        }
    }

    public void initHolder(PostViewHolder holder, Post current) {
        //set the elements that in the screen with the post
        holder.title.setText(current.getTitle());
        holder.content.setText(current.getContent());
        holder.date.setText(current.getDate());
        holder.firstName.setText(current.getFirstName());
        holder.lastName.setText(current.getLastName());
        holder.likes.setText(current.getLikes() + " likes");
        holder.comments.setText(current.getCommentList().size() + " comments");
        holder.id.setText(Integer.toString(current.getId()));
        holder.userPic.setImageBitmap(current.getUserPic());
        holder.postPic.setImageBitmap(current.getPicBit());

        //check if like press to change icon or not
        Drawable iconLike;
        if (current.isLiked()) {
            iconLike = mInflater.getContext().getResources().getDrawable(R.drawable.ic_like_clicked);
        } else {
            iconLike = mInflater.getContext().getResources().getDrawable(R.drawable.ic_like);
        }
        holder.likes.setCompoundDrawablesWithIntrinsicBounds(null, iconLike , null, null);

        //check if share is press to show 'share buttons'
        if (current.isShareClicked()) {
            holder.link.setVisibility(View.VISIBLE);
            holder.email.setVisibility(View.VISIBLE);
        }
        else {
            holder.link.setVisibility(View.INVISIBLE);
            holder.email.setVisibility(View.INVISIBLE);
        }
    }

    public void setPosts(List<Post> s) {
        posts = s;
    }

    public int getItemCount() {
        if (posts != null)
            return posts.size();
        else return 0;
    }

    public List<Post> getPosts() {
        return posts;
    }

    private void likePost(Post current, PostViewHolder holder){
        viewModel.setLiked(Integer.parseInt(holder.id.getText().toString()));
        if (current.isLiked()) {
            String text = holder.likes.getText().toString();
            String[] arrOfStr = text.split(" ");
            int likeAmount = Integer.parseInt(arrOfStr[0]);
            likeAmount++;
            Drawable top = mInflater.getContext().getResources().getDrawable(R.drawable.ic_like_clicked);
            holder.likes.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
            viewModel.updateLike(Integer.parseInt(holder.id.getText().toString()),likeAmount);
        }
        else {
            String text = holder.likes.getText().toString();
            String[] arrOfStr = text.split(" ");
            int likeAmount = Integer.parseInt(arrOfStr[0]);
            likeAmount--;
            if (likeAmount < 0)
                likeAmount = 0;
            Drawable top = mInflater.getContext().getResources().getDrawable(R.drawable.ic_like);
            holder.likes.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
            viewModel.updateLike(Integer.parseInt(holder.id.getText().toString()),likeAmount);
        }
        notifyDataSetChanged();
    }

    public void editPost(PostViewHolder holder) {
        Intent intent = new Intent(activity, AddPost.class);
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) holder.postPic.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        intent.putExtra("title", holder.title.getText().toString());
        intent.putExtra("content", holder.content.getText().toString());
        intent.putExtra("picture", imageInByte);
        intent.putExtra("type",String.valueOf(EDIT));
        intent.putExtra("id",holder.id.getText().toString());
        intentLauncher.launch(intent);
    }
}
