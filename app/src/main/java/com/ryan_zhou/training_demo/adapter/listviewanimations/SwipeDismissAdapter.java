package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.widget.BaseAdapter;

import com.ryan_zhou.training_demo.listener.listviewanimations.SwipeDismissTouchListener;
import com.ryan_zhou.training_demo.utils.listviewanimations.BaseAdapterDecorator;
import com.ryan_zhou.training_demo.utils.listviewanimations.DismissableManager;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewWrapper;
import com.ryan_zhou.training_demo.utils.listviewanimations.OnDismissCallback;

import java.util.ArrayList;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/2 16:13
 * @copyright TCL-MIE
 */
public class SwipeDismissAdapter extends BaseAdapterDecorator {

    private final OnDismissCallback mOnDismissCallbace;

    private SwipeDismissTouchListener mDismissTouchListener;

    private boolean mParentIsHorizontalScrollContainer;

    private int mSwipeTouchChildResId;

    public SwipeDismissAdapter(final BaseAdapter baseAdapter, OnDismissCallback onDismissCallback) {
        super(baseAdapter);
        mOnDismissCallbace = onDismissCallback;
    }

    @Override
    public void setListViewWrapper(final ListViewWrapper listViewWrapper) {
        super.setListViewWrapper(listViewWrapper);
        if (getDecoratedBaseAdapter() instanceof  ArrayAdapter<?>) {
            ((ArrayAdapter<?>) getDecoratedBaseAdapter()).propagateNotifyDataSetChanged(this);
        }
        mDismissTouchListener = new SwipeDismissTouchListener(listViewWrapper, mOnDismissCallbace);
        if (mParentIsHorizontalScrollContainer) {
            mDismissTouchListener.setParentIsHorizontalScrollContainer();
        }
        if (mSwipeTouchChildResId != 0) {
            mDismissTouchListener.setTouchChild(mSwipeTouchChildResId);
        }
        listViewWrapper.getListView().setOnTouchListener(mDismissTouchListener);
    }

    public void setDismissableManager(final DismissableManager dismissableManager) {
        if (mDismissTouchListener == null) {
            throw new IllegalStateException("You must call setAbsListView() first.");
        }
        mDismissTouchListener.setDismissableManager(dismissableManager);
    }

    public void setParentIsHorizontalScrollContainer() {
        mParentIsHorizontalScrollContainer = true;
        mSwipeTouchChildResId = 0;
        if (mDismissTouchListener != null) {
            mDismissTouchListener.setParentIsHorizontalScrollContainer();
        }
    }

    public void setSwipeTouchChildResId(final int childResId) {
        mSwipeTouchChildResId = childResId;
        if (mDismissTouchListener != null) {
            mDismissTouchListener.setTouchChild(childResId);
        }
    }

    public void dismiss(final int position) {
        if (mDismissTouchListener == null) {
            throw new IllegalStateException("Call setListViewWrapper on this SwipeDismissAdapter!");
        }
        mDismissTouchListener.dismiss(position);
    }

    public SwipeDismissTouchListener getDismissTouchListener() {
        return mDismissTouchListener;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mDismissTouchListener != null) {
            mDismissTouchListener.notifyDataSetChanged();
        }
    }
}
