package com.example.bruno.instagram.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bruno.instagram.R;

import com.example.bruno.instagram.Share.NextActivity;
import com.example.bruno.instagram.Utils.ImageManager;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private String email, username, password,fullname;
    private EditText mEmail, mPassword, mUsername,mFullname;
    private Button btnRegister;
    private ProgressBar mProgressBar;


    private String append = "";
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;

        Log.d(TAG, "onCreate: started.");

        initWidgets();
        init();
    }

    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                username = mUsername.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                fullname= mFullname.getText().toString().trim();

                if(checkInputs(email, username, password,fullname)){
                    mProgressBar.setVisibility(View.VISIBLE);

                    //Creamos un nuevo usuario Parse
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.put("fullname",fullname);

                    //Llamamos al metodo de registro Parse
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            if(e != null){
                                //Mostramos el mensaje the error
                                switch (e.getCode()) {
                                    case ParseException.USERNAME_TAKEN: {
                                        Toast.makeText(mContext,"Nombre de usuario no disponible ",Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    case ParseException.EMAIL_TAKEN: {
                                        Toast.makeText(mContext,"Email ya registrado",Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    default: {
                                        Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }

                            }else{
                                Toast.makeText(mContext,"Usuario Registrado",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password, String fullname){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(email.equals("") || username.equals("") || password.equals("")|| fullname.equals("")){
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    /**
     * Initialize the activity widgets
     */
    private void initWidgets(){
        Log.d(TAG, "initWidgets: Initializing Widgets.");
        mEmail = (EditText) findViewById(R.id.input_email);
        mUsername = (EditText) findViewById(R.id.input_username);
        btnRegister = (Button) findViewById(R.id.btn_register);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPassword = (EditText) findViewById(R.id.input_password);
        mFullname = (EditText)findViewById(R.id.input_fullname);
        mContext = RegisterActivity.this;
        mProgressBar.setVisibility(View.GONE);

    }


}
