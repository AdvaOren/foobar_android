package entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Entity
public class Post {

    @PrimaryKey
    @NonNull
    private String _id;
    private String userId;
    private String content;
    private String date;
    private int likes;
    private String img;
    //private List<Comment> commentList;
    private int comments;
    private boolean liked;
    private boolean shareClicked;
    private String owner;

    // Constructor
    public Post(String content, Bitmap img, String date, String userId) {
        _id = "";
        this.content = content;
        this.date = date;
        this.userId = userId;
        //commentList = new ArrayList<>();
        likes = 0;
        setImgBitmap(img);
        liked = false;
        shareClicked = false;
        owner = "";
        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //img.compress(Bitmap.CompressFormat.PNG, 100, stream);
    }

    public Post(@NonNull String _id, String userId, String content, String date, String img, String owner) {
        this._id = _id;
        this.userId = userId;
        this.content = content;

        this.date = date;
        this.img = img;
        this.owner = owner;
        shareClicked = false;
    }
// Getters and setters for various fields

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUserId() {
        return userId;
    }

    public void setShareClicked(boolean shareClicked) {
        this.shareClicked = shareClicked;
    }

    public boolean isShareClicked() {
        return shareClicked;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setImgBitmap(Bitmap img) {

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        this.img = Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap getImgBitmap() {
        try {
            byte [] encodeByte=Base64.decode(img,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getImg() {
        return img;
    }

    /*public String getLastName() {
        return lastName;
    }*/

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<Comment> getCommentList() {
        //return commentList;
        return null;
    }

    /*public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }*/
}
