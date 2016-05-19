package com.example.android.grocerylist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ItemDialogFragment.ItemDialogListener,
        LoaderManager.LoaderCallbacks<ArrayList<TaskModel>> {
    private ItemAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemDialogFragment().show(getFragmentManager(), "dialog");
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
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
        Intent intent = new Intent(MainActivity.this, ItemsUpdateService.class);
        startService(intent);
        adapter = new ItemAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        assert recyclerView != null;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    private void logout() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.apply();
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        SqlRepository repository = new SqlRepository(getApplicationContext());
        repository.logout();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

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

