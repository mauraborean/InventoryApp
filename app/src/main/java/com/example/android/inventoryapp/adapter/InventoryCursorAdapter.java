package com.example.android.inventoryapp.adapter;

import android.content.*;
import android.database.*;
import android.view.*;
import android.widget.*;

import com.example.android.inventoryapp.*;
import com.example.android.inventoryapp.data.ProductContract.*;

public class InventoryCursorAdapter extends CursorAdapter {
    
    public InventoryCursorAdapter ( Context context, Cursor c ) {
        super( context, c, 0 );
    }
    
    @Override
    public View newView ( Context context, Cursor cursor, ViewGroup parent ) {
        return LayoutInflater.from( context ).inflate( R.layout.product_item, parent, false );
    }
    
    @Override
    public void bindView ( View view, Context context, Cursor cursor ) {
        TextView nameTextView = ( TextView ) view.findViewById( R.id.product_name );
        TextView priceTextView = ( TextView ) view.findViewById( R.id.product_price );
        TextView quantityTextView = ( TextView ) view.findViewById( R.id.product_stock );
        
        int iName = cursor.getColumnIndex( ProductEntry.COLUMN_NAME );
        int iPrice = cursor.getColumnIndex( ProductEntry.COLUMN_PRICE );
        int iQuantity = cursor.getColumnIndex( ProductEntry.COLUMN_QUANTITY );
        
        String name = cursor.getString( iName );
        String price = String.valueOf( cursor.getDouble( iPrice ) + " " + context.getString( R.string.currency ) );
        String quantity = String.valueOf( cursor.getInt( iQuantity ) );
        
        nameTextView.setText( name );
        priceTextView.setText( price );
        quantityTextView.setText( quantity );
    }
}
