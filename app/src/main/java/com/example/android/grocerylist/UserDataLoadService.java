package com.example.android.grocerylist;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lapa on 20.05.16.
 */
public class UserDataLoadService extends IntentService {

    public UserDataLoadService() {
        super("UserDataLoadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences("token", MODE_PRIVATE);
        final String token = preferences.getString("token", null);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original
                        .newBuilder()
                        .header("X-AUTH-TOKEN", token)
                        .method(original.method(), original.body());
                Request request = builder.build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(getString(R.string.url))
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client).build();
        APIService service = retrofit.create(APIService.class);
        Call<UserDTO> call = service.userData();
        try {
            retrofit2.Response<UserDTO> response = call.execute();
            if (response.isSuccessful()) {
                UserDTO userDTO = response.body();
                UserModel userModel = new UserModel();
                userModel.setUserName(userDTO.getName());
                userModel.setUserEmail(userDTO.getEmail());
                SqlRepository repository = new SqlRepository(getApplicationContext());
                repository.updateUserData(userModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
