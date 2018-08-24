package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.StorageContract.ProductsEntry;

public class ProductProvider extends ContentProvider {
    // Database Helper class
    InventoryDbHelper mInventoryDbHelper;

    // Uri matcher code for the content URI for the products table
    private static final int PRODUCTS = 100;

    // Uri matcher code for the content URI for the products table (one current product)
    private static final int PRODUCT_ID = 101;

    //Create UriMatcher object to mach a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer runs the first time anything is called from this class.
    static {
        // Calls to addURI() for all of the content URI patterns that the provider should recognize
        sUriMatcher.addURI(StorageContract.CONTENT_AUTHORITY, StorageContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(StorageContract.CONTENT_AUTHORITY, StorageContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    // TAG for Log messages
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * Initialize Provider and Database Helper object
     */
    @Override
    public boolean onCreate() {
        mInventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    // Perform the query for the given URI
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase db = mInventoryDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match){
            case PRODUCTS:

                cursor = db.query(ProductsEntry.TABLE_NAME, projection,
                        null, null, null, null, null);
                break;

            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = db.query(ProductsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
        }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return ProductsEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }
    }

    // Insert a product into the database with the given content values.
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String productName = values.getAsString(ProductsEntry.COLUMN_PRODUCT_NAME);
        if(productName == null){
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer productPrice = values.getAsInteger(ProductsEntry.COLUMN_PRICE);
        if(productPrice == null){
            throw new IllegalArgumentException("Product requires a price");
        }

        if(productPrice < 0){
            throw new IllegalArgumentException("Product requires positive price value");
        }

        Integer quantity = values.getAsInteger(ProductsEntry.COLUMN_QUANTITY);
        if(quantity < 0){
            throw new IllegalArgumentException("Product requires positive quantity value");
        }

        String supplierName = values.getAsString(ProductsEntry.COLUMN_SUPPLIER_NAME);
        if(supplierName == null){
            throw new IllegalArgumentException("Product requires a supplier name");
        }

        String supplierPhoneNumber = values.getAsString(ProductsEntry.COLUMN_PHONE_NUMBER);
        if(supplierPhoneNumber == null){
            throw new IllegalArgumentException("Product requires a supplier phone number");
        }

        // Get writable database
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        long id = db.insert(ProductsEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return the new URI with the ID appended
        return ContentUris.withAppendedId(uri, id);
    }

    //
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                int rowsDeleted = db.delete(ProductsEntry.TABLE_NAME, null, null);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Delete is not support for " + uri);
        }
    }

    // Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);

            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    // Update product in the database with the given content values
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        ContentValues newValues = new ContentValues();
        if(values.containsKey(ProductsEntry.COLUMN_PRODUCT_NAME)){
            String productName = values.getAsString(ProductsEntry.COLUMN_PRODUCT_NAME);
            if(productName == null){
                throw new IllegalArgumentException("Product requires a name");
            } else {
                newValues.put(ProductsEntry.COLUMN_PRODUCT_NAME, productName);
            }
        }

        if(values.containsKey((ProductsEntry.COLUMN_PRICE))){
            Integer productPrice = values.getAsInteger(ProductsEntry.COLUMN_PRICE);
            if(productPrice == null){
                throw new IllegalArgumentException("Product requires a price");
            }

            if(productPrice < 0){
                throw new IllegalArgumentException("Product requires positive price value");
            } else {
                newValues.put(ProductsEntry.COLUMN_PRICE, productPrice);
            }
        }

        if(values.containsKey(ProductsEntry.COLUMN_QUANTITY)){
            Integer quantity = values.getAsInteger(ProductsEntry.COLUMN_QUANTITY);
            if(quantity < 0){
                throw new IllegalArgumentException("Product requires positive quantity value");
            } else {
                newValues.put(ProductsEntry.COLUMN_QUANTITY, quantity);
            }
        }

        if(values.containsKey(ProductsEntry.COLUMN_SUPPLIER_NAME)){
            String supplierName = values.getAsString(ProductsEntry.COLUMN_SUPPLIER_NAME);
            if(supplierName == null){
                throw new IllegalArgumentException("Product requires a supplier name");
            } else {
                newValues.put(ProductsEntry.COLUMN_SUPPLIER_NAME, supplierName);
            }
        }

        if(values.containsKey(ProductsEntry.COLUMN_PHONE_NUMBER)){
            String supplierPhoneNumber = values.getAsString(ProductsEntry.COLUMN_PHONE_NUMBER);
            if(supplierPhoneNumber == null){
                throw new IllegalArgumentException("Product requires a supplier phone number");
            } else {
                newValues.put(ProductsEntry.COLUMN_PHONE_NUMBER, supplierPhoneNumber);
            }
        }

        if(values.size() == 0){
            return 0;
        }

        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(ProductsEntry.TABLE_NAME, newValues, selection, selectionArgs);

        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
