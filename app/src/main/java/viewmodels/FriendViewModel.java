package viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import entities.Friend;
import repositories.FriendRepo;

/**
 * ViewModel class responsible for managing friend-related data and interactions.
 */
public class FriendViewModel extends ViewModel {
    private LiveData<List<Friend>> friendsAsk;
    private LiveData<List<Friend>> myFriends;
    private LiveData<String> btnText;
    private FriendRepo friendRepo;
    private String requested;

    /**
     * Initializes the FriendViewModel with the provided JWT token and requested user ID.
     *
     * @param jwtToken   The JWT token.
     * @param requested  The ID of the requested user.
     */
    public void initializeFriendViewModel(String jwtToken, String requested) {
        friendRepo = new FriendRepo(jwtToken);
        this.requested = requested;
    }

    /**
     * Retrieves friend requests for the requested user.
     *
     * @return A LiveData object containing a list of friend requests.
     */
    public LiveData<List<Friend>> getAsk() {
        friendsAsk = friendRepo.getAsk(requested);
        return friendsAsk;
    }

    /**
     * Retrieves the list of friends of the requested user.
     *
     * @param currUserId The ID of the current user.
     * @return A LiveData object containing a list of friends.
     */
    public LiveData<List<Friend>> getMyFriends(String currUserId) {
        myFriends = friendRepo.getMyFriends(requested,currUserId);
        return myFriends;
    }

    /**
     * Accepts a friend request.
     *
     * @param friend The friend request to accept.
     */
    public void acceptFriend(Friend friend) {
        friendRepo.acceptFriend(friend);
    }

    /**
     * Rejects a friend request.
     *
     * @param friend The friend request to reject.
     */
    public void rejectFriend(Friend friend) {
        friendRepo.rejectFriend(friend);
    }

    /**
     * Retrieves text for the button related to friend requests.
     *
     * @param requester The ID of the requester.
     * @return A LiveData object containing the text for the button.
     */
    public LiveData<String> getTextForBtn(String requester) {
        btnText =  friendRepo.getTextForBtn(requested,requester);
        return btnText;
    }

    /**
     * Sends a friend request to the requested user.
     *
     * @param loginUserId The ID of the user sending the request.
     */
    public void askToBeFriend(String loginUserId) {
        friendRepo.askToBeFriend(loginUserId,requested);
    }
}
