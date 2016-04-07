package com.example.android.grocerylist;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by lapa on 06.04.16.
 */
public class ItemsLoader extends AsyncTaskLoader<ArrayList<String>> {

    public ItemsLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<String> loadInBackground() {
        SqlRepository repository = new SqlRepository(getContext());
        ArrayList<String> items = repository.findItems();
        return items;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
