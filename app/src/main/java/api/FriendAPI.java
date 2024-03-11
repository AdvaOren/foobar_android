package api;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import entities.Friend;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendAPI {
    private WebServicesAPI webServicesAPI;
    private MutableLiveData<List<Friend>> friends;
    private MutableLiveData<String> btnText;

    public FriendAPI(String token, MutableLiveData<List<Friend>> friends, MutableLiveData<String> btnText) {
        this.friends = friends;
        this.btnText = btnText;

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

    public void getAsk(String requested) {
        Call<List<Friend>> call = webServicesAPI.getFriendsAsk(requested);
        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                friends.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void acceptFriend(Friend friend) {
        Call<Void> call = webServicesAPI.acceptFriend(friend.getRequested(), friend.getRequester());
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

    public void rejectFriend(Friend friend) {
        Call<Void> call = webServicesAPI.deleteFriend(friend.getRequested(), friend.getRequester());
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

    public void btnText(String requested, String requester) {
        Call<JsonObject> call = webServicesAPI.checkIfFriend(requester, requested);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject json = response.body();
                if (json == null || json.get("status") == null) {
                    btnText.setValue(Friend.NOT_FRIENDS);
                    return;
                }
                String status = json.get("status").getAsString();
                switch (status) {
                    case "wait":
                        btnText.setValue(Friend.REQUEST_SENT);
                        break;
                    case "approve":
                        btnText.setValue(Friend.FRIENDS);
                        break;
                    case "sent":
                        btnText.setValue(Friend.REQUEST_SENT_HIS_SIDE);
                        break;
                    default:
                        btnText.setValue(Friend.NOT_FRIENDS);
                        break;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void askToBeFriend(String requester, String requested) {
        Call<Friend> call = webServicesAPI.askToBeFriend(requested);
        call.enqueue(new Callback<Friend>() {
            @Override
            public void onResponse(Call<Friend> call, Response<Friend> response) {
                btnText.setValue(Friend.REQUEST_SENT);
            }

            @Override
            public void onFailure(Call<Friend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getFriends(String userId, MutableLiveData<List<Friend>> myFriends,String currUserId) {
        Call<List<Friend>> call = webServicesAPI.getFriends(userId);

        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                new Thread(() -> {
                    List<Friend> data = response.body();
                    if (data == null || data.size() == 0 && userId.equals(currUserId)) {
                        Friend friend = new Friend("", "", "", "YOU DONT HAVE FRIENDS YET", "");
                        List<Friend> temp = new ArrayList<>();
                        temp.add(friend);
                        myFriends.postValue(temp);
                        return;
                    }
                    if (data.size() == 0) {
                        Friend friend = new Friend("", "", "", "ONLY MY FRIENDS CAN SEE MY FRIENDS\n BE MY FRIEND", "");
                        List<Friend> temp = new ArrayList<>();
                        temp.add(friend);
                        myFriends.postValue(temp);
                        return;
                    }
                    myFriends.postValue(data);
                }).start();
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
