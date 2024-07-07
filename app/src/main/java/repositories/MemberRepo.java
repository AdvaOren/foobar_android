package repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import api.UserAPI;
import daos.AppDB;
import daos.MemberDao;
import entities.Member;

/**
 * Repository class for managing member data.
 */
public class MemberRepo {
    private UserAPI userAPI;
    private MemberDao dao;
    private MutableLiveData<Member> currentMember;
    private MutableLiveData<String> jwt;

    /**
     * Constructor for initializing MemberRepo.
     *
     * @param context The application context.
     */
    public MemberRepo(Context context) {
        this(context,"");
    }
    /**
     * Constructor for initializing MemberRepo with JWT token.
     *
     * @param context The application context.
     * @param token The JWT token.
     */
    public MemberRepo(Context context,String token) {
        AppDB db = Room.databaseBuilder(context, AppDB.class, "Foobar_DAT")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        dao = db.memberDao();
        this.userAPI = new UserAPI(token,dao);
        currentMember = new MutableLiveData<>(new Member("","","","","",""));
        jwt = new MutableLiveData<>("");
    }

    /**
     * Retrieves a member by email.
     *
     * @param email The email of the member to retrieve.
     */
    public void getMemberByEmail(String email) {
        userAPI.getMemberByEmail(email,currentMember);
    }


    /**
     * Retrieves the JWT token.
     *
     * @param member The member for which to retrieve the JWT token.
     */
    public void getJWT(Member member) {
        userAPI.getJWT(member,jwt);
    }

    /**
     * Initializes the JWT token.
     *
     * @return LiveData holding the JWT token.
     */
    public LiveData<String> initJwt() {return jwt;}

    /**
     * Adds a new member.
     *
     * @param member The member to add.
     */
    public void addMember(Member member) {
        userAPI.addUser(member,currentMember);
    }

    /**
     * Retrieves the LiveData containing the current member.
     *
     * @return LiveData holding the current member.
     */
    public LiveData<Member> getCurrentMember() {
        return currentMember;
    }

    /**
     * Saves a member to the database.
     *
     * @param member The member to save.
     */
    public void saveMember(Member member) {
        dao.insert(member);
    }

    /**
     * Retrieves a member quickly by ID.
     *
     * @param id The ID of the member to retrieve.
     * @return The member retrieved from the database.
     */
    public Member getMemberQuick(String id) {
        return dao.getById(id);
    }

    /**
     * Updates a member.
     *
     * @param member The member to update.
     */
    public void updateMember(Member member) {
        userAPI.updateMember(member);
    }

    /**
     * Deletes a member.
     *
     * @param toDelete The member to delete.
     */
    public void delete(Member toDelete) {
        userAPI.deleteMember(toDelete);
    }
}
