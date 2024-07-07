package api;


import android.util.Pair;

import com.google.gson.JsonObject;

import java.util.List;

import entities.Friend;
import entities.Member;
import entities.Post;
import entities.PostInfo;
import kotlin.Triple;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Interface for defining API endpoints and their corresponding HTTP methods.
 */
public interface WebServicesAPI {

    @POST("usersList")
    Call<List<Member>> getUsersList(@Body List<String> ids);
    @POST("users")
    Call<String> createUser(@Body Member member);

    @POST("tokens")
    Call<JsonObject> getJWT(@Body JsonObject id);

    @GET("users/{id}")
    Call<Member> getUser(@Path("id") String id);

    @GET("users/{email}")
    Call<Member> getUserByEmail(@Path("email") String email);

    @PUT("users/{id}")
    Call<Void> updateUserAll(@Path("id") String id, @Body Member member);

    @PATCH("users/{id}")
    Call<Void> updateUser(@Path("id") String id, @Body Member member);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);
    @GET("users/{id}/posts")
    Call<List<Pair<Post, PostInfo>>> getPostsOfUser(@Path("id") String id);

    @GET("posts")
    Call<List<Triple<Post, Member, PostInfo>>> getLastPosts();

    @POST("users/{id}/posts")
    Call<Post> createPost(@Path("id") String id,@Body Post post);

    @PUT("users/{id}/posts/{pid}")
    Call<Void> updatePostAll(@Path("id") String id,@Path("pid") String pid,@Body Post post);

    @PUT("users/{id}/posts/{pid}")
    Call<Post> updatePost(@Path("id") String id,@Path("pid") String pid,@Body Post post);

    @DELETE("users/{id}/posts/{pid}")
    Call<Void> deletePost(@Path("id") String id,@Path("pid") String pid);

    @GET("users/{id}/friends")
    Call<List<Friend>> getFriends(@Path("id") String id);
    @GET("users/{id}/friendsAsk")
    Call<List<Friend>> getFriendsAsk(@Path("id") String id);

    @POST("users/{id}/friends")
    Call<Friend> askToBeFriend(@Path("id") String id);

    @PATCH("users/{id}/friends/{fid}")
    Call<Void> acceptFriend(@Path("id") String id,@Path("fid") String fid);

    @DELETE("users/{id}/friends/{fid}")
    Call<Void> deleteFriend(@Path("id") String id,@Path("fid") String fid);

    @GET("users/{id}/friends/{fid}")
    Call<JsonObject> checkIfFriend(@Path("id") String id, @Path("fid") String fid);

    @GET("users/{id}/posts/{pid}/likes")
    Call<List<PostInfo>> getLikes(@Path("id") String id, @Path("pid") String pid);

    @POST("users/{id}/posts/{pid}/like")
    Call<Void> addLike(@Path("id") String id, @Path("pid") String pid);

    @POST("users/{id}/posts/{pid}/like")
    Call<Void> removeLike(@Path("id") String id, @Path("pid") String pid);

    @GET("posts/{pid}/comments")
    Call<List<Pair<JsonObject,Member>>> getComments(@Path("pid") String pid);

    @POST("users/{id}/posts/{pid}/comments")
    Call<JsonObject> addComment(@Path("id") String id,@Path("pid") String pid ,@Body JsonObject text);

    @DELETE("users/{id}/posts/{pid}/comments/{cid}")
    Call<Void> deleteComment(@Path("id") String id,@Path("pid") String pid, @Path("cid") String cid);

    @PUT("users/{id}/posts/{pid}/comments/{cid}")
    Call<Void> updateComment(@Path("id") String id,@Path("pid") String pid, @Path("cid") String cid, @Body JsonObject text);


}
