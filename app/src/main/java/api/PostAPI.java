package api;

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

    public PostAPI(PostRepo.PostListData postListData, PostDao dao, PostInfoDao infoDao, String token) {
        this.postListData = postListData;
        this.dao = dao;
        this.infoDao = infoDao;

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

    public void getPosts(String userId, int currentPage, List<Post> posts, MemberViewModel mVM) {
        Call<List<Triple<Post, Member, PostInfo>>> call = webServicesAPI.getLastPosts(userId, currentPage);
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
                            infoDao.insert(postInfo);
                            //set post info
                            setPostByInfo(post, postInfo);
                            posts.add(post);
                        }
                        newData.clear();
                    } else {
                        //get less than 20 posts
                        postListData.postValue(posts);
                        return;
                    }
                    if (currentPage == 5) {
                        postListData.postValue(posts);
                        return;
                    }
                    // Increment the page number and fetch the next page
                    getPosts(userId, currentPage + 1, posts, mVM);

                }).start();
            }

            @Override
            public void onFailure(Call<List<Triple<Post, Member, PostInfo>>> call, Throwable t) {
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
            List<Post> posts2 = new ArrayList<>();
            getPosts(userId, 1, posts2, mVM);
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
