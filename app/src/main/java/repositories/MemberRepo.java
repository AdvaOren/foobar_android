package repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.util.List;

import api.UserAPI;
import daos.AppDB;
import daos.MemberDao;
import entities.Member;

public class MemberRepo {
    private UserAPI userAPI;
    private MemberDao dao;
    private MutableLiveData<Member> currentMember;
    private MutableLiveData<String> jwt;

    public MemberRepo(Context context) {
        this(context,"");
    }
    public MemberRepo(Context context,String token) {
        AppDB db = Room.databaseBuilder(context, AppDB.class, "Foobar_DAT")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        dao = db.memberDao();
        this.userAPI = new UserAPI(token,dao);
        currentMember = new MutableLiveData<>(new Member("","","","","",""));
        jwt = new MutableLiveData<>("");
    }

    public void getMemberByEmail(String email) {
        userAPI.getMemberByEmail(email,currentMember);
    }

    public void getMember(String id) {
        userAPI.getMember(id,currentMember);
        /*final Member[] member = {dao.getById(id)};
        new Thread(() -> {
            member[0] = userAPI.getMember(id);
        }).start();
        return member[0];*/
    }

    public void getJWT(Member member) {
        userAPI.getJWT(member,jwt);
    }

    public LiveData<String> initJwt() {return jwt;}

    public void addMember(Member member) {
        userAPI.addUser(member);
    }


    public void updateMembers(List<String> ids) {
        userAPI.getMembers(ids);
    }

    public LiveData<Member> getCurrentMember() {
        return currentMember;
    }

    public void saveMember(Member member) {
        dao.insert(member);
    }

    public Member getMemberQuick(String id) {
        return dao.getById(id);
    }
}
