package com.example.android.grocerylist.api;

import com.example.android.grocerylist.api.dto.SignUpDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by lapa on 21.07.16.
 */
public interface SignUpAPIService {

    @POST("auth/signup")
    Call<String> signUp (@Body SignUpDTO signUpDTO);
}
