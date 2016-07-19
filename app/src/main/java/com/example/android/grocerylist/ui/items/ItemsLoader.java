package com.example.android.grocerylist.ui.items;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.grocerylist.dal.SqlRepository;
import com.example.android.grocerylist.model.TaskModel;
import com.example.android.grocerylist.service.ItemsUpdatedEvent;

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
        return repository.findItems();
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
