package api;

import static com.example.foobar_dt_ad.FeedScreen.FEED;
import static com.example.foobar_dt_ad.UserScreen.USER_SCREEN;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import daos.PostDao;
import daos.PostInfoDao;
import entities.Member;
import entities.Post;
import entities.PostInfo;
import kotlin.Triple;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import repositories.PostRepo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import viewmodels.MemberViewModel;

/**
 * Manages the API calls related to posts.
 */
public class PostAPI {
    private final PostRepo.PostListData postListData;
    private final PostRepo.PostListData postListDataUser;
    private final PostDao dao;
    private final WebServicesAPI webServicesAPI;
    private final PostInfoDao infoDao;
    private final Post internetFailed;

    /**
     * Constructor for initializing PostAPI.
     *
     * @param postListData MutableLiveData object to hold the list of posts
     * @param dao           Data access object for post database operations
     * @param infoDao       Data access object for post information database operations
     * @param token         JWT token for authorization
     */
    public PostAPI(PostRepo.PostListData postListData, PostDao dao, PostInfoDao infoDao
            , String token, PostRepo.PostListData postListDataUser) {
        this.postListData = postListData;
        this.postListDataUser = postListDataUser;
        this.dao = dao;
        this.infoDao = infoDao;
        internetFailed = new Post("FAILED TO CONNECT TO SERVER\nPLEASE TRY AGAIN", null, "", "no internet");

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Add the JWT token to the request headers
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.readTimeout(30, TimeUnit.SECONDS).build())
                .build();

