package com.example.bruno.instagram.Utils;

import android.content.Intent;

import com.parse.gcm.ParseGCMInstanceIDListenerService;

/**
 * Created by bruno on 12/06/2018.
 */

public class Listenerservice extends ParseGCMInstanceIDListenerService {
    public Listenerservice() {
        super();
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

    }
}
