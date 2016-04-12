package com.example.android.grocerylist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by lapa on 12.04.16.
 */
public interface APIService {

    @Headers("Authorization: Basic dXNlcjE6c2VjcmV0MQ==")
    @GET("items")
    Call<List<TaskDTO>> requestList();
}
