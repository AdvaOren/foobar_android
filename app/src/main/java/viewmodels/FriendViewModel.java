package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import entities.Friend;
import repositories.FriendRepo;

public class FriendViewModel extends ViewModel {
    private LiveData<List<Friend>> friendsAsk;
    private LiveData<List<Friend>> myFriends;
    private LiveData<String> btnText;
    private FriendRepo friendRepo;
    private String requested;


    public void initializeFriendViewModel(String jwtToken, String requested) {
        friendRepo = new FriendRepo(jwtToken);
        this.requested = requested;
    }

    public LiveData<List<Friend>> getAsk() {
        friendsAsk = friendRepo.getAsk(requested);
        return friendsAsk;
    }

    public LiveData<List<Friend>> getMyFriends(String currUserId) {
        myFriends = friendRepo.getMyFriends(requested,currUserId);
        return myFriends;
    }

    public void acceptFriend(Friend friend) {
        friendRepo.acceptFriend(friend);
    }

    public void rejectFriend(Friend friend) {
        friendRepo.rejectFriend(friend);
    }

    public LiveData<String> getTextForBtn(String requester) {
        btnText =  friendRepo.getTextForBtn(requested,requester);
        return btnText;
    }

    public void askToBeFriend(String loginUserId) {
        friendRepo.askToBeFriend(loginUserId,requested);
    }
}
