package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.inventoryapp.data.StorageContract.ProductsEntry;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = ProductActivity.class.getSimpleName();
    private boolean mProductHasChanged = false;
    Uri uri;
    int isUri;
    private static final int URL_LOADER = 0;

    // EditText field to enter the product's name
    private EditText mProductNameEditText;

    // EditText field to enter the product's price
    private EditText mProductPriceEditText;

    // EditText field to enter the product's quantity
    private EditText mProductQuantityEditText;

    // EditText field to enter the product's supplier name
    private EditText mSupplierNameEditText;

    // EditText field to enter the product's supplier phone number
    private EditText mSupplierPhoneNumberEditText;

    private Button mOrderButton;

    private ImageButton mIncreaseQuantityButton;
    private ImageButton mDecreaseQuantityButton;

    int quantity;



    // Listens for any user touches on a View, implying that they are modifying
    // the view, and change the mProductHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Find all relevant views that we will need to read user input from
        mProductNameEditText = findViewById(R.id.edit_product_name);
        mProductPriceEditText = findViewById(R.id.edit_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);
        mOrderButton = findViewById(R.id.button_order);
        mIncreaseQuantityButton = findViewById(R.id.b_quantity_increase);
        mDecreaseQuantityButton = findViewById(R.id.b_quantity_decrease);

        // set onTouchListener on that views
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
        mIncreaseQuantityButton.setOnTouchListener(mTouchListener);
        mDecreaseQuantityButton.setOnTouchListener(mTouchListener);


        uri = getIntent().getData();
        if (uri == null) {
            isUri = 0;
            setTitle("Add Product");
            mOrderButton.setVisibility(View.GONE);
        } else {
            isUri = 1;
            setTitle("Edit Product");

            // initialize Loader
            getLoaderManager().initLoader(URL_LOADER, null, this);
        }


        mIncreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(mProductQuantityEditText.getText().toString());
                quantity += 1;
                mProductQuantityEditText.setText(String.valueOf(quantity));
            }
        });

        mDecreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(mProductQuantityEditText.getText().toString());
                if(quantity == 0){
                    String toastText = getString(R.string.positive_quantity);
                    Toast toast = Toast.makeText(ProductActivity.this, toastText, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    quantity -= 1;
                    mProductQuantityEditText.setText(String.valueOf(quantity));
                }
            }
        });



        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_DIAL);
                sendIntent.setData(Uri.parse("tel:" + mSupplierPhoneNumberEditText.getText().toString()));
                startActivity(sendIntent);
            }
        });


    }

    // Create the menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                boolean positiveValidationData = dataValidation();
                if(positiveValidationData){
                    saveProduct();
                }
                return true;

            // Respond to a click on the "delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;


                // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                Log.i(TAG, "onOptionsItemSelected >> mProductHasChanged = " + mProductHasChanged);
                // If the product hasn't changed, continue with navigating up to parent activity
                if(!mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(ProductActivity.this);
                    return true;
                }

                // if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean dataValidation(){
        String productName = mProductNameEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();

        if(TextUtils.isEmpty(productName)){
            Toast.makeText(this, R.string.warn_product, Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(mProductPriceEditText.getText().toString())){
            Toast.makeText(this, R.string.warn_price, Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(supplierName)){
            Toast.makeText(this, R.string.warn_supplier, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(supplierPhoneNumber)){
            Toast.makeText(this, R.string.warn_supplier_phone, Toast.LENGTH_SHORT).show();
        }

        return !TextUtils.isEmpty(productName) && !TextUtils.isEmpty(supplierName)
                && !TextUtils.isEmpty(supplierPhoneNumber) && !TextUtils.isEmpty(mProductPriceEditText.getText().toString());
    }

    public void saveProduct(){
        String productName = mProductNameEditText.getText().toString().trim();
        int productPrice = Integer.parseInt(mProductPriceEditText.getText().toString());
        int productQuantity = Integer.parseInt(mProductQuantityEditText.getText().toString());
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(ProductsEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductsEntry.COLUMN_PRICE, productPrice);
        values.put(ProductsEntry.COLUMN_QUANTITY, productQuantity);
        values.put(ProductsEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductsEntry.COLUMN_PHONE_NUMBER, supplierPhoneNumber);

        switch (isUri){
            case 0:
                Uri insertState = getContentResolver().insert(ProductsEntry.CONTENT_URI, values);

                if (insertState == null) {
                    String toastText = getString(R.string.error_saving_product);
                    Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String toastText = getString(R.string.product_saved);
                    Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_LONG);
                    toast.show();
                }
                break;

            case 1:
                int updateState = getContentResolver().update(uri, values, null, null);
                Log.i(TAG, "saveProduct >> updateState = " + updateState);
                if (updateState == 0) {
                    String toastText = getString(R.string.error_update_product);
                    Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String toastText = getString(R.string.update_product);
                    Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
        }


        finish();
    }

    public void deleteProduct(){
        int rowsDeleted = getContentResolver().delete(uri, null, null);

        if(rowsDeleted != 0 ){
            String toastText = getString(R.string.editor_delete_product_successful);
            Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            String toastText = getString(R.string.editor_delete_product_error);
            Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
            toast.show();
        }

        finish();


    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // If there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
            ProductsEntry._ID,
            ProductsEntry.COLUMN_PRODUCT_NAME,
            ProductsEntry.COLUMN_PRICE,
            ProductsEntry.COLUMN_QUANTITY,
            ProductsEntry.COLUMN_SUPPLIER_NAME,
            ProductsEntry.COLUMN_PHONE_NUMBER
        };
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            String indexProductName = data.getString(data.getColumnIndex(ProductsEntry.COLUMN_PRODUCT_NAME));
            int indexProductPrice = data.getInt(data.getColumnIndex(ProductsEntry.COLUMN_PRICE));
            int indexProductQuantity = data.getInt(data.getColumnIndex(ProductsEntry.COLUMN_QUANTITY));
            String indexSupplierName = data.getString(data.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_NAME));
            String indexSupplierPhoneNumber = data.getString(data.getColumnIndex(ProductsEntry.COLUMN_PHONE_NUMBER));

            mProductNameEditText.setText(indexProductName);
            mProductPriceEditText.setText(Integer.toString(indexProductPrice));
            mProductQuantityEditText.setText(Integer.toString(indexProductQuantity));
            mSupplierNameEditText.setText(indexSupplierName);
            mSupplierPhoneNumberEditText.setText(indexSupplierPhoneNumber);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText(null);
        mProductPriceEditText.setText(null);
        mProductQuantityEditText.setText(null);
        mSupplierNameEditText.setText(null);
        mSupplierPhoneNumberEditText.setText(null);

    }
}
