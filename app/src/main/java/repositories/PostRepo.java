package repositories;

import static com.example.foobar_dt_ad.FeedScreen.FEED;
import static com.example.foobar_dt_ad.UserScreen.USER_SCREEN;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.util.LinkedList;
import java.util.List;

import api.PostAPI;
import daos.AppDB;
import daos.PostDao;
import daos.PostInfoDao;
import entities.Member;
import entities.PostInfo;
import entities.Post;
import viewmodels.MemberViewModel;

/**
 * Repository class for handling post data.
 */
public class PostRepo {

    private final PostDao postDao;
    private final PostInfoDao postInfoDao;
    private final PostListData posts;
    private final PostListData postsByUser;
    private final PostAPI postAPI;
    private final MemberViewModel memberVm;


    /**
     * Constructor for the PostRepo class.
     *
     * @param context   The activity context.
     * @param jwtToken  The JWT token for authorization.
     * @param memberVM  The MemberViewModel instance.
     */
    public PostRepo(Activity context, String jwtToken, MemberViewModel memberVM) {
        AppDB db = Room.databaseBuilder(context, AppDB.class, "Foobar_DAT").build();
        postDao = db.postDao();
        postInfoDao = db.postInfoDao();
        posts = new PostListData();
        postsByUser = new PostListData();
        postAPI = new PostAPI(posts, postDao,postInfoDao, jwtToken,postsByUser);
        this.memberVm = memberVM;
    }

    /**
     * Custom subclass of MutableLiveData to hold a list of posts.
     * Provides methods to modify and update the list of posts.
     */
    public class PostListData extends MutableLiveData<List<Post>> {
        /**
         * Constructor for initializing the PostListData.
         * Initializes the list of posts as an empty LinkedList.
         */
        public PostListData() {
            super();
            setValue(new LinkedList<>());
        }

        /**
         * Called when the LiveData becomes active.
         * Fetches the list of posts for the current member and updates the LiveData.
         */
        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                Member member = memberVm.getCurrentMember().getValue();
                if (member != null)
                    postValue(postDao.getAll(member.get_id()));
            });
        }

        /**
         * Adds a new post to the list of posts and updates the LiveData.
         * @param newPost The post to add.
         */
        public void addPost(Post newPost) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            posts1.add(0,newPost);
            postValue(posts1);
        }

        /**
         * Removes a post from the list of posts.
         *
         * @param post The post to remove.
         */
        public void removePost(Post post) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            posts1.remove(post);
            postValue(posts1);
        }

        /**
         * Updates the content and image of a post.
         *
         * @param post The updated post.
         */
        public void updatePost(Post post) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            String postId = post.get_id();
            for (Post curr : posts1) {
                if (curr.get_id().equals(postId)) {
                    curr.setImg(post.getImg());
                    curr.setContent(post.getContent());
                    postValue(posts1);
                    break;
                }
            }
        }

        /**
         * Updates the number of comments for a post.
         *
         * @param postInfo The post information containing the number of comments.
         */
        public void updateNumComments(PostInfo postInfo) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            for (Post curr: posts1) {
                if (curr.get_id().equals(postInfo.getPostId())) {
                    curr.setComments(postInfo.getCommentsAmount());
                    postValue(posts1);
                    break;
                }
            }
        }
    }

    /**
     * Retrieves all posts associated with a specific user.
     *
     * @param userId The ID of the user.
     * @return A LiveData object containing a list of posts.
     */
    public LiveData<List<Post>> getAll(String userId) {
        postAPI.getLastPosts(userId, memberVm);
        return posts;
    }

    /**
     * Adds a new post for a user.
     *
     * @param userId The ID of the user.
     * @param post   The post to add.
     */
    public void add(String userId, Post post) {
        postAPI.addPost(userId, post);
    }

    /**
     * Reloads all posts associated with a specific user.
     *
     * @param userId The ID of the user.
     */
    public void reload(String userId) {
        postAPI.getLastPosts(userId, memberVm);
    }

    /**
     * Deletes a post for a user.
     *
     * @param userId The ID of the user.
     * @param post   The post to delete.
     */
    public void delete(String userId, Post post, int whereAmI) {
        postAPI.deletePost(userId, post,whereAmI);
    }

    /**
     * Updates a post for a user.
     *
     * @param userId The ID of the user.
     * @param post   The updated post.
     */
    public void update(String userId, Post post, int whereAmI) {
        if (post.getContent().equals("") && post.getImg().equals(""))
            return;
        if (post.getContent().equals("") || post.getImg().equals(""))
            postAPI.updatePost(userId, post,whereAmI);
        else
            postAPI.updateAllThePost(userId, post,whereAmI);
    }


    /**
     * Updates the number of comments for a post.
     *
     * @param userId     The user ID.
     * @param postId     The post ID.
     * @param numChanges The number of changes.
     */
    public void updateNumComments(String userId,String postId,int numChanges, int whereAmI) {
        new Thread(() -> {
            PostInfo postInfo = postInfoDao.getPostInfo(userId, postId);
            if (postInfo == null)
                return;
            postInfo.setCommentsAmount(postInfo.getCommentsAmount()+ numChanges);
            postInfoDao.update(postInfo);
            if (whereAmI == FEED)
                posts.updateNumComments(postInfo);
            else if (whereAmI == USER_SCREEN)
                postsByUser.updateNumComments(postInfo);
        }).start();
    }

    /**
     * Adds a like to a post.
     *
     * @param userId The user ID.
     * @param postId The post ID.
     */
    public void addLike(String userId, String postId) {
        new Thread(() -> {
            PostInfo postInfo = postInfoDao.getPostInfo(userId, postId);
            postInfo.setLiked(true);
            postInfo.setLikeAmount(postInfo.getLikeAmount() + 1);
            postInfoDao.update(postInfo);
            postAPI.addLike(userId, postId);
        }).start();
    }

    /**
     * Removes a like from a post.
     *
     * @param userId The user ID.
     * @param postId The post ID.
     */
    public void removeLike(String userId, String postId) {
        new Thread(() -> {
            PostInfo postInfo = postInfoDao.getPostInfo(userId, postId);
            postInfo.setLiked(false);
            postInfo.setLikeAmount(postInfo.getLikeAmount() - 1);
            postInfoDao.update(postInfo);
            postAPI.removeLike(userId, postId);
        }).start();
    }

    /**
     * Retrieves all posts by a specific user.
     *
     * @param userId    The ID of the user.
     * @param requester The ID of the requester.
     * @return A LiveData object containing a list of posts.
     */
    public LiveData<List<Post>> getPostsByUser(String userId,String requester) {
        postAPI.getUserPosts(postsByUser,userId,requester);
        return postsByUser;
    }

    /**
     * Reloads all posts by a specific user.
     *
     * @param requested The ID of the user.
     * @param requester The ID of the requester.
     */
    public void reloadByUser(String requested,String requester) {
        postAPI.getUserPosts(postsByUser,requested,requester);
    }


    /**
     * Deletes all posts and post information associated with a user.
     *
     * @param userId The ID of the user.
     */
    public void deleteUser(String userId) {
        new Thread(() -> {
            postDao.clear(userId);
            postDao.deleteUserPosts(userId);
            postInfoDao.clear(userId);
        }).start();
    }

}
