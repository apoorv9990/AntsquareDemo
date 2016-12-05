package com.antsquaredemo.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.antsquaredemo.R;

/**
 * Created by patel on 12/2/2016.
 */

public class RecyclerViewHolders extends RecyclerView.ViewHolder {

    public ImageView gridImageView;

    public RecyclerViewHolders(View itemView) {
        super(itemView);

        gridImageView = (ImageView) itemView.findViewById(R.id.grid_imageview);
    }
}
