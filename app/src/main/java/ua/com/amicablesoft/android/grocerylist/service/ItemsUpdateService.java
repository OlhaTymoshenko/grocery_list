package ua.com.amicablesoft.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;

import ua.com.amicablesoft.android.grocerylist.api.ItemsAPIService;
import ua.com.amicablesoft.android.grocerylist.api.RetrofitGenerator;
import ua.com.amicablesoft.android.grocerylist.api.dto.TaskDTO;
import ua.com.amicablesoft.android.grocerylist.dal.SqlRepository;
import ua.com.amicablesoft.android.grocerylist.model.TaskModel;

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
        ItemsAPIService service = retrofitGenerator.createService(ItemsAPIService.class);
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
