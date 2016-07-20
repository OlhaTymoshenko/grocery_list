package com.example.android.grocerylist.api;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lapa on 20.07.16.
 */
public class RetrofitGenerator {

    public static final String API_BASE_URL = "http://46.101.241.44:8080/";
    private static HttpLoggingInterceptor httpLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder okHttpClient =
            new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor);
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(new Gson()));

    public static <S> S createService(Class<S> sClass) {
        Retrofit retrofit = builder.client(okHttpClient.build()).build();
        return retrofit.create(sClass);
    }
}
