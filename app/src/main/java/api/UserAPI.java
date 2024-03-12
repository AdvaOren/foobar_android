package api;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;

import java.util.List;

import entities.Member;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import daos.MemberDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API for interacting with user-related data on the server.
 */
public class UserAPI {
    private Retrofit retrofit;
    private WebServicesAPI webServicesAPI;
    private MemberDao dao;

    /**
     * Constructor for the UserAPI class.
     *
     * @param token JWT token used for authentication
     * @param dao   Data Access Object for accessing local database
     */
    public UserAPI(String token, MemberDao dao) {
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

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build()).build();


        webServicesAPI = retrofit.create(WebServicesAPI.class);
    }

    /**
     * Adds a new user to the server and local database.
     *
     * @param member The member object representing the user to be added
     */
    public void addUser(Member member, MutableLiveData<Member> curr) {
        Call<String> call = webServicesAPI.createUser(member);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String id = response.body();
                if (id == null) {
                    member.set_id("exists");
                    curr.setValue(member);
                } else {
                    member.set_id(id);
                    dao.insert(member);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    /**
     * Retrieves the JWT token for the given member from the server.
     *
     * @param member The member object for which to retrieve the JWT token
     * @param jwt    MutableLiveData to hold the JWT token value
     */
    public void getJWT(Member member, MutableLiveData<String> jwt) {
        JsonObject json = new JsonObject();
        json.addProperty("id", member.get_id());
        Call<JsonObject> call = webServicesAPI.getJWT(json);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject re = response.body();
                if (re == null) {
                    jwt.setValue(null);
                    dao.clear();
                    return;
                }
                jwt.setValue(re.get("token").toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Retrieves the member information for the given user ID from the server.
     *
     * @param id      The ID of the user for which to retrieve member information
     * @param current MutableLiveData to hold the current member object
     */
    public void getMember(String id, MutableLiveData<Member> current) {
        Call<Member> call = webServicesAPI.getUser(id);
        new Thread(() -> {
            if (dao.getById(id) != null) {
                current.postValue(null);
                current.postValue(dao.getById(id));
            }
        }).start();
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                current.setValue(null);
                current.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    /**
     * Retrieves the member information by email from the server or local database if available.
     *
     * @param email   The email of the member to retrieve
     * @param current MutableLiveData to hold the current member object
     */
    public void getMemberByEmail(String email, MutableLiveData<Member> current) {
        Call<Member> call = webServicesAPI.getUserByEmail(email);
        new Thread(() -> {
            Member temp = dao.getByEmail(email);
            if (temp != null)
                current.postValue(temp);
        }).start();
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                new Thread(() -> {
                    Member temp = response.body();
                    if (temp == null) {
                        current.postValue(null);
                        return;
                    }
                    if (dao.getByEmail(email) != null)
                        dao.update(temp);
                    else
                        dao.insert(temp);
                    current.postValue(dao.getByEmail(email));
                }).start();
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    /**
     * Updates the member information on the server and local database.
     *
     * @param member The member object to be updated
     */
    public void updateMember(Member member) {
        Call<Void> call = webServicesAPI.updateUser(member.get_id(), member);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    dao.update(member);
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Deletes the member from the server and local database.
     *
     * @param toDelete The member object to be deleted
     */
    public void deleteMember(Member toDelete) {
        Call<Void> call = webServicesAPI.deleteUser(toDelete.get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> dao.delete(toDelete)).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Retrieves a list of members by their IDs from the server.
     *
     * @param ids List of member IDs to retrieve
     */
    public void getMembers(List<String> ids) {
        Call<List<Member>> call = webServicesAPI.getUsersList(ids);
        call.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                dao.insert(response.body());
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {
            }
        });
    }

}
