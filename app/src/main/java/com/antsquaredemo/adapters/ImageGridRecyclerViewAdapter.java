package com.antsquaredemo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.antsquaredemo.R;
import com.antsquaredemo.viewHolders.RecyclerViewHolders;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by patel on 12/2/2016.
 */

public class ImageGridRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders>  {

    private Context context;
    private ArrayList<String> imageURLsArrayList = new ArrayList<>();

    private ImageLoader imageLoader;

    public ImageGridRecyclerViewAdapter(Context context, ArrayList<String> imageURLsArrayList) {
        this.context = context;
        this.imageURLsArrayList = imageURLsArrayList;

        imageLoader = ImageLoader.getInstance(); // Get singleton instance*
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_row_layout, null);

        int imageDimension = context.getResources().getDisplayMetrics().widthPixels/3;

        layoutView.setLayoutParams(new LinearLayout.LayoutParams(imageDimension,imageDimension));
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        // load image here
        imageLoader.displayImage(imageURLsArrayList.get(position), holder.gridImageView);
    }

    @Override
    public int getItemCount() {
        return this.imageURLsArrayList.size();
    }
}
