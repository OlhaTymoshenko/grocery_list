package com.example.android.grocerylist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ItemDialogFragment.ItemDialogListener,
        LoaderManager.LoaderCallbacks<ArrayList<TaskModel>> {
    private ItemAdapter adapter;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemDialogFragment().show(getFragmentManager(), "dialog");
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

        adapter = new ItemAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        assert recyclerView != null;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onOKButtonClick(String item) {
        WriteItemsTask itemsTask = new WriteItemsTask();
        TaskModel model = new TaskModel();
        model.setItemName(item);
        itemsTask.execute(model);

    }

    @Override
    public Loader<ArrayList<TaskModel>> onCreateLoader(int id, Bundle args) {
        return new ItemsLoader(MainActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<TaskModel>> loader, ArrayList<TaskModel> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<TaskModel>> loader) {
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
}

