package com.example.bruno.instagram.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bruno.instagram.Home.HomeActivity;
import com.example.bruno.instagram.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";


    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mPassword,mUsername;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPassword = (EditText) findViewById(R.id.input_password);
        mUsername =(EditText) findViewById(R.id.input_username);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        mProgressBar.setVisibility(View.GONE);

        init();

    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

     /*
    ------------------------------------ ServerParse ---------------------------------------------
     */

     private void init(){

         //initialize the button for logging in
         Button btnLogin = (Button) findViewById(R.id.btn_login);
         btnLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d(TAG, "onClick: attempting to log in.");

                 String password = mPassword.getText().toString().trim();
                 String username= mUsername.getText().toString().trim();
                 if(isStringNull(username) && isStringNull(password)){
                     Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                 }else{
                     mProgressBar.setVisibility(View.VISIBLE);

                     ParseUser.logInInBackground(username, password, new LogInCallback() {
                         public void done(ParseUser user, ParseException e) {
                             if (user != null) {//If the user is logged in then navigate to HomeActivity and call 'finish()'
                                 Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                 startActivity(intent);
                                 finish();
                             } else {
                                 // Signup failed. Look at the ParseException to see what happened.
                                 Log.w(TAG, "signInWithEmail:failed", e.getCause());

                                 Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),Toast.LENGTH_SHORT).show();
                                 mProgressBar.setVisibility(View.GONE);
                             }
                         }
                     });

                 }

             }
         });

         TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
         linkSignUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d(TAG, "onClick: navigating to register screen");
                 Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                 startActivity(intent);
             }
         });

          /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
         if(ParseUser.getCurrentUser() != null){
             Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
             startActivity(intent);
             finish();
         }
     }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
