        webServicesAPI = retrofit.create(WebServicesAPI.class);
    }

    /**
     * Retrieves posts from the server and updates the database and MutableLiveData object.
     *
     * @param userId The ID of the user whose posts are being retrieved
     * @param mVM    The MemberViewModel instance to save members associated with posts
     */
    private void getPosts(String userId, MemberViewModel mVM) {
        Call<List<Triple<Post, Member, PostInfo>>> call = webServicesAPI.getLastPosts();
        List<Post> posts = new ArrayList<>();
        call.enqueue(new Callback<List<Triple<Post, Member, PostInfo>>>() {
            @Override
            public void onResponse(Call<List<Triple<Post, Member, PostInfo>>> call, Response<List<Triple<Post, Member, PostInfo>>> response) {
                new Thread(() -> {
                    dao.clear(userId);
                    infoDao.clear(userId);
                    List<Triple<Post, Member, PostInfo>> newData = response.body();
                    if (newData == null) {
                        return;
                    }
                    for (Triple<Post, Member, PostInfo> triple : newData) {
                        PostInfo postInfo = triple.getThird();
                        Post post = triple.getFirst();
                        post.setOwner(userId);

                        dao.insert(post);
                        mVM.saveMember(triple.getSecond());
                        infoDao.insert(postInfo);
                        //set post info
                        setPostByInfo(post, postInfo);
                        posts.add(post);
                    }
                    postListData.postValue(posts);
                }).start();
            }

            @Override
            public void onFailure(Call<List<Triple<Post, Member, PostInfo>>> call, Throwable t) {
                posts.add(internetFailed);
                postListData.setValue(posts);
                t.printStackTrace();
            }
        });
    }

    /**
     * Retrieves the last posts for a given user from the local database and the server.
     *
     * @param userId The ID of the user whose last posts are being retrieved
     * @param mVM    The MemberViewModel instance to save members associated with posts
     */
    public void getLastPosts(String userId, MemberViewModel mVM) {
        new Thread(() -> {
            //get posts from local db
            List<Post> posts = dao.getAll(userId);
            for (Post post : posts) {
                PostInfo postInfo = infoDao.getPostInfo(userId, post.get_id());
                setPostByInfo(post,postInfo);
            }
            postListData.postValue(posts);

            //get posts from server
            getPosts(userId, mVM);
        }).start();
    }

    /**
     * Sets the likes, liked status, and comments of a post based on its associated PostInfo object.
     *
     * @param post     The post to be updated
     * @param postInfo The PostInfo object containing the relevant information
     */
    private void setPostByInfo(Post post, PostInfo postInfo) {
        post.setLikes(postInfo.getLikeAmount());
        post.setLiked(postInfo.isLiked());
        post.setComments(postInfo.getCommentsAmount());
    }


    /**
     * Retrieves the posts of a specific user from the server.
     *
     * @param postsUser The MutableLiveData object to hold the list of user posts
     * @param userId    The ID of the user whose posts are being retrieved
     * @param requester The ID of the user making the request
     */
    public void getUserPosts(PostRepo.PostListData postsUser,String userId, String requester) {
        Call<List<Pair<Post, PostInfo>>> call = webServicesAPI.getPostsOfUser(userId);
        List<Post> posts = new ArrayList<>();
        call.enqueue(new Callback<List<Pair<Post, PostInfo>>>() {
            @Override
            public void onResponse(Call<List<Pair<Post, PostInfo>>> call, Response<List<Pair<Post, PostInfo>>> response) {
                new Thread(() -> {
                    List<Pair<Post, PostInfo>> newData = response.body();

                    if ((newData == null || newData.size() == 0) && requester.equals(userId)) {
                        Post dontHavePost = new Post("YOU DON'T HAVE POSTS\n", null, "", "dont have posts");
                        posts.add(dontHavePost);
                        postsUser.postValue(posts);
                        return;
                    }
                    if ((newData == null || newData.size() == 0)) {
                        Post notFriendsPost = new Post("MY POSTS AVIABLE ONLY TO MY FRIENDS\n \tBE MY FRIEND :)", null, "", "not friends");
                        posts.add(notFriendsPost);
                        postsUser.postValue(posts);
                        return;
                    }

                    for (Pair<Post, PostInfo> pair : newData) {
                        PostInfo postInfo = pair.second;
                        Post post = pair.first;
                        post.setOwner(userId);

                        dao.insert(post);
                        infoDao.insert(postInfo);
                        //set post info
                        setPostByInfo(post, postInfo);
                        posts.add(post);
                    }
                    postsUser.postValue(posts);
                }).start();
            }

            @Override
            public void onFailure(Call<List<Pair<Post, PostInfo>>> call, Throwable t) {
                posts.add(internetFailed);
                postsUser.setValue(posts);
                t.printStackTrace();
            }
        });
    }

    /**
     * Adds a new post to the server and local database.
     *
     * @param userId The ID of the user creating the post
     * @param post   The post to be added
     */
    public void addPost(String userId, Post post) {
        Call<Post> call = webServicesAPI.createPost(userId, post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                new Thread(() -> {
                    Post newPost = response.body();
                    if (newPost == null)
                        return;
                    PostInfo postInfo = new PostInfo(userId, newPost.get_id(), 0, false, 0);
                    newPost.setOwner(userId);
                    dao.insert(newPost);
                    infoDao.insert(postInfo);
                    postListData.addPost(newPost);
                }).start();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Updates a post on the server and local database.
     *
     * @param userId The ID of the user updating the post
     * @param post   The updated post
     */
    public void updateAllThePost(String userId, Post post,int whereAmI) {
        Call<Void> call = webServicesAPI.updatePostAll(userId, post.get_id(), post);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    dao.update(post);
                    if (whereAmI == FEED)
                        postListData.updatePost(post);
                    else if (whereAmI == USER_SCREEN)
                        postListDataUser.updatePost(post);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Partially updates a post on the server and local database.
     *
     * @param userId The ID of the user updating the post
     * @param post   The updated post with only the fields to be modified
     */
    public void updatePost(String userId, Post post, int whereAmI) {
        Call<Void> call = webServicesAPI.updatePost(userId, post.get_id(), post);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    Post temp = dao.getPost(post.get_id());
                    if (temp == null)
                        return;
                    if (!post.getContent().equals(""))
                        temp.setContent(post.getContent());
                    if (!post.getImg().equals(""))
                        temp.setImg(post.getImg());
                    dao.update(temp);
                    if (whereAmI == FEED)
                        postListData.updatePost(temp);
                    else if (whereAmI == USER_SCREEN)
                        postListDataUser.updatePost(temp);

                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    /**
     * Deletes a post from the server and local database.
     *
     * @param userId The ID of the user deleting the post
     * @param post   The post to be deleted
     */
    public void deletePost(String userId, Post post, int whereAmI) {
        Call<Void> call = webServicesAPI.deletePost(userId, post.get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    dao.deletePostById(post.get_id());
                    infoDao.delete(userId, post.get_id());
                    if (whereAmI == FEED)
                        postListData.removePost(post);
                    else if (whereAmI == USER_SCREEN)
                        postListDataUser.removePost(post);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Adds a like to a post on the server.
     *
     * @param userId The ID of the user adding the like
     * @param postId The ID of the post to like
     */
    public void addLike(String userId, String postId) {
        Call<Void> call = webServicesAPI.addLike(userId, postId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Removes a like from a post on the server.
     *
     * @param userId The ID of the user removing the like
     * @param postId The ID of the post to unlike
     */
    public void removeLike(String userId, String postId) {
        Call<Void> call = webServicesAPI.removeLike(userId, postId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
