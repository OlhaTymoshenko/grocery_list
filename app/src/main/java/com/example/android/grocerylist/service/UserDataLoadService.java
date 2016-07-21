package com.example.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.grocerylist.api.UserAPIService;
import com.example.android.grocerylist.api.RetrofitGenerator;
import com.example.android.grocerylist.api.dto.UserDTO;
import com.example.android.grocerylist.dal.SqlRepository;
import com.example.android.grocerylist.model.UserModel;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by lapa on 20.05.16.
 */
public class UserDataLoadService extends IntentService {

    public UserDataLoadService() {
        super("UserDataLoadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
        UserAPIService service = retrofitGenerator.createService(UserAPIService.class);
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
