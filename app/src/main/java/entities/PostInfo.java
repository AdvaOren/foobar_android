package entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"userId","postId"},tableName = "postInfo")
public class PostInfo {

    @NonNull
    private String userId;
    @NonNull
    private String postId;

    private int likeAmount;
    private boolean isLiked;
    private int commentsAmount;

    public PostInfo(@NonNull String userId, @NonNull String postId, int likeAmount, boolean isLiked, int commentsAmount) {
        this.userId = userId;
        this.postId = postId;
        this.likeAmount = likeAmount;
        this.isLiked = isLiked;
        this.commentsAmount = commentsAmount;
    }

    @Ignore
    public PostInfo(@NonNull String postId, int likeAmount, boolean isLiked,int commentsAmount) {
        this.postId = postId;
        this.likeAmount = likeAmount;
        this.isLiked = isLiked;
        this.userId = "";
        this.commentsAmount = commentsAmount;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getPostId() {
        return postId;
    }

    public void setPostId(@NonNull String postId) {
        this.postId = postId;
    }

    public int getLikeAmount() {
        return likeAmount;
    }

    public void setLikeAmount(int likeAmount) {
        this.likeAmount = likeAmount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getCommentsAmount() {
        return commentsAmount;
    }

    public void setCommentsAmount(int commentsAmount) {
        this.commentsAmount = commentsAmount;
    }
}
