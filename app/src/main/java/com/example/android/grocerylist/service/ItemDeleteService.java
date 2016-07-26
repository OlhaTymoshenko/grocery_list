package com.example.android.grocerylist.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.grocerylist.dal.SqlRepository;

/**
 * Created by lapa on 25.07.16.
 */
public class ItemDeleteService extends IntentService {

    public ItemDeleteService() {
        super("itemDelete");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Integer remoteId = intent.getExtras().getInt("remoteId");
        SqlRepository sqlRepository = new SqlRepository(getApplicationContext());
        sqlRepository.deleteItemByRemoteId(remoteId);
    }
}
