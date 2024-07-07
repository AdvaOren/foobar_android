package viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import entities.Member;
import repositories.MemberRepo;

/**
 * ViewModel class responsible for managing member-related data and interactions.
 */
public class MemberViewModel extends ViewModel {
    private MemberRepo memberRepo;
    private LiveData<Member> currentMember;
    private LiveData<String> jwt;

    /**
     * Default constructor for MemberViewModel.
     */
    public MemberViewModel(){}

    /**
     * Retrieves the LiveData object containing the current member information.
     *
     * @return A LiveData object containing the current member.
     */
    public LiveData<Member> getCurrentMember() {
        return currentMember;
    }

    /**
     * Initializes the MemberViewModel with the provided context.
     *
     * @param context The context.
     */
    public void initializeMemberViewModel(Context context) {
        memberRepo = new MemberRepo(context);
        currentMember = memberRepo.getCurrentMember();
        jwt = memberRepo.initJwt();
    }


    /**
     * Retrieves a member quickly by their ID.
     *
     * @param id The ID of the member.
     * @return The member object.
     */
    public Member getMemberQuick(String id) {return memberRepo.getMemberQuick(id);}

    /**
     * Retrieves a member by their email.
     *
     * @param email The email of the member.
     */
    public void getMemberByEmail(String email) {
        memberRepo.getMemberByEmail(email);
    }


    /**
     * Updates the JWT token.
     *
     * @param context The context.
     * @param token   The new JWT token.
     */
    public void updateToken(Context context,String token) {
        memberRepo = new MemberRepo(context,token);
    }

    /**
     * Retrieves the JWT token for the specified member.
     *
     * @param member The member for which to retrieve the JWT token.
     */
    public void getJWT(Member member) {
        memberRepo.getJWT(member);
    }

    /**
     * Retrieves the JWT token.
     *
     * @return A LiveData object containing the JWT token.
     */
    public LiveData<String> getJwt() {
        return jwt;
    }

    /**
     * Adds a new member.
     *
     * @param member The member to add.
     */
    public void addMember(Member member) {
        memberRepo.addMember(member);
    }

    /**
     * Updates a member's information.
     *
     * @param member The member to update.
     */
    public void updateUser(Member member) {
        memberRepo.updateMember(member);
    }

    /**
     * Saves a member's information.
     *
     * @param member The member to save.
     */
    public void saveMember(Member member) {
        memberRepo.saveMember(member);
    }

    /**
     * Deletes a member.
     *
     * @param toDelete The member to delete.
     */
    public void delete(Member toDelete) {
        memberRepo.delete(toDelete);
    }
}
