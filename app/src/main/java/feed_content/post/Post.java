package feed_content.post;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import feed_content.comment.Comment;

public class Post {

    private int id;
    private String firstName;
    private String lastName;
    private String title;
    private String content;
    private String date;
    private int likes;
    private Bitmap postPic;
    private Bitmap userPic;
    private List<Comment> commentList;
    private boolean liked;
    private boolean shareClicked;

    // Constructor
    public Post(int id, String title, String firstName, String lastName, String content, Bitmap postPic, Bitmap userPic, String date) {
        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.content = content;
        this.date = date;
        this.userPic = userPic;
        commentList = new ArrayList<>();
        likes = 0;
        this.postPic = postPic;
        liked = false;
        shareClicked = false;
    }

    // Getters and setters for various fields

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

    public Bitmap getUserPic() {
        return userPic;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPicBit(Bitmap picBit) {
        this.postPic = picBit;
    }

    public Bitmap getPicBit() {
        return postPic;
    }

    public String getLastName() {
        return lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
