package com.example.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.grocerylist.api.APIService;
import com.example.android.grocerylist.api.RetrofitGenerator;
import com.example.android.grocerylist.api.dto.TaskDTO;
import com.example.android.grocerylist.dal.SqlRepository;
import com.example.android.grocerylist.model.TaskModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lapa on 12.04.16.
 */
public class ItemsUpdateService extends IntentService {

    public ItemsUpdateService() {
        super("itemsUpdate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
        APIService service = retrofitGenerator.createService(APIService.class);
        Call<List<TaskDTO>> listCall = service.requestList();
        try {
            Response<List<TaskDTO>> response = listCall.execute();
            if (response.isSuccessful()) {
                List<TaskDTO> taskDTOList = response.body();
                ArrayList<TaskModel> taskModels = new ArrayList<>();
                for (TaskDTO taskDTO : taskDTOList) {
                    String name = taskDTO.getTitle();
                    int id = taskDTO.getId();
                    TaskModel model = new TaskModel();
                    model.setItemName(name);
                    model.setRemoteId(id);
                    taskModels.add(model);
                }
                SqlRepository repository = new SqlRepository(getApplicationContext());
                repository.updateItems(taskModels);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
