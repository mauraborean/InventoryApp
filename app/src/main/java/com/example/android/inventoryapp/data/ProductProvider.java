package com.example.android.inventoryapp.data;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.support.annotation.*;
import android.text.*;
import android.util.*;

import com.example.android.inventoryapp.data.ProductContract.*;

public class ProductProvider extends ContentProvider {
    
    private static final String LOG_TAG = ProductProvider.class.getName();
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    
    private static final UriMatcher uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
    private ProductDbHelper productDbHelper;
    
    static {
        uriMatcher.addURI( ProductContract.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCTS, PRODUCTS );
        uriMatcher.addURI( ProductContract.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCTS + "/#", PRODUCT_ID );
    }
    
    @Override
    public boolean onCreate () {
        productDbHelper = new ProductDbHelper( getContext() );
        return true;
    }
    
    @Nullable
    @Override
    public String getType ( @NonNull Uri uri ) {
        String type;
        
        switch ( uriMatcher.match( uri ) ) {
            case PRODUCTS:
                type = ProductEntry.DIRECTORY_MIME_TYPE;
                break;
            case PRODUCT_ID:
                type = ProductEntry.ITEM_MIME_TYPE;
                break;
            default:
                throw new IllegalArgumentException( String.format( "No match found for uri: %s", uri.toString() ) );
        }
        
        return type;
    }
    
    @Nullable
    @Override
    public Cursor query ( @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder ) {
        Cursor cursor;
        SQLiteDatabase db = productDbHelper.getReadableDatabase();
        
        switch ( uriMatcher.match( uri ) ) {
            case PRODUCTS:
                cursor = db.query( ProductEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder, null, null );
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf( ContentUris.parseId( uri ) ) };
                
                cursor = db.query( ProductEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder, null, null );
                break;
            default:
                throw new IllegalArgumentException( String.format( "No match found for uri: %s", uri.toString() ) );
        }
    
        cursor.setNotificationUri( getContext().getContentResolver(), uri );
        
        return cursor;
    }
    
    @Nullable
    @Override
    public Uri insert ( @NonNull Uri uri, @Nullable ContentValues values ) {

        if ( !isDataToInsertValid( values ) ) {
            return null;
        }
        
        switch ( uriMatcher.match( uri ) ) {
            case PRODUCTS: {
                return insertProduct( uri, values );
            }
            default:
                throw new IllegalArgumentException( String.format( "No match found for uri: %s", uri.toString() ) );
        }
    }
    
    @Override
    public int delete ( @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs ) {
        int rowsDeleted;
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        
        switch ( uriMatcher.match( uri ) ) {
            case PRODUCTS:
                rowsDeleted = db.delete( ProductEntry.TABLE_NAME, selection, selectionArgs );
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf( ContentUris.parseId( uri ) ) };
                
                rowsDeleted = db.delete( ProductEntry.TABLE_NAME, selection, selectionArgs );
                break;
            default:
                throw new IllegalArgumentException( String.format( "No match found for uri: %s", uri.toString() ) );
        }
        
        if ( rowsDeleted > 0 ) {
            getContext().getContentResolver().notifyChange( uri, null );
        }
        
        return rowsDeleted;
    }
    
    @Override
    public int update ( @NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs ) {
        int rowsUpdated = 0;
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        
        switch ( uriMatcher.match( uri ) ) {
            case PRODUCTS: {
                throw new IllegalArgumentException( String.format( "Bad uri: %s", uri.toString() ) );
            }
            case PRODUCT_ID: {
                if ( !isDataToInsertValid( values ) ) {
                    break;
                }
                
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf( ContentUris.parseId( uri ) ) };
                
                rowsUpdated = db.update( ProductEntry.TABLE_NAME, values, selection, selectionArgs );
                break;
            }
            default: {
                throw new IllegalArgumentException( String.format( "No match found for uri: %s", uri.toString() ) );
            }
        }
        
        if ( rowsUpdated > 0 ) {
            getContext().getContentResolver().notifyChange( uri, null );
        }
        
        return rowsUpdated;
    }
    
    private boolean isDataToInsertValid ( ContentValues values ) {
        boolean isValid = true;
        
        String name = values.getAsString( ProductEntry.COLUMN_NAME );
        if ( !ProductEntry.isNameValid( name ) ) {
            Log.e( LOG_TAG, "Invalid name: " + name );
            isValid = false;
        }
        
        String sPrice = values.getAsString( ProductEntry.COLUMN_PRICE );
        if ( TextUtils.isEmpty( sPrice ) ) {
            Log.e( LOG_TAG, "Invalid price: " + sPrice );
            isValid = false;
        } else {
            Double price = Double.parseDouble( sPrice );
            if ( !ProductEntry.isPriceValid( price ) ) {
                Log.e( LOG_TAG, "Invalid price: " + price );
                isValid = false;
            }
        }
        
        String sQuantity = values.getAsString( ProductEntry.COLUMN_QUANTITY );
        if ( TextUtils.isEmpty( sQuantity ) ) {
            Log.e( LOG_TAG, "Quantity found empty! Default value is 0" );
            sQuantity = "0";
        }
        
        Integer quantity = Integer.parseInt( sQuantity );
        if ( !ProductEntry.isQuantityValid( quantity ) ) {
            Log.e( LOG_TAG, "Invalid quantity: " + quantity );
            isValid = false;
        }
        
        String desc = values.getAsString( ProductEntry.COLUMN_DESC );
        if ( !ProductEntry.isDescriptionValid( desc ) ) {
            Log.e( LOG_TAG, "Invalid desc: " + desc );
            isValid = false;
        }
        
        return isValid;
    }
    
    private Uri insertProduct ( Uri uri, ContentValues values ) {
        Uri responseUri = null;
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        
        long rowId = db.insert( ProductEntry.TABLE_NAME, null, values );
        
        if ( rowId > 0 ) {
            responseUri = ContentUris.withAppendedId( uri, rowId );
        }
        
        getContext().getContentResolver().notifyChange( uri, null );
        return responseUri;
    }
}
