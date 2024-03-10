package repositories;

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

public class PostRepo {

    private final PostDao postDao;
    private final PostInfoDao postInfoDao;
    private final PostListData posts;
    private final PostListData postsByUser;
    private final PostAPI postAPI;
    private final MemberViewModel memberVm;


    /**
     * Constructor for the PostRepo class.
     * Loads posts from a JSON file and initializes the postList.
     *
     * @param context The activity context.
     */
    public PostRepo(Activity context, String jwtToken, MemberViewModel memberVM) {
        AppDB db = Room.databaseBuilder(context, AppDB.class, "Foobar_DAT").build();
        postDao = db.postDao();
        postInfoDao = db.postInfoDao();
        posts = new PostListData();
        postsByUser = new PostListData();
        postAPI = new PostAPI(posts, postDao,postInfoDao, jwtToken);
        this.memberVm = memberVM;
    }

    public void updateNumComments(String userId,String postId,int numChanges) {
        new Thread(() -> {
            PostInfo postInfo = postInfoDao.getPostInfo(userId, postId);
            if (postInfo == null)
                return;
            postInfo.setCommentsAmount(postInfo.getCommentsAmount()+ numChanges);
            postInfoDao.update(postInfo);
            posts.updateNumComments(postInfo);
        }).start();
    }


    public class PostListData extends MutableLiveData<List<Post>> {
        public PostListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                Member member = memberVm.getCurrentMember().getValue();
                if (member != null)
                    posts.postValue(postDao.getAll(member.get_id()));
            });
        }

        public void addPost(Post newPost) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            posts1.add(0,newPost);
            posts.postValue(posts1);
        }

        public void removePost(Post post) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            posts1.remove(post);
            posts.postValue(posts1);
        }

        public void updatePost(Post post) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            String postId = post.get_id();
            for (Post curr : posts1) {
                if (curr.get_id().equals(postId)) {
                    curr.setImg(post.getImg());
                    curr.setContent(post.getContent());
                    posts.postValue(posts1);
                    break;
                }
            }
        }

        public void updateNumComments(PostInfo postInfo) {
            List<Post> posts1 = getValue();
            if (posts1 == null)
                return;
            for (Post curr: posts1) {
                if (curr.get_id().equals(postInfo.getPostId())) {
                    curr.setComments(postInfo.getCommentsAmount());
                    posts.postValue(posts1);
                    break;
                }
            }
        }
    }

    public LiveData<List<Post>> getAll(String userId) {
        postAPI.getLastPosts(userId, memberVm);
        return posts;
    }

    public void add(String userId, Post post) {
        postAPI.addPost(userId, post);
    }

    public void reload(String userId) {
        postAPI.getLastPosts(userId, memberVm);
    }

    public void delete(String userId, Post post) {
        postAPI.deletePost(userId, post);
    }

    public void update(String userId, Post post) {
        if (post.getContent().equals("") && post.getImg().equals(""))
            return;
        if (post.getContent().equals("") || post.getImg().equals(""))
            postAPI.updatePost(userId, post);
        else
            postAPI.updateAllThePost(userId, post);
    }



    public void addLike(String userId, String postId) {
        new Thread(() -> {
            PostInfo postInfo = postInfoDao.getPostInfo(userId, postId);
            postInfo.setLiked(true);
            postInfo.setLikeAmount(postInfo.getLikeAmount() + 1);
            postInfoDao.update(postInfo);
            postAPI.addLike(userId, postId);
        }).start();
    }

    public void removeLike(String userId, String postId) {
        new Thread(() -> {
            PostInfo postInfo = postInfoDao.getPostInfo(userId, postId);
            postInfo.setLiked(false);
            postInfo.setLikeAmount(postInfo.getLikeAmount() - 1);
            postInfoDao.update(postInfo);
            postAPI.removeLike(userId, postId);
        }).start();
    }

    public LiveData<List<Post>> getPostsByUser(String userId,String requester) {
        postAPI.getUserPosts(postsByUser,userId,requester);
        return postsByUser;
    }

    public void reloadByUser(String requested,String requester) {
        postAPI.getUserPosts(postsByUser,requested,requester);
    }
}
