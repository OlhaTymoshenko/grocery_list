package com.example.android.grocerylist.ui.signin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.grocerylist.R;
import com.example.android.grocerylist.api.RetrofitGenerator;
import com.example.android.grocerylist.api.SignInAPIService;
import com.example.android.grocerylist.api.dto.LoginDTO;
import com.example.android.grocerylist.ui.common.TokenSaver;
import com.example.android.grocerylist.ui.items.MainActivity;
import com.example.android.grocerylist.ui.signup.SignUpActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;

    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private View mProgressView;
    private View mLoginFormView;
    private String email;
    private String password;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat
                .getColor(getApplicationContext(), R.color.colorPrimaryDark));
        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences("token", MODE_PRIVATE);
        if (preferences.contains("token")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            callbackManager = CallbackManager.Factory.create();
            setContentView(R.layout.activity_login);
            // Set up the login form.
            mEmailView = (TextInputEditText) findViewById(R.id.email);
            mPasswordView = (TextInputEditText) findViewById(R.id.password);
            emailLayout = (TextInputLayout) findViewById(R.id.email_layout);
            passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });
            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            assert mEmailSignInButton != null;
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);

            final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            assert loginButton != null;
            loginButton.setReadPermissions("email");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
                    SignInAPIService service = retrofitGenerator.createService(SignInAPIService.class);
                    Call<String> call = service.signInFb(loginResult.getAccessToken().getToken());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                String token = response.body();
                                TokenSaver tokenSaver = new TokenSaver(getApplicationContext());
                                tokenSaver.saveToken(token);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                        }
                    });
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });

            Button signUpButton = (Button) findViewById(R.id.email_sign_up_button);
            assert signUpButton != null;
            signUpButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    Bundle extras = new Bundle();
                    saveEmail();
                    extras.putString("Extra_email", email);
                    savePassword();
                    extras.putString("Extra_password", password);
                    intent.putExtras(extras);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Store values at the time of the login attempt.
        saveEmail();
        savePassword();

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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail(email);
            loginDTO.setPassword(password);
            RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
            SignInAPIService service = retrofitGenerator.createService(SignInAPIService.class);
            Call<String> call = service.signIn(loginDTO);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String token = response.body();
                        TokenSaver tokenSaver = new TokenSaver(getApplicationContext());
                        tokenSaver.saveToken(token);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong email or password", Toast
                                .LENGTH_LONG).show();
                        showProgress(false);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private String saveEmail() {
        email = mEmailView.getText().toString();
        return email;
    }

    private String savePassword() {
        password = mPasswordView.getText().toString();
        return password;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

