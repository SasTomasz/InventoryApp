package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.StorageContract.ProductsEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ProductCursorAdapter cursorAdapter;
    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open ProductActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        ListView productList = findViewById(R.id.listView);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productList.setEmptyView(emptyView);


        cursorAdapter = new ProductCursorAdapter(this, null);
        productList.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Uri uri = ContentUris.withAppendedId(ProductsEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_QUANTITY
        };
        return new CursorLoader(this, ProductsEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);

    }

}
