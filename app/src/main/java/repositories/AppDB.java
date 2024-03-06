package repositories;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import entities.PostInfo;
import entities.Member;
import entities.Post;

@Database(entities = {Post.class, Member.class, PostInfo.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class AppDB extends RoomDatabase {
    public abstract PostDao postDao();

    public abstract MemberDao memberDao();
    public abstract PostInfoDao likeDao();
}