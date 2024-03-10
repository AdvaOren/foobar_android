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

public class UserAPI {
    private Retrofit retrofit;
    private WebServicesAPI webServicesAPI;
    private MemberDao dao;

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

    public void addUser(Member member) {
        Call<String> call = webServicesAPI.createUser(member);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                member.set_id(response.body());
                dao.insert(member);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void getJWT(Member member, MutableLiveData<String> jwt) {
        JsonObject json = new JsonObject();
        json.addProperty("id",member.get_id());
        Call<JsonObject> call = webServicesAPI.getJWT(json);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject re= response.body();
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

    public void updateMemberAll(Member member) {
        webServicesAPI.updateUserAll(member.get_id(), member);
    }

    public void updateMember(Member member) {
        webServicesAPI.updateUser(member.get_id(), member);
    }

    public void deleteMember(String id) {
        webServicesAPI.deleteUser(id);
    }

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
