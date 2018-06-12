package com.example.bruno.instagram.Utils;

import android.os.Bundle;
import android.widget.Toast;

import com.parse.gcm.ParseGCMListenerService;

/**
 * Created by bruno on 12/06/2018.
 */

public class Reciever extends ParseGCMListenerService {
    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);
        Toast.makeText(this,s,Toast.LENGTH_SHORT);
    }
}
