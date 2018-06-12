package com.example.bruno.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bruno.instagram.Profile.ProfileActivity;
import com.example.bruno.instagram.R;
import com.example.bruno.instagram.models.Photo;
import com.example.bruno.instagram.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.security.PolicySpi;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by User on 9/22/2017.
 */

public class MainfeedListAdapter extends ArrayAdapter<Photo> {

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final String TAG = "MainFeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private String currentUsername = "";

    public MainfeedListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;

//        for(Photo photo: objects){
//            Log.d(TAG, "MainFeedListAdapter: photo id: " + photo.getPhoto_id());
//        }
    }

    static class ViewHolder{
        CircleImageView mprofileImage;
        TextView username, timeDetla, caption;
        SquareImageView image;
        User user  = new User();
        StringBuilder users;
        Photo photo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.image = (SquareImageView) convertView.findViewById(R.id.post_image);
            holder.caption = (TextView) convertView.findViewById(R.id.image_caption);
            holder.timeDetla = (TextView) convertView.findViewById(R.id.image_time_posted);
            holder.mprofileImage = (CircleImageView) convertView.findViewById(R.id.profile_photo);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.photo = getItem(position);
        holder.users = new StringBuilder();

        //set the caption
        holder.caption.setText(getItem(position).getCaption());


        //set the time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            if(timestampDifference.equals("1")){
                holder.timeDetla.setText("HACE "+timestampDifference+" DÍA");
            }else{
                holder.timeDetla.setText("HACE "+timestampDifference+" DÍAS");
            }

        }else{
            holder.timeDetla.setText("HOY");
        }

        //set the profile image
        initImageLoader();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(), holder.image);



        //get the user object
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",getItem(position).getUser_id());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    User user = new User(objects.get(0).get("fullname").toString(),objects.get(0).getUsername());
                    holder.user = user;
                    holder.username.setText(objects.get(0).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " + holder.user.getUsername());
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(intent);
                        }
                    });
                    try{
                        ParseFile parseFile = (ParseFile) objects.get(0).get("profilephoto");
                        imageLoader.displayImage(parseFile.getUrl(),holder.mprofileImage);
                        holder.mprofileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "onClick: navigating to profile of: " +holder.user.getUsername());
                                Intent intent = new Intent(mContext, ProfileActivity.class);
                                intent.putExtra(mContext.getString(R.string.calling_activity),mContext.getString(R.string.home_activity));
                                intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                                mContext.startActivity(intent);
                            }
                        });
                    }catch (NullPointerException er){
                        String url="https://vignette.wikia.nocookie.net/creepypasta/images/4/44/Android-Logo.jpg/revision/latest?cb=20121003030357";
                        imageLoader.displayImage(url,holder.mprofileImage);
                        holder.mprofileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "onClick: navigating to profile of: " +holder.user.getUsername());
                                Intent intent = new Intent(mContext, ProfileActivity.class);
                                intent.putExtra(mContext.getString(R.string.calling_activity),mContext.getString(R.string.home_activity));
                                intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                                mContext.startActivity(intent);
                            }
                        });
                    }

                }else{
                    e.printStackTrace();
                }
            }
        });
        if(reachedEndOfList(position)){
            loadMoreData();
        }

        return convertView;
    }

    private boolean reachedEndOfList(int position){
        return position == getCount() - 1;
    }

    private void loadMoreData(){

        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */

    private String getTimestampDifference(Photo photo){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_created();

        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

}





























