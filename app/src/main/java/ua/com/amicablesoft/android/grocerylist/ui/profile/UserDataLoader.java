package ua.com.amicablesoft.android.grocerylist.ui.profile;

import android.content.AsyncTaskLoader;
import android.content.Context;

import ua.com.amicablesoft.android.grocerylist.dal.SqlRepository;
import ua.com.amicablesoft.android.grocerylist.service.UserDataUpdatedEvent;
import ua.com.amicablesoft.android.grocerylist.model.UserModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lapa on 20.05.16.
 */
public class UserDataLoader extends AsyncTaskLoader<UserModel> {
    public UserDataLoader(Context context) {
        super(context);
    }

    @Override
    public UserModel loadInBackground() {
        SqlRepository repository = new SqlRepository(getContext());
        return repository.findUserData();
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
    public void onUserDataUpdatedEvent (UserDataUpdatedEvent userDataUpdatedEvent) {
        forceLoad();
    }
}
