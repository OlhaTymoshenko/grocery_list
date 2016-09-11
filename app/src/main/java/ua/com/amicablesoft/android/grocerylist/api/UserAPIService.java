package ua.com.amicablesoft.android.grocerylist.api;

import ua.com.amicablesoft.android.grocerylist.api.dto.UserDTO;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by lapa on 12.04.16.
 */
public interface UserAPIService {

    @GET("user")
    Call<UserDTO> userData();

    @Multipart
    @POST("user/avatar")
    Call<ResponseBody> userAvatar (@Part MultipartBody.Part avatar);
}
