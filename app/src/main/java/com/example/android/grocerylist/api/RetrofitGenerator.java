package com.example.android.grocerylist.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lapa on 20.07.16.
 */
public class RetrofitGenerator {

    private final String token;
    private final String deviceId;
    public static final String API_BASE_URL = "http://46.101.241.44:8080/";
    private static HttpLoggingInterceptor httpLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(new Gson()));

    public RetrofitGenerator(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        token = preferences.getString("token", null);
        deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient okHttpClient;
        if (token == null) {
            okHttpClient =
                    new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        } else {
            okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder builder = original
                                    .newBuilder()
                                    .header("X-AUTH-TOKEN", token)
                                    .header("Device-Id", deviceId)
                                    .method(original.method(), original.body());
                            Request request = builder.build();
                            return chain.proceed(request);
                        }
                    }).build();
        }
        return okHttpClient;
    }

    public <S> S createService(Class<S> sClass) {
        OkHttpClient okHttpClient = createOkHttpClient();
        Retrofit retrofit = builder.client(okHttpClient).build();
        return retrofit.create(sClass);
    }
}
