package repositories;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.PostInfo;

@Dao
public interface PostInfoDao {

    @Query("SELECT likeAmount FROM postInfo WHERE postId = :postId AND userId = :userId")
    int getLikeAmount(String userId, String postId);

    @Query("SELECT isLiked FROM postInfo WHERE postId = :postId AND userId = :userId")
    boolean getIfLike(String userId, String postId);

    @Query("SELECT * FROM postInfo WHERE postId = :postId AND userId = :userId")
    PostInfo getPostInfo(String userId, String postId);

    @Insert
    void insert(PostInfo... postInfos);

    @Insert
    void insert(List<PostInfo> postInfos);

    @Delete
    void delete(PostInfo... postInfos);

    @Query("DELETE FROM postInfo WHERE postId = :postId AND userId = :userId")
    void delete(String userId, String postId);

    @Query("DELETE FROM postInfo WHERE userId = :userId")
    void clear(String userId);

    @Update
    void update(PostInfo... postInfos);

    @Query("UPDATE postInfo SET likeAmount = likeAmount + 1 WHERE userId = :userId AND postId = :postId")
    void addLike(String userId, String postId);

}
