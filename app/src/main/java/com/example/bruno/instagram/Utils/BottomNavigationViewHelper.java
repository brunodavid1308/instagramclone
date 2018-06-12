package com.example.bruno.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.bruno.instagram.Home.HomeActivity;
import com.example.bruno.instagram.Profile.ProfileActivity;
import com.example.bruno.instagram.R;
import com.example.bruno.instagram.Search.SearchActivity;
import com.example.bruno.instagram.Share.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by bruno on 20/04/2018.
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG,"setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_house:
                        Intent inten1 = new Intent(context, HomeActivity.class);
                        context.startActivity(inten1);
                        break;
                    case R.id.ic_search:
                        Intent inten2 = new Intent(context, SearchActivity.class);
                        context.startActivity(inten2);
                        break;
                    case R.id.ic_circle:
                        Intent inten3 = new Intent(context, ShareActivity.class);
                        context.startActivity(inten3);
                        break;
                    case R.id.ic_android:
                        Intent inten5 = new Intent(context, ProfileActivity.class);
                        context.startActivity(inten5);
                        break;

                }


                return false;
            }
        });
    }
}
