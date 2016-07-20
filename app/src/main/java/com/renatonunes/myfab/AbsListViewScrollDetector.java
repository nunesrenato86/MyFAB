package com.renatonunes.myfab;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by Renato on 11/07/2016.
 */
abstract class AbsListViewScrollDetector implements AbsListView.OnScrollListener {
    private int mLastScrollY;
    private int mPreviousFirstVisibleItem;
    private AbsListView mListView;
    private int mScrollThreshold;

    abstract void onScrollUp();

    abstract void onScrollDown();

    abstract void setScrollThreshold();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(totalItemCount == 0) return;
        if (isSameRow(firstVisibleItem)) {
            int newScrollY = getTopItemScrollY();
            boolean isSignificantDelta = Math.abs(mLastScrollY - newScrollY) > mScrollThreshold;
            Log.i("Abscroll", "mLastScrollY " + mLastScrollY);
            Log.i("Abscroll", "newScrollY " + newScrollY);
            if (isSignificantDelta) {
                Log.i("Abscroll", "sig delta");
                if (mLastScrollY > newScrollY) {
                    onScrollUp();
                    Log.i("Abscroll", "sig delta up");
                } else {
                    onScrollDown();
                    Log.i("Abscroll", "sig delta down");
                }
            }
            mLastScrollY = newScrollY;
        } else {
            if (firstVisibleItem > mPreviousFirstVisibleItem) {
                onScrollUp();
                Log.i("Abscroll", "prev up");
            } else {
                onScrollDown();
                Log.i("Abscroll", "prev down");
            }

            mLastScrollY = getTopItemScrollY();
            mPreviousFirstVisibleItem = firstVisibleItem;
        }
    }

    public void setScrollThreshold(int scrollThreshold) {
        mScrollThreshold = scrollThreshold;
        Log.i("Abscroll", "LView thresh " + scrollThreshold);
    }

    public void setListView(@NonNull AbsListView listView) {
        mListView = listView;
    }

    private boolean isSameRow(int firstVisibleItem) {
        return firstVisibleItem == mPreviousFirstVisibleItem;
    }

    private int getTopItemScrollY() {
        if (mListView == null || mListView.getChildAt(0) == null) return 0;
        View topChild = mListView.getChildAt(0);
        return topChild.getTop();
    }
}