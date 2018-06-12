package com.example.bruno.instagram.Home;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.example.bruno.instagram.Login.LoginActivity;
import com.example.bruno.instagram.R;
import com.example.bruno.instagram.Utils.BottomNavigationViewHelper;
import com.example.bruno.instagram.Utils.MainfeedListAdapter;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM=0;
    private Context mContext = HomeActivity.this;
    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG,"onCreate: starting");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        setupBottonNavigationView();
        setupViewPager();

        ParseUser currentUser = ParseUser.getCurrentUser();
        checkCurrentUser(currentUser);
        ArrayList<String> channels = new ArrayList<>();
        channels.add("Notis");
        ParsePush.subscribeInBackground(channels.get(0));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.getPhotos();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.getPhotos();
        }
    }

    /**
     * Crea y muestra las diferentes ventanas: Cámara, Home, Mensajes
     */
    private void setupViewPager(){
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        sectionsPagerAdapter.addFragment(new HomeFragment());//index 1
        mViewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);

    }
    /**
     * Configuarar menú de navegación
     */
        private void setupBottonNavigationView(){
            Log.d(TAG,"setupBottomNavigationView: setting up BottomNavigationView");
            BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
            BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
            BottomNavigationViewHelper.enableNavigation(HomeActivity.this,bottomNavigationViewEx);
            Menu menu = bottomNavigationViewEx.getMenu();
            MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
            menuItem.setChecked(true);
    }


    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(ParseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkCurrentUser: User looged in"+user.getUsername());
        }
    }


}
