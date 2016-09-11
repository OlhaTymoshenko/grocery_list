package ua.com.amicablesoft.android.grocerylist.api;

import ua.com.amicablesoft.android.grocerylist.api.dto.LoginDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by lapa on 21.07.16.
 */
public interface SignInAPIService {

    @POST("auth/signin")
    Call<String> signIn (@Body LoginDTO loginDTO);

    @POST("auth/signin-fb")
    Call<String> signInFb (@Body String token);
}
