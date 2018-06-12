package com.example.bruno.instagram.Utils;

import android.app.Application;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by bruno on 21/04/2018.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String idtoken=FirebaseInstanceId.getInstance().getId();
        Log.d("asd",idtoken);
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("92fe4c26e6196e26be482503a1c75ee539f34113")
                .clientKey("fbecb3f1dfe9cf79fb9a7d48c3b30cfb5393732d")
                .server("http://34.243.36.28:80/parse/")
                .build()
        );
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "764730200387");
        installation.saveInBackground();
    }
}
