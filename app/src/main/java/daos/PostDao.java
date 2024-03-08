package daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Post;

@Dao
public interface PostDao {
    @Query("SELECT * FROM post WHERE owner = :owner")
    List<Post> getAll(String owner);

    @Query("SELECT * FROM post WHERE _id = :id")
    Post getPost(String id);

    @Query("SELECT * FROM post WHERE userId = :id")
    List<Post> getUserPosts(String id);

    @Query("SELECT userId FROM post")
    List<String> getUserIds();

    @Insert
    void insert(List<Post> posts);
    @Insert
    void insert(Post... posts);

    @Update
    void update(Post... posts);

    @Delete
    void delete(Post... posts);

    @Query("DELETE FROM post WHERE userId = :userId")
    void deleteUserPosts(String userId);

    @Query("DELETE FROM post WHERE _id = :id")
    void deletePostById(String id);

    @Query("DELETE FROM post WHERE owner = :userId")
    void clear(String userId);


}