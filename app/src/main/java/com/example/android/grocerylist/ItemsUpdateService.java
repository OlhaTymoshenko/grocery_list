package com.example.android.grocerylist;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lapa on 12.04.16.
 */
public class ItemsUpdateService extends IntentService {

    public ItemsUpdateService() {
        super("itemsUpdate");
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
        Call<List<TaskDTO>> listCall = service.requestList();
        try {
            Response<List<TaskDTO>> response = listCall.execute();
            if (response.isSuccessful()) {
                List<TaskDTO> taskDTOList = response.body();
                ArrayList<TaskModel> taskModels = new ArrayList<>();
                for (int i = 0; i < taskDTOList.size(); i++) {
                    TaskDTO taskDTO = taskDTOList.get(i);
                    String name = taskDTO.getName();
                    int id = taskDTO.getId();
                    TaskModel model = new TaskModel();
                    model.setItemName(name);
                    model.setRemoteId(id);
                    taskModels.add(model);
                    SqlRepository repository = new SqlRepository(getApplicationContext());
                    repository.updateItems(taskModels);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
