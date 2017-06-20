package com.example.android.inventoryapp.listener;

import android.app.*;
import android.view.*;
import android.widget.*;

import com.example.android.inventoryapp.*;

public class QuantityButtonListener implements View.OnClickListener {
    
    private Activity activity;
    private Action action;
    
    public QuantityButtonListener ( Activity activity, Action action ) {
        this.activity = activity;
        this.action = action;
    }
    
    public enum Action {
        INCREMENT, DECREMENT
    }
    
    @Override
    public void onClick ( View v ) {
        TextView quantity = ( TextView ) activity.findViewById( R.id.quantity_text_view );
        int intQuantity = Integer.parseInt( quantity.getText().toString() );
        intQuantity += getActionInt( intQuantity );
        quantity.setText( String.valueOf( intQuantity ) );
    }
    
    /**
     * Retrieves the value to add to the quantity depending on its action
     *
     * @param intQuantity actual quantity value
     *
     * @return 1 if the action is {@link Action#INCREMENT}, -1 if is {@link Action#DECREMENT} and 0 if the quantity has reached the 0 value and the action is decrement
     */
    private int getActionInt ( int intQuantity ) {
        int i = 0;
        
        switch ( action ) {
            case INCREMENT:
                i = 1;
                break;
            case DECREMENT:
                if ( intQuantity > 0 ) {
                    i = -1;
                } else {
                    Toast.makeText( activity.getApplicationContext(), activity.getString( R.string.quantity_lower_than_zero ), Toast.LENGTH_SHORT ).show();
                }
                break;
        }
        
        return i;
    }
}
