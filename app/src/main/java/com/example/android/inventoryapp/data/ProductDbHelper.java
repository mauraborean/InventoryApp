package com.example.android.inventoryapp.data;

import android.content.*;
import android.database.sqlite.*;

import com.example.android.inventoryapp.data.ProductContract.*;

class ProductDbHelper extends SQLiteOpenHelper {
    
    private static final String DB_NAME = "inventory";
    private static final int DB_VERSION = 1;
    
    ProductDbHelper ( Context context ) {
        super( context, DB_NAME, null, DB_VERSION );
    }
    
    @Override
    public void onCreate ( SQLiteDatabase db ) {
        db.execSQL( ProductEntry.SQL_CREATE );
    }
    
    @Override
    public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( ProductEntry.SQL_DELETE );
        onCreate( db );
    }
}
