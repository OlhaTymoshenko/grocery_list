package com.example.android.grocerylist;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences("token", MODE_PRIVATE);
        final String token = preferences.getString("token", null);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                assert token != null;
                Request.Builder builder = original.newBuilder()
                        .header("X-AUTH-TOKEN", token)
                        .method(original.method(), original.body());
                Request request = builder.build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.url))
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();
        APIService service = retrofit.create(APIService.class);

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
