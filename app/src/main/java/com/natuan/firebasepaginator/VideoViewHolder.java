package com.natuan.firebasepaginator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by natuan on 16/12/11.
 */

public class VideoViewHolder extends RecyclerView.ViewHolder{
    public TextView tvName;
    public VideoViewHolder(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
    }
}
