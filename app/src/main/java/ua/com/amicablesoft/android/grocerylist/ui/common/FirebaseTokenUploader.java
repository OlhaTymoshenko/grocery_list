package ua.com.amicablesoft.android.grocerylist.ui.common;

import android.content.Context;
import android.provider.Settings;

import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.com.amicablesoft.android.grocerylist.api.FirebaseAPIService;
import ua.com.amicablesoft.android.grocerylist.api.RetrofitGenerator;
import ua.com.amicablesoft.android.grocerylist.api.dto.FirebaseDTO;

/**
 * Created by lapa on 28.07.16.
 */
public class FirebaseTokenUploader {

    private Context context;

    public FirebaseTokenUploader(Context context) {
        this.context = context;
    }

    public void uploadToken() {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseToken != null) {
            FirebaseDTO firebaseDTO = new FirebaseDTO();
            firebaseDTO.setDeviceId(deviceId);
            firebaseDTO.setToken(firebaseToken);
            RetrofitGenerator generator = new RetrofitGenerator(context);
            FirebaseAPIService apiService = generator.createService(FirebaseAPIService.class);
            Call<FirebaseDTO> call = apiService.createFirebaseToken(firebaseDTO);
            call.enqueue(new Callback<FirebaseDTO>() {
                @Override
                public void onResponse(Call<FirebaseDTO> call, Response<FirebaseDTO> response) {
                    if (response.isSuccessful()) {
                        FirebaseDTO dto = response.body();
                    }
                }

                @Override
                public void onFailure(Call<FirebaseDTO> call, Throwable t) {

                }
            });
        }
    }

    public void deleteToken() {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        RetrofitGenerator retrofitGenerator = new RetrofitGenerator(context);
        FirebaseAPIService apiService = retrofitGenerator.createService(FirebaseAPIService.class);
        Call<Void> voidCall = apiService.deleteFirebaseToken(deviceId);
        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
