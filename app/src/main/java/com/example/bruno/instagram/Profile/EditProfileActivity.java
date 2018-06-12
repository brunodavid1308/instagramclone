package com.example.bruno.instagram.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bruno.instagram.R;
import com.example.bruno.instagram.Share.NextActivity;
import com.example.bruno.instagram.Share.ShareActivity;
import com.example.bruno.instagram.Utils.GridImageAdapter;
import com.example.bruno.instagram.Utils.ImageManager;
import com.example.bruno.instagram.Utils.UniversalImageLoader;
import com.example.bruno.instagram.models.Photo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.bitmap;
import static com.example.bruno.instagram.R.id.caption;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileFragment";



    //EditProfile Fragment widgets
    private EditText mDisplayName, mDescription, mPhoneNumber;
    private TextView mChangeProfilePhoto,mUsername,mEmail;
    private ImageView mProfilePhoto;


    //vars
    String currentUserID;
    ParseUser currentUser;
    Context mcontContext;
    private Bitmap bitmap;
    private String mAppend = "file:/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editprofile);

        mProfilePhoto = (ImageView) findViewById(R.id.profile_photo);
        mDisplayName = (EditText) findViewById(R.id.display_name);
        mUsername = (TextView) findViewById(R.id.username);
        mDescription = (EditText) findViewById(R.id.description);
        mEmail = (TextView)findViewById(R.id.email);
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) findViewById(R.id.changeProfilePhoto);
        currentUserID = ParseUser.getCurrentUser().getObjectId();
        currentUser = ParseUser.getCurrentUser();
        mcontContext=this;
        setProfileWidgets();
        initImageLoader();
        getIncomingIntent();
        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                finish();
            }
        });

        ImageView checkmark = (ImageView) findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setContentView(R.layout.fragment_editprofile);

        mProfilePhoto = (ImageView) findViewById(R.id.profile_photo);
        mDisplayName = (EditText) findViewById(R.id.display_name);
        mUsername = (TextView) findViewById(R.id.username);
        mDescription = (EditText) findViewById(R.id.description);
        mEmail = (TextView)findViewById(R.id.email);
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) findViewById(R.id.changeProfilePhoto);
        currentUserID = ParseUser.getCurrentUser().getObjectId();
        currentUser = ParseUser.getCurrentUser();
        mcontContext=this;
        setProfileWidgets();
        initImageLoader();
        getIncomingIntent();
        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                finish();
            }
        });

        ImageView checkmark = (ImageView) findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
            }
        });
    }


    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before donig so it chekcs to make sure the username chosen is unqiue
     */
    private void saveProfileSettings(){

        String displayName = mDisplayName.getText().toString();
        String description = mDescription.getText().toString();
        long phoneNumber=0;
        try {
            phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());
        }catch (NumberFormatException e){

        }
        /**
         * change the rest of the settings that do not require uniqueness
         */
        if(!currentUser.get("fullname").equals(displayName)&&(!displayName.isEmpty())){
            //update displayname
            currentUser.put("fullname",displayName);

        }

        if(!description.isEmpty()){
            //update description
            currentUser.put("description",description);

        }


        if((phoneNumber!=0)){
            //update phoneNumber
            currentUser.put("phonenumber",phoneNumber);

        }
        currentUser.saveInBackground();
        finish();
    }


    private void setProfileWidgets(){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + currentUser.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + currentUser.getEmail());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + currentUser.getUsername());

        //User user = userSettings.getUser();

        //UniversalImageLoader.setImage(currentUser.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(currentUser.get("fullname").toString());
        mUsername.setText(currentUser.getUsername());
        mEmail.setText(currentUser.getEmail());
        if(currentUser.get("phonenumber")!=null){
            mPhoneNumber.setText(String.valueOf(currentUser.get("phonenumber")));
        }else{
        }
        if(currentUser.get("description")!=null){
            mDescription.setText(currentUser.get("description").toString());
        }
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(mcontContext, ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                startActivity(intent);
                finish();
            }
        });
        try {
            ParseFile parseFile=(ParseFile) currentUser.get("profilephoto");
            UniversalImageLoader.setImage(parseFile.getUrl(),mProfilePhoto,null,"");
        }catch (NullPointerException e){
            String url="https://vignette.wikia.nocookie.net/creepypasta/images/4/44/Android-Logo.jpg/revision/latest?cb=20121003030357";
            UniversalImageLoader.setImage(url,mProfilePhoto,null,"");
        }

    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))){

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");
            if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))){

                if(intent.hasExtra(getString(R.string.selected_image))){
                    //set the new profile picture
                    String imgUrl = intent.getStringExtra(getString(R.string.selected_image));

                    bitmap = ImageManager.getBitmap(imgUrl);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    byte[] byteArray = stream.toByteArray();
                    ParseFile parseFile = new ParseFile("image.png", byteArray);
                    currentUser.put("profilephoto",parseFile);
                    currentUser.saveInBackground();
                    UniversalImageLoader.setImage(imgUrl, mProfilePhoto, null, mAppend);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    //set the new profile picture
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    byte[] byteArray = stream.toByteArray();
                    ParseFile parseFile = new ParseFile("image.png", byteArray);
                    currentUser.put("profilephoto",parseFile);
                    currentUser.saveInBackground();
                    mProfilePhoto.setImageBitmap(bitmap);

                }

            }

        }

    }
}
