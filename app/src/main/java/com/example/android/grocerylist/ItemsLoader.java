package com.example.android.grocerylist;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        ItemWriterDBHelper dbHelper = new ItemWriterDBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<String> items = new ArrayList<>();
        String[] result = {ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_NAME};
        Cursor cursor = database.query(
                ItemWriterContract.ItemEntry.TABLE_NAME,
                result,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_NAME));
            items.add(itemName);
        }
        cursor.close();
        database.close();
        return items;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
