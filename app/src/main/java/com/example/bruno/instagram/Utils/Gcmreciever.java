package com.example.bruno.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmReceiver;

/**
 * Created by bruno on 12/06/2018.
 */

public class Gcmreciever extends GcmReceiver {
    public Gcmreciever() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Toast.makeText(context,"recibidooooooo",Toast.LENGTH_SHORT);

    }

}
