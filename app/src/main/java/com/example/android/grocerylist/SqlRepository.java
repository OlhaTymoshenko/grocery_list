package com.example.android.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by lapa on 07.04.16.
 */
public class SqlRepository {
    private ItemWriterDBHelper dbHelper;

    public SqlRepository(Context context) {
        dbHelper = new ItemWriterDBHelper(context);
    }

    public void addItems(TaskModel item) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item_name", item.getItemName());
        database.insert("items", null, values);
        database.close();
        EventBus.getDefault().post(new ItemsUpdatedEvent());
    }

    public void deleteItems(TaskModel item) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_NAME + " LIKE ?";
        String[] selectionArgs = {item.getItemName()};
        database.delete(ItemWriterContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
        database.close();
        EventBus.getDefault().post(new ItemsUpdatedEvent());
    }

    public ArrayList<TaskModel> findItems() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<TaskModel> items = new ArrayList<>();
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
            TaskModel model = new TaskModel();
            model.setItemName(itemName);
            items.add(model);
        }
        cursor.close();
        database.close();
        return items;
    }
}
