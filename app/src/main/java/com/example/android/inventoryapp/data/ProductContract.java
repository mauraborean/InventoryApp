package com.example.android.inventoryapp.data;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.provider.*;
import android.text.*;

import java.io.*;

public final class ProductContract {
    
    static final String CONTENT_AUTHORITY = "com.example.android.products";
    private static final Uri BASE_CONTENT = Uri.parse( "content://" + CONTENT_AUTHORITY );
    
    public static class ProductEntry implements BaseColumns {
        
        static final String TABLE_NAME = "product";
        static final String PATH_PRODUCTS = "products";
        
        static final String DIRECTORY_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        static final String ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final Uri CONTENT_URI = Uri.withAppendedPath( BASE_CONTENT, PATH_PRODUCTS );
        
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_IMAGE_NAME = "image_name";
        public static final String COLUMN_IMAGE_DATA = "image_data";
        
        static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_PRICE + " DECIMAL(5,2) NOT NULL, " + COLUMN_QUANTITY + " INTEGER, " + COLUMN_IMAGE_NAME + " TEXT, " + COLUMN_IMAGE_DATA + " BLOB, " + COLUMN_DESC + " TEXT " + ");";
        static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME;
        
        static boolean isNameValid ( String name ) {
            return !TextUtils.isEmpty( name );
        }
        
        static boolean isPriceValid ( Double price ) {
            return price != null && price > 0;
        }
        
        static boolean isQuantityValid ( Integer quantity ) {
            return quantity != null & quantity > -1;
        }
        
        static boolean isDescriptionValid ( String desc ) {
            return desc != null;
        }
    }
    
    public static byte[] getBitesFromBitmap ( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress( Bitmap.CompressFormat.PNG, 0, stream );
        return stream.toByteArray();
    }
    
    public static Bitmap getBitmapFromBytes ( byte[] bytes ) {
        return BitmapFactory.decodeByteArray( bytes, 0, bytes.length );
    }
}
