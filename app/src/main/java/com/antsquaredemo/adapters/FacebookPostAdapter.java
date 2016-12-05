package com.antsquaredemo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.antsquaredemo.R;
import com.antsquaredemo.models.FacebookPost;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by patel on 12/4/2016.
 */

public class FacebookPostAdapter extends RecyclerView.Adapter<FacebookPostAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<FacebookPost> posts;

    private ImageLoader imageLoader;

    private DateFormat facebookDateFormat;
    private DateFormat showDateFormat;

    public FacebookPostAdapter(Context context, List<FacebookPost> objects) {
        mContext = context;
        posts = (ArrayList<FacebookPost>) objects;

        imageLoader = ImageLoader.getInstance(); // Get singleton instance*

        facebookDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        showDateFormat = new SimpleDateFormat("MMMM dd,yyyy");

        facebookDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        showDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.facebook_post_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            // Get the data model based on position
            FacebookPost post = posts.get(position);

            // Set item views based on your views and data model
            holder.storyTextView.setText(post.getStory());
            holder.dateTextView.setText(showDateFormat.format(facebookDateFormat.parse(post.getCreated_time())));
            if (TextUtils.isEmpty(post.getMessage()))
                holder.messageTextView.setVisibility(View.GONE);
            else
                holder.messageTextView.setVisibility(View.VISIBLE);

            holder.messageTextView.setText(post.getMessage());

            if (TextUtils.isEmpty(post.getImageURL()))
                holder.postImageView.setVisibility(View.GONE);
            else {
                holder.postImageView.setVisibility(View.VISIBLE);
                imageLoader.displayImage(post.getImageURL(), holder.postImageView);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView storyTextView;
        public TextView dateTextView;
        public TextView messageTextView;
        public ImageView postImageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            storyTextView = (TextView) itemView.findViewById(R.id.post_story_textview);
            dateTextView = (TextView) itemView.findViewById(R.id.post_time_textview);
            messageTextView = (TextView) itemView.findViewById(R.id.post_message_textview);
            postImageView = (ImageView) itemView.findViewById(R.id.post_imageview);
        }
    }
}
