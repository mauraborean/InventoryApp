package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.example.android.inventoryapp.data.*;
import com.example.android.inventoryapp.data.ProductContract.*;
import com.example.android.inventoryapp.listener.*;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    
    private static final String LOG_TAG = ProductActivity.class.getName();
    private static final int LOADER_ID = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    
    private Uri productUri;
    private boolean itemHasChange;
    
    /*
     * EditText fields
     */
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText descEditText;
    private TextView quantityTextView;
    private ImageView photoImageView;
    private View quantityView;
    
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_product );
        setButtonsListeners();
        setUri();
        setViews();
    }
    
    /*
     * LOADER IMPLEMENTATION
     */
    
    @Override
    public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
        return new CursorLoader( this, productUri, null, null, null, null );
    }
    
    @Override
    public void onLoadFinished ( Loader<Cursor> loader, Cursor data ) {
        if ( data == null && data.getCount() < 0 ) {
            return;
        }
        
        if ( data.moveToFirst() ) {
            int iName = data.getColumnIndex( ProductEntry.COLUMN_NAME );
            int iPrice = data.getColumnIndex( ProductEntry.COLUMN_PRICE );
            int iQuantity = data.getColumnIndex( ProductEntry.COLUMN_QUANTITY );
            int iDesc = data.getColumnIndex( ProductEntry.COLUMN_DESC );
            int iImage = data.getColumnIndex( ProductEntry.COLUMN_IMAGE_DATA );
            
            String name = data.getString( iName );
            Double price = data.getDouble( iPrice );
            Integer quantity = data.getInt( iQuantity );
            String desc = data.getString( iDesc );
            Bitmap image = ProductContract.getBitmapFromBytes( data.getBlob( iImage ) );
            
            nameEditText.setText( name );
            priceEditText.setText( String.valueOf( price ) );
            quantityTextView.setText( String.valueOf( quantity ) );
            descEditText.setText( desc );
            photoImageView.setImageBitmap( image );
        }
    }
    
    @Override
    public void onLoaderReset ( Loader loader ) {
        nameEditText.setText( "" );
        priceEditText.setText( "" );
        quantityTextView.setText( "" );
        descEditText.setText( "" );
    }
    
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu_editor, menu );
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.action_save: {
                saveProduct();
                finish();
                return true;
            }
            case R.id.action_order_from_supplier: {
                return true;
            }
            case R.id.action_delete: {
                handleDeleteAction();
                return true;
            }
            case android.R.id.home: {
                handleReturnToParentActivity();
                return true;
            }
            default: {
                return super.onOptionsItemSelected( item );
            }
        }
    }
    
    @Override
    public void onBackPressed () {
        if ( itemHasChange ) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    public boolean onPrepareOptionsMenu ( Menu menu ) {
        super.onPrepareOptionsMenu( menu );
        
        if ( isAddView() ) {
            MenuItem delete = menu.findItem( R.id.action_delete );
            delete.setVisible( false );
            
            MenuItem orderFromSupplier = menu.findItem( R.id.action_order_from_supplier );
            orderFromSupplier.setVisible( false );
        }
        
        return true;
    }
    
    private void setButtonsListeners () {
        ImageView increment = ( ImageView ) findViewById( R.id.increment_quantity );
        increment.setOnClickListener( getOnClickIncrementQuantity() );
        
        ImageView decrement = ( ImageView ) findViewById( R.id.decrement_quantity );
        decrement.setOnClickListener( getOnClickDecrementQuantity() );
    }
    
    private View.OnClickListener getOnClickDecrementQuantity () {
        return new QuantityButtonListener( this, QuantityButtonListener.Action.DECREMENT );
    }
    
    private View.OnClickListener getOnClickIncrementQuantity () {
        return new QuantityButtonListener( this, QuantityButtonListener.Action.INCREMENT );
    }
    
    private void setUri () {
        productUri = getIntent().getData();
    }
    
    private void setViews () {
        initializeViews();
        setListenersOnViews();
        
        if ( isAddView() ) {
            setUpAddView();
        } else {
            setUpEditView();
        }
    }
    
    private void initializeViews () {
        nameEditText = ( EditText ) findViewById( R.id.name_edit_text );
        priceEditText = ( EditText ) findViewById( R.id.price_edit_text );
        quantityTextView = ( TextView ) findViewById( R.id.quantity_text_view );
        quantityEditText = ( EditText ) findViewById( R.id.quantity_edit_view );
        descEditText = ( EditText ) findViewById( R.id.desc_edit_text );
        photoImageView = ( ImageView ) findViewById( R.id.product_image );
        quantityView = findViewById( R.id.quantity_view );
    }
    
    private void setListenersOnViews () {
        View.OnTouchListener onTouchListener = getOnTouchListener();
        nameEditText.setOnTouchListener( onTouchListener );
        priceEditText.setOnTouchListener( onTouchListener );
        quantityTextView.setOnTouchListener( onTouchListener );
        descEditText.setOnTouchListener( onTouchListener );
    }
    
    private View.OnTouchListener getOnTouchListener () {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch ( View v, MotionEvent event ) {
                itemHasChange = true;
                return false;
            }
        };
    }
    
    /**
     * Sets the on click behavior for the Floating Action Button
     */
    private void setUpFloatingActionButton () {
        final FloatingActionButton fab = ( FloatingActionButton ) findViewById( R.id.fab_new_photo );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v ) {
                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                
                if ( intent.resolveActivity( getPackageManager() ) != null ) {
                    startActivityForResult( intent, REQUEST_IMAGE_CAPTURE );
                }
            }
        } );
    }
    
    @RequiresApi( api = Build.VERSION_CODES.KITKAT )
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data ) {
        if ( REQUEST_IMAGE_CAPTURE == requestCode && RESULT_OK == resultCode ) {
            Bundle extra = data.getExtras();
            Bitmap bitmap = ( Bitmap ) extra.get( "data" );
            
            if ( bitmap != null ) {
                photoImageView.setImageBitmap( bitmap );
                itemHasChange = true;
            }
        }
    }
    
    private boolean isAddView () {
        return productUri == null;
    }
    
    private void setUpAddView () {
        setTitle( R.string.add_product );
        setUpFloatingActionButton();
        quantityEditText.setVisibility( View.VISIBLE );
        quantityView.setVisibility( View.GONE );
        invalidateOptionsMenu();
    }
    
    private void setUpEditView () {
        setTitle( R.string.edit_product );
        quantityEditText.setVisibility( View.GONE );
        quantityView.setVisibility( View.VISIBLE );
        getLoaderManager().initLoader( LOADER_ID, null, this );
    }
    
    private void saveProduct () {
        ContentValues values = new ContentValues();
        values.put( ProductEntry.COLUMN_NAME, nameEditText.getText().toString().trim() );
        values.put( ProductEntry.COLUMN_PRICE, priceEditText.getText().toString().trim() );
        values.put( ProductEntry.COLUMN_DESC, descEditText.getText().toString().trim() );
        
        BitmapDrawable drawable = ( BitmapDrawable ) photoImageView.getDrawable();
        values.put( ProductEntry.COLUMN_IMAGE_DATA, ProductContract.getBitesFromBitmap( drawable.getBitmap() ) );
        
        if ( isAddView() ) {
            values.put( ProductEntry.COLUMN_QUANTITY, quantityEditText.getText().toString().trim() );
        } else {
            values.put( ProductEntry.COLUMN_QUANTITY, quantityTextView.getText().toString().trim() );
        }
        
        if ( valuesAreEmpty( values ) ) {
            return;
        }
        
        if ( isAddView() ) {
            insertProduct( values );
        } else {
            updateProduct( values );
        }
    }
    
    /**
     * Looks all the values stored in the given ContentValues and returns if all of the values are empty.
     *
     * @param values
     *
     * @return <codde>true</codde> if all of the values are empty, <code>false</code> otherwise
     */
    private boolean valuesAreEmpty ( ContentValues values ) {
        int emptyFields = 0;
        
        String name = values.getAsString( ProductEntry.COLUMN_NAME );
        if ( name.isEmpty() )
            ++emptyFields;
        
        String price = values.getAsString( ProductEntry.COLUMN_PRICE );
        if ( price.isEmpty() )
            ++emptyFields;
        
        String quantity = values.getAsString( ProductEntry.COLUMN_QUANTITY );
        if ( quantity.isEmpty() )
            ++emptyFields;
        
        String desc = values.getAsString( ProductEntry.COLUMN_DESC );
        if ( desc.isEmpty() )
            ++emptyFields;
        
        byte[] image = values.getAsByteArray( ProductEntry.COLUMN_IMAGE_DATA );
        if ( image.length == 0 )
            ++emptyFields;
        
        return emptyFields == values.size();
    }
    
    private void insertProduct ( ContentValues values ) {
        Uri response = getContentResolver().insert( ProductEntry.CONTENT_URI, values );
        
        String toastMsg;
        
        if ( response == null ) {
            toastMsg = getString( R.string.save_error );
        } else {
            toastMsg = getString( R.string.save_ok );
        }
        
        Toast.makeText( this, toastMsg, Toast.LENGTH_SHORT ).show();
    }
    
    private void updateProduct ( ContentValues values ) {
        int rowsUpdated = getContentResolver().update( productUri, values, null, null );
        
        String toastMsg;
        
        if ( rowsUpdated > 0 ) {
            toastMsg = getString( R.string.save_ok );
        } else {
            toastMsg = getString( R.string.save_error );
        }
        
        Toast.makeText( this, toastMsg, Toast.LENGTH_SHORT ).show();
    }
    
    private void deleteProduct () {
        if ( isAddView() ) {
            Log.e( LOG_TAG, "Calling delete method without an URI and without an product id" );
            return;
        }
        
        int rowsDeleted = getContentResolver().delete( productUri, null, null );
        String toastMsg;
        
        if ( rowsDeleted > 0 ) {
            toastMsg = getString( R.string.delete_ok );
        } else {
            toastMsg = getString( R.string.delete_error );
        }
        
        Toast.makeText( this, toastMsg, Toast.LENGTH_SHORT ).show();
    }
    
    private void handleReturnToParentActivity () {
        if ( itemHasChange ) {
            showUnsavedChangesDialog();
        } else {
            NavUtils.navigateUpFromSameTask( this );
        }
    }
    
    private void handleDeleteAction () {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( getString( R.string.delete_product ) );
        
        builder.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialog, int which ) {
                deleteProduct();
                finish();
            }
        } );
        
        builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialog, int which ) {
                if ( dialog != null ) {
                    dialog.dismiss();
                }
            }
        } );
        
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    private void showUnsavedChangesDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( getString( R.string.unsaved_changes ) );
        
        builder.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialog, int which ) {
                finish();
            }
        } );
        
        builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick ( DialogInterface dialog, int which ) {
                if ( dialog != null ) {
                    dialog.dismiss();
                }
            }
        } );
        
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    // TODO: order from supplier
    
}
