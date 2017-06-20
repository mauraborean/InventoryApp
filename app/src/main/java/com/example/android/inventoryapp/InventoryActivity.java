package com.example.android.inventoryapp;

import android.app.LoaderManager.*;
import android.content.*;
import android.database.*;
import android.support.design.widget.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.example.android.inventoryapp.adapter.*;
import com.example.android.inventoryapp.data.ProductContract.*;

public class InventoryActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    
    private static final int LOADER_ID = 1;
    
    private InventoryCursorAdapter cursorAdapter;
    
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_inventory );
        
        cursorAdapter = new InventoryCursorAdapter( this, null );
        
        setUpListView();
        setUpFloatingActionButton();
        getLoaderManager().initLoader( LOADER_ID, null, this );
    }
    
    /*
     * LOADER IMPLEMENTATION
     */
    @Override
    public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
        String[] projections = new String[] { ProductEntry._ID, ProductEntry.COLUMN_NAME, ProductEntry.COLUMN_PRICE, ProductEntry.COLUMN_QUANTITY };
        return new CursorLoader( this, ProductEntry.CONTENT_URI, projections, null, null, null );
    }
    
    @Override
    public void onLoadFinished ( Loader<Cursor> loader, Cursor data ) {
        cursorAdapter.swapCursor( data );
    }
    
    @Override
    public void onLoaderReset ( Loader<Cursor> loader ) {
        cursorAdapter.swapCursor( null );
    }
    
    /**
     * Sets the on click behavior for the Floating Action Button
     */
    private void setUpFloatingActionButton () {
        final FloatingActionButton fab = ( FloatingActionButton ) findViewById( R.id.fab_open_product_view );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick ( View v ) {
                Intent intent = new Intent( InventoryActivity.this, ProductActivity.class );
                startActivity( intent );
            }
        } );
    }
    
    /**
     * Sets the ListView properties
     */
    private void setUpListView () {
        ListView listView = ( ListView ) findViewById( R.id.list );
        listView.setAdapter( cursorAdapter );
        
        View emptyView = findViewById( R.id.empty_view );
        listView.setEmptyView( emptyView );
        
        listView.setOnItemClickListener( getOnItemClickListener() );
    }
    
    /**
     * Creates an {@link AdapterView.OnItemClickListener} and defines its behavior
     *
     * @return an {@link AdapterView.OnItemClickListener} new instance
     */
    private AdapterView.OnItemClickListener getOnItemClickListener () {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick ( AdapterView<?> parent, View view, int position, long id ) {
                Intent intent = new Intent( InventoryActivity.this, ProductActivity.class );
                intent.setData( ContentUris.withAppendedId( ProductEntry.CONTENT_URI, id ) );
                startActivity( intent );
            }
        };
    }
}
