package com.example.android.grocerylist.api;

import com.example.android.grocerylist.api.dto.FirebaseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by lapa on 28.07.16.
 */
public interface FirebaseAPIService {

    @POST("user/fcm/token/")
    Call<FirebaseDTO> createFirebaseToken(@Body FirebaseDTO firebaseDTO);

    @DELETE("user/fcm/token/")
    Call<Void> deleteFirebaseToken (@Query("device_id") String deviceId);
}
