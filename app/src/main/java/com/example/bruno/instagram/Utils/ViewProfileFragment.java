package com.example.bruno.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.bruno.instagram.Login.LoginActivity;
import com.example.bruno.instagram.Profile.ProfileActivity;
import com.example.bruno.instagram.R;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 6/29/2017.
 */

public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;

    private static final int ACTIVITY_NUM = 3;
    private static final int NUM_GRID_COLUMNS = 3;



    //widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;
    private ImageView mBackArrow;


    //vars
    private User mUser;
    private int mPostsCount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        mDisplayName = (TextView) view.findViewById(R.id.display_name);
        mUsername = (TextView) view.findViewById(R.id.username);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        gridView = (GridView) view.findViewById(R.id.gridView);
        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);

        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();

        Log.d(TAG, "onCreateView: stared.");

        try{
            mUser = getUserFromBundle();
            init();
            setupProfileData();
        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: "  + e.getMessage() );
            Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }

        setupBottonNavigationView();
        //setupToolbar();

        ParseUser currentUser = ParseUser.getCurrentUser();
        checkCurrentUser(currentUser);
        initImageLoader();
        //setupImageGrid();
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

    private User getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.intent_user));
        }else{
            return null;
        }
    }

    private void init(){
        final ArrayList<Photo> photos = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
        query.whereEqualTo("username", mUser.getUsername());
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
                        mPostsCount++;
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

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });


    }
    private void setupProfileData(){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",mUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {

                    mDisplayName.setText(objects.get(0).get("fullname").toString());
                    mUsername.setText(objects.get(0).getUsername().toString());
                    ParseFile imageFile = (ParseFile)objects.get(0).get("profilephoto");
                    if(imageFile!=null){
                        UniversalImageLoader.setImage(imageFile.getUrl(),mProfilePhoto,null,"");
                    }else {
                        String url = "https://vignette.wikia.nocookie.net/creepypasta/images/4/44/Android-Logo.jpg/revision/latest?cb=20121003030357";
                        UniversalImageLoader.setImage(url, mProfilePhoto, null, "");
                    }
                }else{
                    e.printStackTrace();
                }
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
