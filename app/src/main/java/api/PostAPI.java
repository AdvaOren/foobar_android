package api;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import entities.Member;
import entities.Post;
import entities.PostInfo;
import kotlin.Triple;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import repositories.PostDao;
import repositories.PostInfoDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import viewmodels.MemberViewModel;

public class PostAPI {
    private final MutableLiveData<List<Post>> postListData;
    private final PostDao dao;
    private final WebServicesAPI webServicesAPI;

    public PostAPI(MutableLiveData<List<Post>> postListData, PostDao dao, String token) {
        this.postListData = postListData;
        this.dao = dao;

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

    public void getPosts(String userId, int currentPage, List<Post> posts, MemberViewModel mVM, PostInfoDao postInfoDao) {
        Call<List<Triple<Post, Member, PostInfo>>> call = webServicesAPI.getLastPosts(userId, currentPage);
        /*try {
            Response<List<Triple<Post, Member, PostInfo>>> response = call.execute();
            List<Triple<Post, Member, PostInfo>> newData = response.body();

            // Check if there are more pages
            if (newData != null && !newData.isEmpty()) {
                List<Post> posts = new ArrayList<>();
                for (Triple<Post, Member, PostInfo> triple : newData) {
                    PostInfo postInfo = triple.getThird();
                    Post post = triple.getFirst();

                    dao.insert(post);
                    mVM.saveMember(triple.getSecond());
                    postInfoDao.insert(postInfo);
                    //set post info
                    setPostByInfo(post,postInfo);
                    posts.add(post);
                }
                newData.clear();
            }
            if (currentPage == 5)
                return;
            // Increment the page number and fetch the next page
            getPosts(userId, currentPage + 1, mVM,postInfoDao);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        call.enqueue(new Callback<List<Triple<Post, Member, PostInfo>>>() {
            @Override
            public void onResponse(Call<List<Triple<Post, Member, PostInfo>>> call, Response<List<Triple<Post, Member, PostInfo>>> response) {
                new Thread(() -> {
                    List<Triple<Post, Member, PostInfo>> newData = response.body();

                    // Check if there are more pages
                    if (newData != null && !newData.isEmpty()) {
                        for (Triple<Post, Member, PostInfo> triple : newData) {
                            PostInfo postInfo = triple.getThird();
                            Post post = triple.getFirst();
                            post.setOwner(userId);

                            dao.insert(post);
                            mVM.saveMember(triple.getSecond());
                            postInfoDao.insert(postInfo);
                            //set post info
                            setPostByInfo(post, postInfo);
                            posts.add(post);
                        }
                        newData.clear();
                    }
                    if (currentPage == 5) {
                        postListData.postValue(posts);
                        return;
                    }
                    // Increment the page number and fetch the next page
                    getPosts(userId, currentPage + 1, posts, mVM, postInfoDao);

                }).start();
            }

            @Override
            public void onFailure(Call<List<Triple<Post, Member, PostInfo>>> call, Throwable t) {
                //we get less than 20 posts
                postListData.postValue(posts);
            }
        });
    }

    public void getLastPosts(String userId, PostInfoDao postInfoDao, MemberViewModel mVM) {
        new Thread(() -> {
            //get posts from local db
            List<Post> posts = dao.getAll(userId);
            for (Post post : posts) {
                PostInfo postInfo = postInfoDao.getPostInfo(userId, post.get_id());
                setPostByInfo(post,postInfo);
            }
            postListData.postValue(posts);

            //get posts from server
            dao.clear(userId);
            postInfoDao.clear(userId);
            List<Post> posts2 = new ArrayList<>();
            getPosts(userId, 1, posts2, mVM, postInfoDao);
        }).start();
    }

    private void setPostByInfo(Post post, PostInfo postInfo) {
        post.setLikes(postInfo.getLikeAmount());
        post.setLiked(postInfo.isLiked());
        post.setComments(postInfo.getCommentsAmount());
    }

    public void getUserPosts(String userId) {
        /*Call<List<Post>> call = webServicesAPI.getLastPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                new Thread(() -> {
                    dao.deleteUserPosts(userId);
                    dao.insert(response.body());
                    postListData.postValue(dao.getUserPosts(userId));
                }).start();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });*/
    }

    public void addPost(String userId, Post post) {
        webServicesAPI.createPost(userId, post);
        dao.insert(post);
        postListData.postValue(dao.getAll(userId));

    }

    public void updateAllThePost(String userId, Post post) {
        webServicesAPI.updatePostAll(userId, post.get_id(), post);
        dao.update(post);
        postListData.postValue(dao.getAll(userId));
    }

    public void updatePost(String userId, Post post) {
        webServicesAPI.updatePost(userId, post.get_id(), post);
        Post temp = dao.getPost(post.get_id());
        if (!post.getContent().equals(""))
            temp.setContent(post.getContent());
        if (post.getImg() != null)
            temp.setImg(post.getImgBitmap());
        dao.update(temp);
        postListData.postValue(dao.getAll(userId));
    }

    public void deletePost(String userId, String postId) {
        webServicesAPI.deletePost(userId, postId);
        dao.deletePostById(postId);
        postListData.postValue(dao.getAll(userId));
    }

    public void addLike(String userId, String postId) {
        webServicesAPI.addLike(userId, postId);
    }

    public void removeLike(String userId, String postId) {
        webServicesAPI.removeLike(userId, postId);
    }
}
