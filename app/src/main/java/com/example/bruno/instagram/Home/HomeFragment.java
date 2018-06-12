package com.example.bruno.instagram.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.eschao.android.widget.elasticlistview.LoadFooter;
import com.eschao.android.widget.elasticlistview.OnLoadListener;
import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.example.bruno.instagram.R;
import com.example.bruno.instagram.Utils.GridImageAdapter;
import com.example.bruno.instagram.Utils.MainfeedListAdapter;
import com.example.bruno.instagram.models.Photo;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruno on 20/04/2018.
 */

public class HomeFragment extends Fragment implements OnUpdateListener, OnLoadListener{

    private static final String TAG = "HomeFragment";


    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;

    private int recursionIterator = 0;
    private ElasticListView mListView;
    private MainfeedListAdapter adapter;
    private int resultsCount = 0;
    private JSONArray mMasterStoriesArray;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home, container ,false);
        mListView = (ElasticListView) view.findViewById(R.id.listView);

        initListViewRefresh();
        clearAll();
        getPhotos();
        return view;
    }

    public void getPhotos(){
        Log.d(TAG, "getPhotos: getting list of photos");

        mPhotos = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
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
                            mPhotos.add(photo);
                        }catch(NullPointerException j){
                            Log.e(TAG, "onDataChange: NullPointerException: " + j.getMessage() );
                        }
                    }
                    displayPhotos();
                }
            }
        });
    }

    public void displayPhotos(){
//
        if(mPhotos != null){

            try{

                //sort for newest to oldest
                /*Collections.sort(mPhotos, new Comparator<Photo>() {
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });*/

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                int iterations = mPhotos.size();
                if(iterations > 10){
                    iterations = 10;
                }
//
                resultsCount = 0;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                    resultsCount++;
                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getPhoto_id());
                }

                adapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(adapter);

                // Notify update is done
                mListView.notifyUpdated();

            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
            }
        }
    }
    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > resultsCount && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                resultsCount = resultsCount + iterations;
                adapter.notifyDataSetChanged();
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }
    private void clearAll(){

        if(mPhotos != null){
            mPhotos.clear();
            if(adapter != null){
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        }
        if(mPaginatedPhotos != null){
            mPaginatedPhotos.clear();
        }
        if(mRecyclerView != null){
            mRecyclerView.setAdapter(null);
        }
        mPhotos = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();
    }

    private void initListViewRefresh(){
        mListView.setHorizontalFadingEdgeEnabled(true);
        mListView.setAdapter(adapter);
        mListView.enableLoadFooter(true)
                .getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
        mListView.setOnUpdateListener(this)
                .setOnLoadListener(this);
//        mListView.requestUpdate();
    }

    @Override
    public void onUpdate() {
        clearAll();
        getPhotos();
    }

    @Override
    public void onLoad() {
        mListView.notifyLoaded();
    }
}
