package ua.com.amicablesoft.android.grocerylist.ui.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lapa on 20.07.16.
 */
public class TokenSaver {

    private Context context;

    public TokenSaver(Context context) {
        this.context = context;
    }

    public void saveToken(String token) {
        SharedPreferences preferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}
