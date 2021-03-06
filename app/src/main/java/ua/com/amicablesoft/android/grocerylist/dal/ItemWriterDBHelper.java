package ua.com.amicablesoft.android.grocerylist.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lapa on 04.04.16.
 */
public class ItemWriterDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "ItemWriter.db";

    public ItemWriterDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemWriterContract.ItemEntry.TABLE_NAME + " (" +
                ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemWriterContract.ItemEntry.COLUMN_NAME_REMOTE_ID + " INTEGER UNIQUE, " +
                ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_NAME +" TEXT NOT NULL, " +
                ItemWriterContract.ItemEntry.COLUMN_NAME_ITEM_UPDATED + " INTEGER NOT NULL DEFAULT 0, " +
                ItemWriterContract.ItemEntry.COLUMN_NAME_IS_NEW + " INTEGER NOT NULL DEFAULT 0, " +
                ItemWriterContract.ItemEntry.COLUMN_NAME_IS_DELETED + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + ItemWriterContract.UserEntry.TABLE_NAME + " (" +
                ItemWriterContract.UserEntry.COLUMN_NAME_USER_NAME + " TEXT NOT NULL, " +
                ItemWriterContract.UserEntry.COLUMN_NAME_USER_EMAIL + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemWriterContract.ItemEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ItemWriterContract.UserEntry.TABLE_NAME);
        onCreate(db);
    }
}
