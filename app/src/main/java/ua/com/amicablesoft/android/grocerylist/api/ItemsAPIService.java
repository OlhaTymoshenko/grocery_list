package ua.com.amicablesoft.android.grocerylist.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ua.com.amicablesoft.android.grocerylist.api.dto.TaskDTO;

/**
 * Created by lapa on 21.07.16.
 */
public interface ItemsAPIService {

    @GET("items")
    Call<List<TaskDTO>> requestList();

    @POST("items")
    Call<TaskDTO> createTask(@Body TaskDTO taskDTO);

    @DELETE("items/{id}")
    Call<Void> deleteTask (@Path("id") int id);
}
