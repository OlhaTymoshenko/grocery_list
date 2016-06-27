package com.example.android.grocerylist;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserPhotoActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 0;
    private String currentPhotoPath;
    private String currentPhotoName;
    private File image;
    static final int MY_PERMISSIONS_REQUEST = 1;
    static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photo);
        TextView textViewTakePicture = (TextView) findViewById(R.id.take_picture_text_view);
        assert textViewTakePicture != null;
        textViewTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    takePicture();
                } else {
                    ActivityCompat.requestPermissions(UserPhotoActivity.this,
                            new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
                }
            }
        });
        TextView textViewSelectPicture = (TextView) findViewById(R.id.select_picture_text_view);
        assert textViewSelectPicture != null;
        textViewSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });
        TextView textViewDone = (TextView) findViewById(R.id.done_text_view);
        assert textViewDone != null;
        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPicture();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                ImageView imageView = (ImageView) findViewById(R.id.user_photo);
                assert imageView != null;
                imageView.setImageBitmap(bitmap);
            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ImageView imageView = (ImageView) findViewById(R.id.user_photo);
                    assert imageView != null;
                    imageView.setImageBitmap(bitmap);
                    try {
                        File photo = createImageFile();
                        OutputStream outputStream = new FileOutputStream(photo);
                        byte[] buf = new byte[1024];
                        int len;
                        assert inputStream != null;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePicture();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void selectPicture() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectPictureIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(selectPictureIntent, "Select picture"), PICK_IMAGE);
    }

    private void postPicture() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("token", MODE_PRIVATE);
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
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", currentPhotoName,
                RequestBody.create(MediaType.parse("multipart/form-data"), image));
        Call<Image> imageCall = service.userAvatar(part);
        imageCall.enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, retrofit2.Response<Image> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<Image> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        currentPhotoName = image.getName();
        return image;
    }
}
