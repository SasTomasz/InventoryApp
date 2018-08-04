package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public final class StorageContract {

    public StorageContract() {
    }

    /* This is class help making database with this COLUMNS and TABLE info*/
    public static class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventory";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PHONE_NUMBER = "supplier_phone_number";
    }
}
