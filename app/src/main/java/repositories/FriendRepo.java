package repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import api.FriendAPI;
import entities.Friend;

/**
 * Repository class for managing friend-related operations.
 */
public class FriendRepo {
    private final MutableLiveData<List<Friend>> friendsAsk;
    private final MutableLiveData<List<Friend>> myFriends;
    private final MutableLiveData<String> btnText;
    private final FriendAPI friendAPI;

    /**
     * Constructor for initializing FriendRepo.
     *
     * @param jwtToken The JWT token for authorization.
     */
    public FriendRepo(String jwtToken) {
        friendsAsk = new MutableLiveData<>();
        myFriends = new MutableLiveData<>();
        btnText = new MutableLiveData<>();
        friendAPI = new FriendAPI(jwtToken, friendsAsk,btnText);
    }

    /**
     * Retrieves friend requests for a user.
     *
     * @param requested The ID of the user who received the friend requests.
     * @return LiveData holding the list of friend requests.
     */
    public LiveData<List<Friend>> getAsk(String requested) {
        friendAPI.getAsk(requested);
        return friendsAsk;
    }

    /**
     * Accepts a friend request.
     *
     * @param friend The friend to accept.
     */
    public void acceptFriend(Friend friend) {
        friendAPI.acceptFriend(friend);
    }

    /**
     * Rejects a friend request.
     *
     * @param friend The friend to reject.
     */
    public void rejectFriend(Friend friend) {
        friendAPI.rejectFriend(friend);
    }

    /**
     * Retrieves text for button status.
     *
     * @param requested The ID of the user who received the friend request.
     * @param requester The ID of the user who sent the friend request.
     * @return LiveData holding the text for button status.
     */
    public LiveData<String> getTextForBtn(String requested, String requester) {
        friendAPI.btnText(requested,requester);
        return btnText;
    }

    /**
     * Sends a friend request.
     *
     * @param requester The ID of the user who sent the friend request.
     * @param requested The ID of the user who received the friend request.
     */
    public void askToBeFriend(String requester, String requested) {
        friendAPI.askToBeFriend(requester,requested);
    }

    /**
     * Retrieves the list of friends for a user.
     *
     * @param userId The ID of the user whose friends are being retrieved.
     * @param currUserId The ID of the current user.
     * @return LiveData holding the list of friends.
     */
    public LiveData<List<Friend>> getMyFriends(String userId,String currUserId) {
        friendAPI.getFriends(userId,myFriends,currUserId);
        return myFriends;
    }
}
