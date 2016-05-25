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
        public static final String COLUMN_NAME_REMOTE_ID = "remote_id";
        public static final String COLUMN_NAME_ITEM_NAME = "item_name";
        public static final String COLUMN_NAME_ITEM_UPDATED = "updated";
        public static final String COLUMN_NAME_IS_NEW = "is_new";
        public static final String COLUMN_NAME_IS_DELETED = "is_deleted";
    }

    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_USER_EMAIL = "user_email";
    }
}
