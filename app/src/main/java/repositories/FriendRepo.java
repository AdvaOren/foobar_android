package repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import api.FriendAPI;
import entities.Friend;

public class FriendRepo {
    private final MutableLiveData<List<Friend>> friends;
    private final MutableLiveData<String> btnText;
    private final FriendAPI friendAPI;


    public FriendRepo(String jwtToken) {
        friends = new MutableLiveData<>();
        btnText = new MutableLiveData<>();
        friendAPI = new FriendAPI(jwtToken,friends,btnText);
    }


    public LiveData<List<Friend>> get() {
        return friends;
    }

    public LiveData<List<Friend>> getAsk(String requested) {
        friendAPI.getAsk(requested);
        return friends;
    }

    public void acceptFriend(Friend friend) {
        friendAPI.acceptFriend(friend);
    }

    public void rejectFriend(Friend friend) {
        friendAPI.rejectFriend(friend);
    }
    public void getAll(String requester) {

    }

    public LiveData<String> getTextForBtn(String requested, String requester) {
        friendAPI.btnText(requested,requester);
        return btnText;
    }

    public void askToBeFriend(String requester, String requested) {
        friendAPI.askToBeFriend(requester,requested);
    }
}
