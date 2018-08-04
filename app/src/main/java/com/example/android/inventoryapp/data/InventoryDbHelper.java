package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.StorageContract.InventoryEntry;


public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "storage.db";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + InventoryEntry.TABLE_NAME
            + " (" + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
            + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
            + InventoryEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL);";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
