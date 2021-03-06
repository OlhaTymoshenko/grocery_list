package ua.com.amicablesoft.android.grocerylist.ui.items;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.com.amicablesoft.android.grocerylist.R;
import ua.com.amicablesoft.android.grocerylist.dal.SqlRepository;
import ua.com.amicablesoft.android.grocerylist.model.TaskModel;
import ua.com.amicablesoft.android.grocerylist.model.UserModel;
import ua.com.amicablesoft.android.grocerylist.service.ItemsUpdateService;
import ua.com.amicablesoft.android.grocerylist.service.UserDataLoadService;
import ua.com.amicablesoft.android.grocerylist.ui.common.FirebaseTokenUploader;
import ua.com.amicablesoft.android.grocerylist.ui.common.ImageLoader;
import ua.com.amicablesoft.android.grocerylist.ui.profile.UserDataLoader;
import ua.com.amicablesoft.android.grocerylist.ui.profile.UserPhotoActivity;
import ua.com.amicablesoft.android.grocerylist.ui.signin.LoginActivity;

public class MainActivity extends AppCompatActivity
        implements ItemDialogFragment.ItemDialogListener {
    private ItemAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CircleImageView circleImageView;
    private BroadcastReceiver broadcastReceiver;
    private static final int ARRAY_LIST_LOADER_ID = 1;
    private static final int USER_MODEL_LOADER_ID = 2;
    public final static String BROADCAST_ACTION_1 = "photo update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);

        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemDialogFragment().show(getFragmentManager(), "dialog");
            }
        });

        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
        LinearLayout container = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.profile_image_container);
        container.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        circleImageView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        reloadAvatar();
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserPhotoActivity.class);
                startActivity(intent);
            }
        });
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        assert refreshLayout != null;
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                        Log.i("LOG_TAG", "onRefresh called from SwipeRefreshLayout");
                        Intent intent = new Intent(MainActivity.this, ItemsUpdateService.class);
                        startService(intent);
                    }
                }
        );

        Intent serviceIntent = new Intent(this, UserDataLoadService.class);
        startService(serviceIntent);

        getLoaderManager().initLoader(USER_MODEL_LOADER_ID, null, new LoaderManager.LoaderCallbacks<UserModel>() {
            @Override
            public Loader<UserModel> onCreateLoader(int id, Bundle args) {
                return new UserDataLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<UserModel> loader, UserModel data) {
                if (data != null) {
                    TextView textViewName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
                    assert textViewName != null;
                    textViewName.setText(data.getUserName());
                    TextView textViewEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email);
                    assert textViewEmail != null;
                    textViewEmail.setText(data.getUserEmail());
                }
            }

            @Override
            public void onLoaderReset(Loader<UserModel> loader) {

            }
        });

        Intent intent = new Intent(this, ItemsUpdateService.class);
        startService(intent);

        adapter = new ItemAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        assert recyclerView != null;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(ARRAY_LIST_LOADER_ID, null, new LoaderManager.LoaderCallbacks<ArrayList<TaskModel>>() {
            @Override
            public Loader<ArrayList<TaskModel>> onCreateLoader(int id, Bundle args) {
                return new ItemsLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<TaskModel>> loader, ArrayList<TaskModel> data) {
                refreshLayout.setRefreshing(false);
                adapter.setData(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<TaskModel>> loader) {

            }
        });
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                reloadAvatar();
                Toast.makeText(getApplicationContext(), "Photo is updated", Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION_1);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void reloadAvatar() {
        int width = circleImageView.getLayoutParams().width;
        int height = circleImageView.getLayoutParams().height;
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.loadImageCenterCrop(getString(R.string.picasso_url), width, height, circleImageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        else {
            super.onBackPressed();
        }
    }

    private void logout() {
        FirebaseTokenUploader firebaseTokenUploader =
                new FirebaseTokenUploader(getApplicationContext());
        firebaseTokenUploader.deleteToken();
        SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.apply();
        LoginManager.getInstance().logOut();
        SqlRepository repository = new SqlRepository(getApplicationContext());
        repository.deleteDatabases();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onOKButtonClick(String item) {
        WriteItemsTask itemsTask = new WriteItemsTask();
        TaskModel model = new TaskModel();
        model.setItemName(item);
        itemsTask.execute(model);
    }

    private class WriteItemsTask extends AsyncTask<TaskModel, Void, Void> {

        @Override
        protected Void doInBackground(TaskModel... params) {
            TaskModel item = params[0];
            SqlRepository repository = new SqlRepository(getApplicationContext());
            repository.addItems(item);
            return null;
        }
    }

    public static class DeleteItemsTask extends AsyncTask<TaskModel, Void, Void> {
        private SqlRepository repository;

        public DeleteItemsTask(Context context) {
            repository = new SqlRepository(context);
        }

        @Override
        protected Void doInBackground(TaskModel... params) {
            TaskModel item = params[0];
            repository.deleteItems(item);
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawerLayout.openDrawer(GravityCompat.START);
        return id == R.id.logout || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

}

