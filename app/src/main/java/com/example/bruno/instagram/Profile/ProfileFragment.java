package com.example.bruno.instagram.Profile;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bruno.instagram.Home.HomeActivity;
import com.example.bruno.instagram.Login.LoginActivity;
import com.example.bruno.instagram.R;

import com.example.bruno.instagram.Share.ShareActivity;
import com.example.bruno.instagram.Utils.BottomNavigationViewHelper;
import com.example.bruno.instagram.Utils.GridImageAdapter;
import com.example.bruno.instagram.Utils.UniversalImageLoader;
import com.example.bruno.instagram.models.Photo;
import com.example.bruno.instagram.models.User;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 6/29/2017.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY_NUM = 3;
    private static final int NUM_GRID_COLUMNS = 3;



    //widgets
    private TextView mPosts, mDisplayName, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private ImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;


    //vars
    private int mPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);

        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();

        Log.d(TAG, "onCreateView: stared.");

        setupBottonNavigationView();
        setupToolbar();

        ParseUser currentUser = ParseUser.getCurrentUser();
        checkCurrentUser(currentUser);
        initImageLoader();
        setupImageGrid();
        setupProfileData();
        setProfilePhoto();
        mProgressBar.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }
    private void setupImageGrid(){
        final ArrayList<Photo> photos = new ArrayList<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("username", currentUser.getUsername());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null) {
                    for(ParseObject object: objects){
                        //Log.i("info","image found");
                        ParseFile imageFile = (ParseFile)object.get("image");

                        Photo photo = new Photo();

                        try {
                            photo.setCaption(object.get("descripcion").toString());
                            //photo.setPhoto_id(object.get("objectId").toString());
                            photo.setUser_id(object.get("username").toString());
                            photo.setDate_created(object.getCreatedAt().toString());
                            photo.setImage_path(imageFile.getUrl());
                            //Log.i("kj",photo.getImage_path());
                            photos.add(photo);
                        }catch(NullPointerException j){
                            Log.e(TAG, "onDataChange: NullPointerException: " + j.getMessage() );
                        }
                        mPostsCount=objects.size();
                    }
                    mPosts.setText(String.valueOf(mPostsCount));
                    int gridWidth = getResources().getDisplayMetrics().widthPixels;
                    int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                    gridView.setColumnWidth(imageWidth);

                    ArrayList<String> imgUrls = new ArrayList<String>();
                    for(int i = 0; i < photos.size(); i++){
                        imgUrls.add(photos.get(i).getImage_path());
                        Log.i("info",imgUrls.get(i));
                    }
                    GridImageAdapter adapter = new GridImageAdapter(mContext,R.layout.layout_grid_imageview,"",imgUrls);
                    gridView.setAdapter(adapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mOnGridImageSelectedListener.onGridImageSelected(photos.get(position),ACTIVITY_NUM);
                        }
                    });

                }
            }
        });



    }
    private void setupProfileData(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    if(objects.size()>0){
                        mDisplayName.setText(objects.get(0).get("fullname").toString());
                        mUsername.setText(objects.get(0).getUsername());
                        try{
                            mDescription.setText(objects.get(0).get("description").toString());
                        }catch (NullPointerException e2){
                            e2.printStackTrace();
                        }

                    }

                }else{
                    e.printStackTrace();
                }
            }
        });
    }
    private void setProfilePhoto(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    ParseFile imageFile = (ParseFile)objects.get(0).get("profilephoto");
                    if(imageFile!=null){
                        UniversalImageLoader.setImage(imageFile.getUrl(),mProfilePhoto,null,"");
                    }else {
                        String url="https://vignette.wikia.nocookie.net/creepypasta/images/4/44/Android-Logo.jpg/revision/latest?cb=20121003030357";
                        UniversalImageLoader.setImage(url,mProfilePhoto,null,"");
                }


                }else{
                    e.printStackTrace();
                }
            }
        });
    }
    private void setupToolbar(){

        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        ParseUser.logOut();
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);

                    case R.id.action_upload:
                        Log.i("ActionBar", "upload!");
                        Intent intent3 = new Intent(mContext,ShareActivity.class);
                        startActivity(intent3);
                        return true;
                    case R.id.action_settings:
                        Log.d(TAG, "onClick: lañksdñalsdkñas");
                        Intent intent2 = new Intent(mContext,EditProfileActivity.class);
                        startActivity(intent2);
                }
                return false;
            }
        });
    }
    /**
    * Configuarar menú de navegación
     */
    private void setupBottonNavigationView(){
        Log.d(TAG,"setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /** checks to see if the @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(ParseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            ((ProfileActivity)getActivity()).finish();
        }else{
            Log.d(TAG, "checkCurrentUser: User looged in"+user.getUsername());
        }
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader((ProfileActivity)getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

}
