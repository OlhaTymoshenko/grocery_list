package com.example.android.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        assert signUpButton != null;
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(String.valueOf(R.string.url))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(new Gson()))
                        .client(client)
                        .build();
                APIService service = retrofit.create(APIService.class);

                SignUpDTO signUpDTO = new SignUpDTO();
                EditText editName = (EditText) findViewById(R.id.sign_up_name);
                assert editName != null;
                signUpDTO.setName(editName.getText().toString());
                EditText editEmail = (EditText) findViewById(R.id.sign_up_email);
                assert editEmail != null;
                signUpDTO.setEmail(editEmail.getText().toString());
                EditText editPassword = (EditText) findViewById(R.id.sign_up_password);
                assert editPassword != null;
                signUpDTO.setPassword(editPassword.getText().toString());

                Call<String> call = service.signUp(signUpDTO);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            String token = response.body();
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("token", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("token", token);
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                    }
                });
            }
        });
    }
}
