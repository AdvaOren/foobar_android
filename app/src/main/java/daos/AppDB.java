package daos;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import entities.PostInfo;
import entities.Member;
import entities.Post;

/**
 * Defines the Room database for the application.
 * This database includes three entities: Post, Member, and PostInfo.
 * The database version is set to 7.
 * TypeConverters are used to handle non-primitive types.
 */
@Database(entities = {Post.class, Member.class, PostInfo.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class AppDB extends RoomDatabase {

    /**
     * Provides access to the PostDao interface for performing database operations related to posts.
     *
     * @return an instance of the PostDao interface.
     */
    public abstract PostDao postDao();

    /**
     * Provides access to the MemberDao interface for performing database operations related to members.
     *
     * @return an instance of the MemberDao interface.
     */
    public abstract MemberDao memberDao();

    /**
     * Provides access to the PostInfoDao interface for performing database operations related to post information.
     *
     * @return an instance of the PostInfoDao interface.
     */
    public abstract PostInfoDao postInfoDao();
}
