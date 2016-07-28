package com.example.android.grocerylist.api;

import com.example.android.grocerylist.api.dto.FirebaseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by lapa on 28.07.16.
 */
public interface FirebaseAPIService {

    @POST("user/fcm/token/")
    Call<FirebaseDTO> createFirebaseToken(@Body FirebaseDTO firebaseDTO);
}
