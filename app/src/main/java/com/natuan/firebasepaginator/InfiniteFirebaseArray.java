package com.natuan.firebasepaginator;

import android.text.TextUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by natuan on 17/01/07.
 */

public class InfiniteFirebaseArray implements ChildEventListener {

    public interface OnChangedListener {
        enum EventType {ADDED, CHANGED, REMOVED}
        void onChanged(EventType type, int index, int oldIndex);
        void onCancelled(DatabaseError databaseError);
    }

    private Query mQuery;
    private List<DataSnapshot> mSnapshots = new ArrayList<>();
    private int mIndex = -1;
    private String mNextChildKey = null;
    private int mNumberPerPage;
    private int mCount;
    private boolean isDuplicateKey;
    private OnChangedListener mListener;

    public InfiniteFirebaseArray(Query ref, int numberPerPage) {
        mNumberPerPage = numberPerPage;
        initQuery(ref);
    }

    private void initQuery(Query ref) {
        Logger.d("LastChildKey: " + mNextChildKey);
        Logger.d("NumberPerPage: " + mNumberPerPage);
        mQuery = ref.limitToFirst(mNumberPerPage + 1).startAt(mNextChildKey);
        mQuery.addChildEventListener(this);
        mCount = 0;
    }

    private void initNextQuery(Query ref) {
        Logger.d("LastChildKey: " + mNextChildKey);
        Logger.d("NumberPerPage: " + mNumberPerPage);
        ref.orderByKey().limitToFirst(mNumberPerPage + 1)
                .startAt(mNextChildKey).addChildEventListener(this);
        mCount = 0;
    }

    public void cleanup() {
        Logger.enter();
        mQuery.removeEventListener(this);
        Logger.exit();
    }

    public void more(Query ref) {
        if (isHasMore()) {
            initNextQuery(ref);
        }
    }

    public int getCount() {
        return mSnapshots.size();
    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        if (snapshot == null) {
            return;
        }
        mCount++;
        mNextChildKey = snapshot.getKey();
        if (mCount > mNumberPerPage) {
            return;
        }
        if (checkDuplicateKey(mNextChildKey)) {
            isDuplicateKey = true;
            return;
        }
        mSnapshots.add(++mIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.ADDED, mIndex);
        Logger.d(mIndex + " : " + mNextChildKey);
    }

    private boolean checkDuplicateKey(String nextChildKey) {
        if (mSnapshots.size() > 0) {
            DataSnapshot previousSnapshot = mSnapshots.get(mSnapshots.size() - 1);
            String previousChildkey = previousSnapshot == null ? "" : previousSnapshot.getKey();
            return (!TextUtils.isEmpty(previousChildkey) && previousChildkey.equals(nextChildKey));
        }
        return false;
    }

    public boolean isHasMore() {
        boolean isHasMore = true;
        if (mCount < mNumberPerPage || isDuplicateKey) {
            isHasMore = false;
        }
        Logger.d("isHasMore: " + isHasMore);
        return isHasMore;
    }

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }

    private void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }

    protected void notifyCancelledListeners(DatabaseError databaseError) {
        if (mListener != null) {
            mListener.onCancelled(databaseError);
        }
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("key not found");
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.CHANGED, index);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.REMOVED, index);
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        notifyCancelledListeners(databaseError);
    }
}
