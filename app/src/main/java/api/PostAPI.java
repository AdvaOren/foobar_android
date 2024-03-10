package api;

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

public class PostAPI {
    private final PostRepo.PostListData postListData;
    private final PostDao dao;
    private final WebServicesAPI webServicesAPI;
    private final PostInfoDao infoDao;
    private final Post internetFailed;

    public PostAPI(PostRepo.PostListData postListData, PostDao dao, PostInfoDao infoDao, String token) {
        this.postListData = postListData;
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

    private void getPosts(String userId, MemberViewModel mVM) {
        Call<List<Triple<Post, Member, PostInfo>>> call = webServicesAPI.getLastPosts();
        List<Post> posts = new ArrayList<>();
        call.enqueue(new Callback<List<Triple<Post, Member, PostInfo>>>() {
            @Override
            public void onResponse(Call<List<Triple<Post, Member, PostInfo>>> call, Response<List<Triple<Post, Member, PostInfo>>> response) {
                new Thread(() -> {
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

    public void getLastPosts(String userId, MemberViewModel mVM) {
        new Thread(() -> {
            //get posts from local db
            /*List<Post> posts = dao.getAll(userId);
            for (Post post : posts) {
                PostInfo postInfo = infoDao.getPostInfo(userId, post.get_id());
                setPostByInfo(post,postInfo);
            }
            postListData.postValue(posts);*/

            //get posts from server
            dao.clear(userId);
            infoDao.clear(userId);
            getPosts(userId, mVM);
        }).start();
    }

    private void setPostByInfo(Post post, PostInfo postInfo) {
        post.setLikes(postInfo.getLikeAmount());
        post.setLiked(postInfo.isLiked());
        post.setComments(postInfo.getCommentsAmount());
    }


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

    public void updateAllThePost(String userId, Post post) {
        Call<Void> call = webServicesAPI.updatePostAll(userId, post.get_id(), post);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    dao.update(post);
                    postListData.updatePost(post);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updatePost(String userId, Post post) {
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
                    postListData.updatePost(temp);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void deletePost(String userId, Post post) {
        Call<Void> call = webServicesAPI.deletePost(userId, post.get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    dao.deletePostById(post.get_id());
                    infoDao.delete(userId, post.get_id());
                    postListData.removePost(post);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

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
