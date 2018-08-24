package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

public final class StorageContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final String PATH_PRODUCTS = "products";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public StorageContract() {
    }

    /* This is class help making database with this COLUMNS and TABLE info*/
    public static class ProductsEntry implements BaseColumns {
        // MIME type for list of products
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        // MIME type for single product
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PHONE_NUMBER = "supplier_phone_number";
    }
}
