package com.example.android.grocerylist;

import android.provider.BaseColumns;

/**
 * Created by lapa on 04.04.16.
 */
public final class ItemWriterContract {
    public ItemWriterContract () {}

    public static abstract class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_ITEM_ID = "item_id";
        public static final String COLUMN_NAME_ITEM_NAME = "item_name";
    }
}
