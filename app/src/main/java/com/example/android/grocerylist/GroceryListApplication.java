package com.example.android.grocerylist;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by lapa on 06.06.16.
 */
public class GroceryListApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
