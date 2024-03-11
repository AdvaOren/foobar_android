package repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import api.FriendAPI;
import entities.Friend;

public class FriendRepo {
    private final MutableLiveData<List<Friend>> friendsAsk;
    private final MutableLiveData<List<Friend>> myFriends;
    private final MutableLiveData<String> btnText;
    private final FriendAPI friendAPI;


    public FriendRepo(String jwtToken) {
        friendsAsk = new MutableLiveData<>();
        myFriends = new MutableLiveData<>();
        btnText = new MutableLiveData<>();
        friendAPI = new FriendAPI(jwtToken, friendsAsk,btnText);
    }


    public LiveData<List<Friend>> getAsk(String requested) {
        friendAPI.getAsk(requested);
        return friendsAsk;
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

    public LiveData<List<Friend>> getMyFriends(String userId,String currUserId) {
        friendAPI.getFriends(userId,myFriends,currUserId);
        return myFriends;
    }
}
