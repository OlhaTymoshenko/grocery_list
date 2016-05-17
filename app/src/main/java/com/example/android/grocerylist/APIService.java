package com.example.android.grocerylist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by lapa on 12.04.16.
 */
public interface APIService {

    @GET("items")
    Call<List<TaskDTO>> requestList();

    @POST("items")
    Call<TaskDTO> createTask(@Body TaskDTO taskDTO);

    @DELETE("items/{id}")
    Call<Void> deleteTask (@Path("id") int id);

    @POST("auth/signin")
    Call<String> signIn (@Body LoginDTO loginDTO);

    @POST("auth/signin-fb")
    Call<String> signInFb (@Body String token);

    @POST("auth/signup")
    Call<String> signUp (@Body SignUpDTO signUpDTO);
}
