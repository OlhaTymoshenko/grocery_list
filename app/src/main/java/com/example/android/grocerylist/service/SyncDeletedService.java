package com.example.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.grocerylist.api.APIService;
import com.example.android.grocerylist.api.RetrofitGenerator;
import com.example.android.grocerylist.dal.SqlRepository;
import com.example.android.grocerylist.model.TaskModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;


public class SyncDeletedService extends IntentService {

    public SyncDeletedService() {
        super("SyncDeletedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
        APIService service = retrofitGenerator.createService(APIService.class);
        SqlRepository repository = new SqlRepository(getApplicationContext());

        ArrayList<TaskModel> taskModels = repository.findDeletedItems();
        for (TaskModel model : taskModels) {
            int id = model.getRemoteId();
            Call<Void> call = service.deleteTask(id);
            try {
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    SqlRepository sqlRepository = new SqlRepository(getApplicationContext());
                    sqlRepository.setDeletedSynced(model.getItemId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
