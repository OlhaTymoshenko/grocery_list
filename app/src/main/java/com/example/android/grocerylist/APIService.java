package com.example.android.grocerylist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by lapa on 12.04.16.
 */
public interface APIService {

    @Headers("Authorization: Basic dXNlcjE6c2VjcmV0MQ==")
    @GET("items")
    Call<List<TaskDTO>> requestList();

    @Headers("Authorization: Basic dXNlcjE6c2VjcmV0MQ==")
    @POST("items")
    Call<TaskDTO> createTask(@Body TaskDTO taskDTO);

    @Headers("Authorization: Basic dXNlcjE6c2VjcmV0MQ==")
    @DELETE("items/{id}")
    Call<TaskDTO> deleteTask (@Path("id") int id);
}
