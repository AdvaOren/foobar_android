package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import entities.Friend;
import repositories.FriendRepo;

public class FriendViewModel extends ViewModel {
    private LiveData<List<Friend>> friends;
    private LiveData<String> btnText;
    private FriendRepo friendRepo;
    private String requested;


    public void initializeFriendViewModel(String jwtToken, String requested) {
        friendRepo = new FriendRepo(jwtToken);
        friends = friendRepo.getAsk(requested);
        this.requested = requested;
    }

    public LiveData<List<Friend>> get() {
        return friends;
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
