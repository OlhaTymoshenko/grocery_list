package com.example.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.example.android.grocerylist.api.APIService;
import com.example.android.grocerylist.api.RetrofitGenerator;
import com.example.android.grocerylist.ui.items.MainActivity;
import com.example.android.grocerylist.ui.profile.UserPhotoActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by lapa on 28.06.16.
 */
public class FileUploadService extends IntentService {
    public FileUploadService() {
        super("FileUploadService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
        APIService service = retrofitGenerator.createService(APIService.class);
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
