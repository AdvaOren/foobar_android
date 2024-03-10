package api;

import android.util.Pair;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import entities.Comment;
import entities.Member;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import repositories.CommentRepo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentAPI {

    private CommentRepo.CommentListData commentListData;

    private final WebServicesAPI webServicesAPI;

    public CommentAPI(CommentRepo.CommentListData commentListData, String token) {
        this.commentListData = commentListData;

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


    public void getAll(String postId) {
        Call<List<Pair<JsonObject, Member>>> call = webServicesAPI.getComments(postId);
        List<Comment> comments = new ArrayList<>();
        call.enqueue(new Callback<List<Pair<JsonObject, Member>>>() {
            @Override
            public void onResponse(Call<List<Pair<JsonObject, Member>>> call, Response<List<Pair<JsonObject, Member>>> response) {
                new Thread(() -> {
                    List<Pair<JsonObject, Member>> newData = response.body();
                    if (newData != null) {
                        for (Pair<JsonObject, Member> pair : newData) {
                            JsonObject json = pair.first;
                            Member member = pair.second;
                            String _id = json.get("_id").getAsString();
                            String text = json.get("text").getAsString();
                            comments.add(new Comment(_id, text, member.get_id(), member.getImgBitmap(), member.getFirstName(),
                                    member.getLastName(), postId));
                        }
                        commentListData.postValue(comments);
                    }
                    else {
                        Comment noComments = new Comment("noComment", "BE THE FIRST TO COMMENT", "", null, "", "", "");
                        comments.add(noComments);
                        commentListData.postValue(comments);
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<List<Pair<JsonObject, Member>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addComment(Comment comment) {
        JsonObject toSend = new JsonObject();
        toSend.addProperty("text",comment.getText());
        Call<JsonObject> call = webServicesAPI.addComment(comment.getUserId(),
                comment.getPostId(), toSend);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                new Thread(() -> {
                    JsonObject json = response.body();
                    if (json != null) {
                        comment.set_id(json.get("_id").getAsString());
                        commentListData.addComment(comment);
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteComment(String userId, String postId, String id) {
        Call<Void> call = webServicesAPI.deleteComment(userId, postId, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    commentListData.removeComment(id);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updateComment(Comment comment) {
        JsonObject toSend = new JsonObject();
        toSend.addProperty("cid",comment.get_id());
        toSend.addProperty("text",comment.getText());
        Call<Void> call = webServicesAPI.updateComment(comment.getUserId(), comment.getPostId(),
                toSend);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    commentListData.updatePost(comment);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public class TempComment {
        private String _id;
        private String text;

        public TempComment(String _id, String text) {
            this._id = _id;
            this.text = text;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
