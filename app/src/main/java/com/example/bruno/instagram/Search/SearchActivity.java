package com.example.bruno.instagram.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.bruno.instagram.Login.LoginActivity;
import com.example.bruno.instagram.Profile.ProfileActivity;
import com.example.bruno.instagram.R;
import com.example.bruno.instagram.Utils.BottomNavigationViewHelper;
import com.example.bruno.instagram.Utils.UserListAdapter;
import com.example.bruno.instagram.models.User;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by bruno on 20/04/2018.
 */

public class SearchActivity extends AppCompatActivity{
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM=1;
    private Context mContext = SearchActivity.this;

    //widgets
    private EditText mSearchParam;
    private ListView mListView;


    //vars
    private List<User> mUserList;
    private UserListAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mListView = (ListView) findViewById(R.id.listView);
        mSearchParam = (EditText) findViewById(R.id.search);

        Log.d(TAG, "onCreate: started.");

        setupBottonNavigationView();
        ParseUser currentUser = ParseUser.getCurrentUser();
        checkCurrentUser(currentUser);
        hideSoftKeyboard();

        initTextListener();
    }
    /**
     * Configuarar menú de navegación
     */
    private void setupBottonNavigationView(){
        Log.d(TAG,"setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(SearchActivity.this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

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
    private void hideSoftKeyboard(){
        if(getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();
        listUsers();
        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mSearchParam.getText().toString().trim().equals("")){
                    listUsers();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = mSearchParam.getText().toString();
                searchForMatch(text);
            }
        });
    }
    private void searchForMatch(final String keyword){
        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        //update the users list view
        if(keyword.length() ==0){

        }else{
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username",keyword);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, com.parse.ParseException e) {
                    if (e == null) {
                        if(objects.size()>0){
                            for(ParseUser object: objects){
                                User user= new User(object.get("fullname").toString(),object.getUsername().toString());
                                mUserList.add(user);
                                updateUsersList();
                            }
                        }

                    }else{
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private void listUsers(){
        Log.d(TAG, "listUsers: listando usuarios: ");
        mUserList.clear();
        //update the users list view
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    if(objects.size()>0) {
                        for (ParseUser object : objects) {
                            User user = new User(object.get("fullname").toString(), object.getUsername().toString());
                            mUserList.add(user);
                            updateUsersList();
                        }
                    }

                }else{
                    e.printStackTrace();
                }
            }
        });

    }
    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");

        mAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());

                //navigate to profile activity
                Intent intent =  new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user), mUserList.get(position));
                startActivity(intent);
            }
        });
    }
}
