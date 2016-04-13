package com.example.android.grocerylist;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SyncNewService extends IntentService {

    public SyncNewService() {
        super("SyncNewService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.150:8080/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();
        APIService service = retrofit.create(APIService.class);
        SqlRepository repository = new SqlRepository(getApplicationContext());
        ArrayList<TaskModel> taskModels = repository.findNewItems();
        for (TaskModel model : taskModels) {
            TaskDTO taskDTO = new TaskDTO();
            taskDTO.setName(model.getItemName());
            Call<TaskDTO> taskDTOCall = service.createTask(taskDTO);
            try {
                Response<TaskDTO> taskDTOResponse = taskDTOCall.execute();
                if (taskDTOResponse.isSuccessful()) {
                    TaskDTO dto = taskDTOResponse.body();
                    TaskModel taskModel = new TaskModel();
                    taskModel.setItemName(dto.getName());
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
