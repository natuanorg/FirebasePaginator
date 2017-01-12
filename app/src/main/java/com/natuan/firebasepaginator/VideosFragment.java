package com.natuan.firebasepaginator;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by natuan on 16/12/11.
 */

public class VideosFragment extends Fragment implements View.OnClickListener, ValueEventListener {

    private RecyclerView rvVideos;
    private ProgressBar prbLoading;
    private TextView tvMessage;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    private InfiniteFirebaseRecyclerAdapter<Video, VideoViewHolder> mAdapter;
    private Query videoQuery;
    private int mPageLimit = 10;

    public static VideosFragment newInstance() {
        Bundle args = new Bundle();
        VideosFragment fragment = new VideosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        prbLoading = (ProgressBar) view.findViewById(R.id.prbLoading);
        rvVideos = (RecyclerView) view.findViewById(R.id.rvVideos);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        rvVideos.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvVideos.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        videoQuery = mDatabase.child("videos");
        videoQuery.addValueEventListener(this);
        mAdapter = new InfiniteFirebaseRecyclerAdapter<Video, VideoViewHolder>(Video.class, R.layout.item_video, VideoViewHolder.class, videoQuery, mPageLimit) {
            @Override
            protected void populateViewHolder(VideoViewHolder viewHolder, Video model, int position) {
                Logger.enter();
                if (model != null) {
                    viewHolder.tvName.setText(model.name);
                }
                Logger.exit();
            }
        };
        rvVideos.setAdapter(mAdapter);
        rvVideos.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Logger.enter();
                Toast.makeText(getActivity(), "Load More",
                        Toast.LENGTH_SHORT).show();
                mAdapter.more();
                Logger.exit();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Logger.enter();
        prbLoading.setVisibility(View.GONE);
        rvVideos.setVisibility(View.VISIBLE);
        Logger.exit();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Logger.enter();
        Logger.exit();
    }
}
