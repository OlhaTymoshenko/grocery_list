package ua.com.amicablesoft.android.grocerylist.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ua.com.amicablesoft.android.grocerylist.R;
import ua.com.amicablesoft.android.grocerylist.api.RetrofitGenerator;
import ua.com.amicablesoft.android.grocerylist.api.SignUpAPIService;
import ua.com.amicablesoft.android.grocerylist.api.dto.SignUpDTO;
import ua.com.amicablesoft.android.grocerylist.ui.common.FirebaseTokenUploader;
import ua.com.amicablesoft.android.grocerylist.ui.common.TokenSaver;
import ua.com.amicablesoft.android.grocerylist.ui.items.MainActivity;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                attemptSignUp();
            }
        });
    }

    /**
     * Attempts to sign up the account specified by the sign up form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual sign up attempt is made.
     */
    private void attemptSignUp() {
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
        final String name = mNameView.getText().toString();
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
            RetrofitGenerator retrofitGenerator = new RetrofitGenerator(getApplicationContext());
            SignUpAPIService service = retrofitGenerator.createService(SignUpAPIService.class);
            Call<String> call = service.signUp(signUpDTO);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String token = response.body();
                        TokenSaver tokenSaver = new TokenSaver(getApplicationContext());
                        tokenSaver.saveToken(token);
                        FirebaseTokenUploader firebaseTokenUploader =
                                new FirebaseTokenUploader(getApplicationContext());
                        firebaseTokenUploader.uploadToken();
                        Log.d("InstanceID token ", FirebaseInstanceId.getInstance().getToken());
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG)
                                .show();
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
}
