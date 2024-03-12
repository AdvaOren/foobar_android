package daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entities.Member;

/**
 * Data Access Object (DAO) interface for the Member entity.
 * Defines methods to interact with the Member entity in the database.
 */
@Dao
public interface MemberDao {

    @Query("SELECT * FROM members WHERE email = :email and password = :password")
    Member get(String email, String password);

    @Query("SELECT * FROM members WHERE email = :email")
    Member getByEmail(String email);

    @Query("SELECT * FROM members WHERE _id = :id")
    Member getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Member... members);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Member> members);

    @Update
    void update(Member... members);

    @Delete
    void delete(Member... members);

    @Query("DELETE FROM members")
    void clear();

}