package com.example.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.example.android.grocerylist.ui.items.MainActivity;
import com.example.android.grocerylist.R;
import com.example.android.grocerylist.ui.profile.UserPhotoActivity;
import com.example.android.grocerylist.api.APIService;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lapa on 28.06.16.
 */
public class FileUploadService extends IntentService {
    public FileUploadService() {
        super("FileUploadService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences("token", MODE_PRIVATE);
        final String token = preferences.getString("token", null);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder builder = original
                                .newBuilder()
                                .header("X-AUTH-TOKEN", token)
                                .method(original.method(), original.body());
                        Request request = builder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.url))
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();
        APIService service = retrofit.create(APIService.class);
        final File file = (File) intent.getExtras().getSerializable("image");
        assert file != null;
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", file.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), file));
        Call<ResponseBody> imageCall = service.userAvatar(part);
        imageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.v("Upload", "success");
                File storageDir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File image = new File(storageDir, file.getName());
                boolean deleted = image.delete();
                Intent intent1 = new Intent(UserPhotoActivity.BROADCAST_ACTION_2);
                sendBroadcast(intent1);
                Intent intent2 = new Intent(MainActivity.BROADCAST_ACTION_1);
                sendBroadcast(intent2);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "Upload error:", t);
            }
        });

    }
}
