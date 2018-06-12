package com.example.bruno.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by bruno on 12/06/2018.
 */

public class ParsePushBroadcast extends ParsePushBroadcastReceiver {
    public ParsePushBroadcast() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Toast.makeText(context,"1",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        Toast.makeText(context,"2",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
        Toast.makeText(context,"3",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        Toast.makeText(context,"3",Toast.LENGTH_SHORT);
    }

}
