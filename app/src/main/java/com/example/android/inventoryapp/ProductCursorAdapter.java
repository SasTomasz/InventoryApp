package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.StorageContract.ProductsEntry;


public class ProductCursorAdapter extends CursorAdapter {
    int position;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView name = view.findViewById(R.id.tv_product_name);
        TextView price = view.findViewById(R.id.tv_product_price);
        final TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        Button buttonSale = view.findViewById(R.id.b_sale);

        position = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsEntry._ID));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductsEntry.COLUMN_QUANTITY));
        final Uri uri = ContentUris.withAppendedId(ProductsEntry.CONTENT_URI, position);

        buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                Log.i("TEST", "onClick >> productQuantity =  " + quantity);

                if (quantity == 0) {
                    Toast toast = Toast.makeText(context, R.string.dont_have_product, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    values.put(ProductsEntry.COLUMN_QUANTITY, quantity - 1);
                    int updateState = context.getContentResolver().update(uri, values, null, null);
                    if (updateState == 0) {
                        Toast toast = Toast.makeText(context, R.string.error_update_product, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, R.string.sale_product, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }
        });

        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_PRODUCT_NAME));
        int productPrice = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_PRICE));
        int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_QUANTITY));

        name.setText(productName);
        price.setText(String.valueOf(productPrice));
        tvQuantity.setText(String.valueOf(productQuantity));
    }

}
