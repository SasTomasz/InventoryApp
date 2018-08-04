package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryDbHelper;
import com.example.android.inventoryapp.data.StorageContract;
import com.example.android.inventoryapp.data.StorageContract.InventoryEntry;

public class MainActivity extends AppCompatActivity {
    InventoryDbHelper mDbHelber = new InventoryDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // two times only for checking if it's working
        insertData();
        insertData();
        readData();
    }
    /* This method can insert Data to database using StorageContrack and InventoryDbHelper */
    public void insertData(){
        SQLiteDatabase db = mDbHelber.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "sunglasses");
        values.put(InventoryEntry.COLUMN_PRICE, 200);
        values.put(InventoryEntry.COLUMN_QUANTITY, 10);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Ray-Ban");
        values.put(InventoryEntry.COLUMN_PHONE_NUMBER, "00-800-12-152-52 ");

        db.insert(InventoryEntry.TABLE_NAME, null, values);

        values.clear();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "bag");
        values.put(InventoryEntry.COLUMN_PRICE, 600);
        values.put(InventoryEntry.COLUMN_QUANTITY, 50);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Louis Vuitton");
        values.put(InventoryEntry.COLUMN_PHONE_NUMBER, "00-800-76-968-09 ");

        db.insert(InventoryEntry.TABLE_NAME, null, values);


    }

    public void readData(){
        SQLiteDatabase db = mDbHelber.getReadableDatabase();

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        TextView displayTextView = findViewById(R.id.testDbView);

        try {
            displayTextView.setText("The inventory table contains " + cursor.getCount() + " different products.\n\n");
            displayTextView.append(InventoryEntry._ID + " - " + InventoryEntry.COLUMN_PRODUCT_NAME
                    + " - " + InventoryEntry.COLUMN_PRICE);

            // index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);

            while (cursor.moveToNext()){
                int currentId = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);

                // display values from each column of the current row in the cursor in the textView
                displayTextView.append("\n" + currentId + " - " + currentProductName + " - "
                    + currentPrice);
            }
        } finally {
            cursor.close();
        }

    }
}
