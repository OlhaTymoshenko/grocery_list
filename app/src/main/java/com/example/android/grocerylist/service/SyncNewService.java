package com.example.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.grocerylist.api.ItemsAPIService;
import com.example.android.grocerylist.api.RetrofitGenerator;
import com.example.android.grocerylist.api.dto.TaskDTO;
import com.example.android.grocerylist.dal.SqlRepository;
import com.example.android.grocerylist.model.TaskModel;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;


public class SyncNewService extends IntentService {

    public SyncNewService() {
        super("SyncNewService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
        ItemsAPIService service = retrofitGenerator.createService(ItemsAPIService.class);
        SqlRepository repository = new SqlRepository(getApplicationContext());
        ArrayList<TaskModel> taskModels = repository.findNewItems();
        for (TaskModel model : taskModels) {
            TaskDTO taskDTO = new TaskDTO();
            taskDTO.setTitle(model.getItemName());
            Call<TaskDTO> taskDTOCall = service.createTask(taskDTO);
            try {
                Response<TaskDTO> taskDTOResponse = taskDTOCall.execute();
                if (taskDTOResponse.isSuccessful()) {
                    TaskDTO dto = taskDTOResponse.body();
                    TaskModel taskModel = new TaskModel();
                    taskModel.setItemName(dto.getTitle());
                    taskModel.setRemoteId(dto.getId());
                    taskModel.setItemId(model.getItemId());
                    SqlRepository sqlRepository = new SqlRepository(getApplicationContext());
                    sqlRepository.setNewSynced(taskModel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
