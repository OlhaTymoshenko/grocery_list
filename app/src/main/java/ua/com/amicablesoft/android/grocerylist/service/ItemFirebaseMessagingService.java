package ua.com.amicablesoft.android.grocerylist.service;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by lapa on 19.07.16.
 */
public class ItemFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FirebaseMessage: ", remoteMessage.getData().toString());
        if (remoteMessage.getData().get("version").equals("1")) {
            if (remoteMessage.getData().get("action").equals("item_new")) {
                Intent intent = new Intent(this, ItemsUpdateService.class);
                startService(intent);
            } else if (remoteMessage.getData().get("action").equals("item_remove")) {
                String itemId = remoteMessage.getData().get("item_id");
                Integer remoteId = Integer.parseInt(itemId);
                Intent intent = new Intent(this, ItemDeleteService.class);
                intent.putExtra("remoteId", remoteId);
                startService(intent);
            }
        }
        super.onMessageReceived(remoteMessage);
    }
}
