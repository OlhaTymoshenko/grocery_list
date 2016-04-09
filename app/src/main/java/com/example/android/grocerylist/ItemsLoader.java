package com.example.android.grocerylist;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by lapa on 06.04.16.
 */
public class ItemsLoader extends AsyncTaskLoader<ArrayList<TaskModel>> {

    public ItemsLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<TaskModel> loadInBackground() {
        SqlRepository repository = new SqlRepository(getContext());
        ArrayList<TaskModel> items = repository.findItems();
        return items;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStopLoading() {
        EventBus.getDefault().unregister(this);
        super.onStopLoading();
    }

    @Subscribe
    public void onItemsUpdatedEvent(ItemsUpdatedEvent event) {
        forceLoad();
    }
}
