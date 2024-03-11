package viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import entities.Member;
import repositories.MemberRepo;

public class MemberViewModel extends ViewModel {
    private MemberRepo memberRepo;
    private LiveData<Member> currentMember;
    private LiveData<String> jwt;

    public MemberViewModel(){}

    public LiveData<Member> getCurrentMember() {
        return currentMember;
    }

    public void initializeMemberViewModel(Context context) {
        memberRepo = new MemberRepo(context);
        currentMember = memberRepo.getCurrentMember();
        jwt = memberRepo.initJwt();
    }

    public void getMember(String id) {
        memberRepo.getMember(id);
    }
    public Member getMemberQuick(String id) {return memberRepo.getMemberQuick(id);}

    public void getMemberByEmail(String email) {
        memberRepo.getMemberByEmail(email);
    }

    public void updateToken(Context context,String token) {
        memberRepo = new MemberRepo(context,token);
    }

    public void getJWT(Member member) {
        memberRepo.getJWT(member);
    }

    public LiveData<String> getJwt() {
        return jwt;
    }

    public void addMember(Member member) {
        memberRepo.addMember(member);
    }

    public void updateUser(Member member) {
        memberRepo.updateMember(member);
    }


    public void saveMember(Member member) {
        memberRepo.saveMember(member);
    }

    public void delete(Member toDelete) {
        memberRepo.delete(toDelete);
    }
}
