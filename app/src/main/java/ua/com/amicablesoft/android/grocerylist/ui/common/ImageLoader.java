package ua.com.amicablesoft.android.grocerylist.ui.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by lapa on 14.07.16.
 */
public class ImageLoader {
    private Picasso picasso;

    public ImageLoader(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
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

        picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .loggingEnabled(true)
                .build();
    }

    public void loadImage(String url, int width, int height, ImageView imageView) {
       picasso.load(url)
               .resize(width, height)
               .into(imageView);
    }

    public void loadImageCenterCrop(String url, int width, int height, ImageView imageView) {
        picasso.load(url)
                .resize(width, height)
                .centerCrop()
                .into(imageView);
    }
}
