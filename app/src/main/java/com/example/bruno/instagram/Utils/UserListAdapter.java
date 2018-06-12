package com.example.bruno.instagram.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bruno.instagram.Profile.ProfileActivity;
import com.example.bruno.instagram.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import com.example.bruno.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by User on 9/17/2017.
 */

public class UserListAdapter extends ArrayAdapter<User>{

    private static final String TAG = "UserListAdapter";


    private LayoutInflater mInflater;
    private List<User> mUsers = null;
    private int layoutResource;
    private Context mContext;

    public UserListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
    }

    private static class ViewHolder{
        TextView username, email;
        CircleImageView profileImage;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.username.setText(getItem(position).getUsername());
        holder.email.setText(getItem(position).getFullname());


        final ImageLoader imageLoader = ImageLoader.getInstance();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",holder.username.getText());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    final User user = new User(objects.get(0).get("fullname").toString(),objects.get(0).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " + holder.username.getText());
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), user);
                            mContext.startActivity(intent);
                        }
                    });
                    try{
                        ParseFile parseFile = (ParseFile) objects.get(0).get("profilephoto");
                        imageLoader.displayImage(parseFile.getUrl(),holder.profileImage);
                        holder.profileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "onClick: navigating to profile of: " +holder.username.getText());
                                Intent intent = new Intent(mContext, ProfileActivity.class);
                                intent.putExtra(mContext.getString(R.string.calling_activity),mContext.getString(R.string.home_activity));
                                intent.putExtra(mContext.getString(R.string.intent_user), user);
                                mContext.startActivity(intent);
                            }
                        });
                    }catch (NullPointerException er){
                        String url="https://vignette.wikia.nocookie.net/creepypasta/images/4/44/Android-Logo.jpg/revision/latest?cb=20121003030357";
                        imageLoader.displayImage(url,holder.profileImage);
                        holder.profileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "onClick: navigating to profile of: " +holder.username);
                                Intent intent = new Intent(mContext, ProfileActivity.class);
                                intent.putExtra(mContext.getString(R.string.calling_activity),mContext.getString(R.string.home_activity));
                                intent.putExtra(mContext.getString(R.string.intent_user), user);
                                mContext.startActivity(intent);
                            }
                        });
                    }

                }else{
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }
}

























