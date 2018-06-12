package com.example.bruno.instagram.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bruno.instagram.Home.HomeActivity;
import com.example.bruno.instagram.R;
import com.example.bruno.instagram.Utils.ImageManager;
import com.example.bruno.instagram.Utils.UniversalImageLoader;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Created by User on 7/24/2017.
 */

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";


    //widgets
    private EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mCaption = (EditText) findViewById(R.id.caption) ;
        mContext = this;


        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });


        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to AWS
                Toast.makeText(NextActivity.this, "Compartiendo foto", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();

                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));

                    bitmap = ImageManager.getBitmap(imgUrl);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    byte[] byteArray = stream.toByteArray();
                    ParseFile parseFile = new ParseFile("image.png", byteArray);
                    ParseObject object = new ParseObject("Image");
                    object.put("image", parseFile);
                    object.put("username", ParseUser.getCurrentUser().getUsername());
                    object.put("descripcion",caption);
                    Log.d(TAG, "onClick:"+ ParseUser.getCurrentUser().getUsername());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(NextActivity.this, "Image saved!", Toast.LENGTH_SHORT);
                            } else {
                                Toast.makeText(NextActivity.this, "Image could not be saved. Please try again later", Toast.LENGTH_SHORT);
                            }
                        }
                    });

                    ParsePush push = new ParsePush();
                    push.setChannel("ImgageUpload");
                    push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
                    push.sendInBackground();

                }else if(intent.hasExtra(getString(R.string.selected_bitmap))){

                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    byte[] byteArray = stream.toByteArray();
                    ParseFile parseFile = new ParseFile("image.png", byteArray);
                    ParseObject object = new ParseObject("Image");
                    object.put("image", parseFile);
                    object.put("username", ParseUser.getCurrentUser().getUsername());
                    object.put("descripcion",caption);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(NextActivity.this, "Image saved!", Toast.LENGTH_SHORT);
                            } else {
                                Toast.makeText(NextActivity.this, "Image could not be saved. Please try again later", Toast.LENGTH_SHORT);
                            }
                        }
                    });


                }

                Intent intent = new Intent(mContext, HomeActivity.class);
                mContext.startActivity(intent);

            }
        });

        setImage();
    }

    private void someMethod(){
        /*
            Step 1)
            Create a data model for Photos

            Step 2)
            Add properties to the Photo Objects (caption, date, imageUrl, photo_id, tags, user_id)

            Step 3)
            Count the number of photos that the user already has.

            Step 4)
            a) Upload the photo to Firebase Storage
            b) insert into 'photos' node
            c) insert into 'user_photos' node

         */

    }


    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
        }
    }

}
