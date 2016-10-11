package ua.com.amicablesoft.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import ua.com.amicablesoft.android.grocerylist.api.ItemsAPIService;
import ua.com.amicablesoft.android.grocerylist.api.RetrofitGenerator;
import ua.com.amicablesoft.android.grocerylist.dal.SqlRepository;
import ua.com.amicablesoft.android.grocerylist.model.TaskModel;


public class SyncDeletedService extends IntentService {

    public SyncDeletedService() {
        super("SyncDeletedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
        ItemsAPIService service = retrofitGenerator.createService(ItemsAPIService.class);
        SqlRepository repository = new SqlRepository(getApplicationContext());

        ArrayList<TaskModel> taskModels = repository.findDeletedItems();
        for (TaskModel model : taskModels) {
            Integer id = model.getRemoteId();
            if (id != null) {
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
}
