package com.example.android.grocerylist;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class UserPhotoActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 0;
    private String currentPhotoPath;
    private File image;
    static final int MY_PERMISSIONS_REQUEST = 1;
    static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photo);
        ImageView imageView = (ImageView) findViewById(R.id.user_photo);
        Picasso picasso = getPicture();
        picasso.load(getString(R.string.picasso_url)).into(imageView);
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
                if (image != null) {
                    Intent serviceIntent = new Intent(UserPhotoActivity.this, FileUploadService.class);
                    serviceIntent.putExtra("image", image);
                    startService(serviceIntent);
                }
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
                    assert inputStream != null;
                    inputStream.close();
                    ImageView imageView = (ImageView) findViewById(R.id.user_photo);
                    assert imageView != null;
                    imageView.setImageBitmap(bitmap);
                    try {
                        File photo = createImageFile();
                        OutputStream outputStream = new FileOutputStream(photo);
                        byte[] buf = new byte[1024*1024];
                        int len;
                        inputStream = getContentResolver().openInputStream(uri);
                        assert inputStream != null;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
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

    private Picasso getPicture() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("token", MODE_PRIVATE);
        final String token = preferences.getString("token", null);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-AUTH-TOKEN", token)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();

        return new Picasso.Builder(getApplicationContext())
                .downloader(new OkHttp3Downloader(client))
                .loggingEnabled(true)
                .build();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
