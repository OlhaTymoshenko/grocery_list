package com.example.android.grocerylist;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ItemDialogFragment.ItemDialogListener,
        LoaderManager.LoaderCallbacks<ArrayList<String>> {
    private ArrayList<String> data;
    private ItemAdapter adapter;

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

        data = new ArrayList<>();
        adapter = new ItemAdapter();
        ListView listView = (ListView) findViewById(R.id.list);
        assert listView != null;
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOKButtonClick(String item) {
        data.add(item);
        adapter.notifyDataSetChanged();
        WriteItemsTask itemsTask = new WriteItemsTask();
        itemsTask.execute(item);

    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        ItemsLoader loader = new ItemsLoader(MainActivity.this);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        this.data = data;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {
    }

    private class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                ViewHolder holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                view = convertView;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            String item = getItem(position);
            holder.textView.setText(item);
            holder.checkBox.setChecked(false);
            return view;
        }

        private class ViewHolder {
            final CheckBox checkBox;
            final TextView textView;

            private ViewHolder(View view) {
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            String text = textView.getText().toString();
                            data.remove(text);
                            adapter.notifyDataSetChanged();
                            DeleteItemsTask itemsTask = new DeleteItemsTask();
                            itemsTask.execute(text);
                        }
                    }
                });
                textView = (TextView) view.findViewById(R.id.list_item_textview);
            }
        }

    }
    private class WriteItemsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String item = params[0];
            SqlRepository repository = new SqlRepository(getApplicationContext());
            repository.addItems(item);
            return null;
        }
    }

    private class DeleteItemsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String item = params[0];
            SqlRepository repository = new SqlRepository(getApplicationContext());
            repository.deleteItems(item);
            return null;
        }
    }
}

