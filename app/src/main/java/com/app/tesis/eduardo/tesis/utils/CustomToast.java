package com.app.tesis.eduardo.tesis.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Eduardo on 29/09/2016.
 */

public class CustomToast {

    public static void centeredToast(Context context, String message){
        Toast toast = Toast.makeText(context,message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
