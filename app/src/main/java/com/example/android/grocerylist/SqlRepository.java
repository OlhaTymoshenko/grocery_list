package com.example.android.grocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by lapa on 07.04.16.
 */
public class SqlRepository {
    private ItemWriterDBHelper dbHelper;
    private Context context;

    public SqlRepository(Context context) {
        dbHelper = new ItemWriterDBHelper(context);
        this.context = context;
    }


    public void addItems(TaskModel item) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item_name", item.getItemName());
        values.put("is_new", 1);
        database.insert("items", null, values);
        database.close();
        Intent intent = new Intent(context, SyncNewService.class);
        context.startService(intent);
        EventBus.getDefault().post(new ItemsUpdatedEvent());
    }

    public void deleteItems(TaskModel item) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_ID + " =?";
        String[] selectionArgs = {String.valueOf(item.getItemId())};
        database.update(
                ItemWriterContract.ItemEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        database.close();
        Intent intent = new Intent(context, SyncDeletedService.class);
        context.startService(intent);
        EventBus.getDefault().post(new ItemsUpdatedEvent());
    }

    public ArrayList<TaskModel> findItems() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<TaskModel> items = new ArrayList<>();
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_IS_DELETED + " =?";
        String[] selectionArgs = {String.valueOf(0)};
        Cursor cursor = database.query(
                ItemWriterContract.ItemEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_NAME));
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_ID));
            Integer remoteId = null;
            if (!cursor.isNull(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID))) {
                remoteId = cursor.getInt(cursor.getColumnIndexOrThrow
                        (ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID));
            }
            TaskModel model = new TaskModel();
            model.setItemName(itemName);
            model.setItemId(itemId);
            model.setRemoteId(remoteId);
            items.add(model);
        }
        cursor.close();
        database.close();
        return items;
    }

    public void updateItems(ArrayList<TaskModel> taskModels) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_UPDATED, 0);
        database.update(ItemWriterContract.ItemEntry.TABLE_NAME, values, null, null);
        for (int i = 0; i < taskModels.size(); i++) {
            values.clear();
            TaskModel model = taskModels.get(i);
            values.put("item_name", model.getItemName());
            values.put("remote_id", model.getRemoteId());
            values.put("updated", 1);
            database.insertWithOnConflict("items", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_UPDATED + " =? AND " +
                ItemWriterContract.ItemEntry.COLUMN_NAME_IS_NEW + " =? AND " +
                ItemWriterContract.ItemEntry.COLUMN_NAME_IS_DELETED + " =?";
        String[] selectionArgs = {String.valueOf(0), String.valueOf(0), String.valueOf(0)};
        database.delete(ItemWriterContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
        database.close();
        EventBus.getDefault().post(new ItemsUpdatedEvent());
    }

    public ArrayList<TaskModel> findNewItems() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<TaskModel> taskModels = new ArrayList<>();
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_IS_NEW + " =?";
        String[] selectionArgs = {String.valueOf(1)};
        Cursor cursor = database.query(
                ItemWriterContract.ItemEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_NAME));
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_ID));
            Integer remoteId = null;
            if (!cursor.isNull(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID))) {
                remoteId = cursor.getInt(cursor.getColumnIndexOrThrow
                        (ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID));
            }
            TaskModel model = new TaskModel();
            model.setItemName(itemName);
            model.setItemId(itemId);
            model.setRemoteId(remoteId);
            taskModels.add(model);
        }
        cursor.close();
        database.close();
        return taskModels;
    }

    public ArrayList<TaskModel> findDeletedItems() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<TaskModel> taskModels = new ArrayList<>();
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_IS_DELETED + " =?";
        String[] selectionArgs = {String.valueOf(1)};
        Cursor cursor = database.query(
                ItemWriterContract.ItemEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_NAME));
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_ID));
            Integer remoteId = null;
            if (!cursor.isNull(cursor.getColumnIndexOrThrow
                    (ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID))) {
                remoteId = cursor.getInt(cursor.getColumnIndexOrThrow
                        (ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID));
            }
            TaskModel model = new TaskModel();
            model.setItemName(itemName);
            model.setItemId(itemId);
            model.setRemoteId(remoteId);
            taskModels.add(model);
        }
        cursor.close();
        database.close();
        return taskModels;
    }

    public void setNewSynced(TaskModel model) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID, model.getRemoteId());
        values.put(ItemWriterContract.ItemEntry.COLUMN_NAME_IS_NEW, 0);
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_ID + " =?";
        String[] selectionArgs = {String.valueOf(model.getItemId())};
        database.update(
                ItemWriterContract.ItemEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        database.close();

    }

    public void setDeletedSynced (int itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_ID + " =?";
        String[] selectionArgs = {String.valueOf(itemId)};
        database.delete(ItemWriterContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
        database.close();
    }
}
