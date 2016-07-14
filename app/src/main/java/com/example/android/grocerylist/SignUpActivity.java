package com.example.android.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private String email;
    private String password;
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("Extra_email");
        password = extras.getString("Extra_password");
        mEmailView = (TextInputEditText) findViewById(R.id.sign_up_email);
        assert mEmailView != null;
        mEmailView.setText(email);
        mPasswordView = (TextInputEditText) findViewById(R.id.sign_up_password);
        assert mPasswordView != null;
        mPasswordView.setText(password);

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        assert signUpButton != null;
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });
    }

    /**
     * Attempts to sign up the account specified by the sign up form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual sign up attempt is made.
     */
    private void attemptSignIn() {
        // Reset errors.

        TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.name_layout);
        assert nameLayout != null;
        nameLayout.setError(null);
        TextInputLayout emailLayout = (TextInputLayout) findViewById(R.id.email_layout);
        assert emailLayout != null;
        emailLayout.setError(null);
        TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
        assert passwordLayout != null;
        passwordLayout.setError(null);

        // Store values at the time of the sign up attempt.
        TextInputEditText mNameView = (TextInputEditText) findViewById(R.id.sign_up_name);
        assert mNameView != null;
        String name = mNameView.getText().toString();
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordLayout.setError(getString(R.string.error_invalid_password));
            focusView = passwordLayout;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.error_field_required));
            focusView = emailLayout;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailLayout.setError(getString(R.string.error_invalid_email));
            focusView = emailLayout;
            cancel = true;
        }

        // Check for a valid user's name.
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.error_field_required));
            focusView = nameLayout;
            cancel = true;
        } else if (!isNameValid(name)) {
            nameLayout.setError(getString(R.string.error_invalid_name));
            focusView = nameLayout;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            SignUpDTO signUpDTO = new SignUpDTO();
            signUpDTO.setName(name);
            signUpDTO.setEmail(email);
            signUpDTO.setPassword(password);
            APIService service = buildRequest();
            Call<String> call = service.signUp(signUpDTO);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String token = response.body();
                        SharedPreferences preferences = getApplicationContext()
                                .getSharedPreferences("token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", token);
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
        }
    }

    private boolean isNameValid(String name) {
        return name.length() > 1;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private APIService buildRequest() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();
        return retrofit.create(APIService.class);
    }
}
