package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ryan_zhou.training_demo.listener.listviewanimations.SwipeUndoTouchListener;
import com.ryan_zhou.training_demo.utils.listviewanimations.BaseAdapterDecorator;
import com.ryan_zhou.training_demo.utils.listviewanimations.DismissableManager;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewWrapper;
import com.ryan_zhou.training_demo.utils.listviewanimations.UndoCallback;
import com.ryan_zhou.training_demo.widget.listviewanimations.DynamicListView;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/20 20:09
 * @copyright TCL-MIE
 */
public class SwipeUndoAdapter extends BaseAdapterDecorator {

    private SwipeUndoTouchListener mSwipeUndoTouchListener;

    private UndoCallback mUndoCallback;

    protected SwipeUndoAdapter(BaseAdapter baseAdapter, UndoCallback undoCallback) {
        super(baseAdapter);
        mUndoCallback = undoCallback;
    }

    @Override
    public void setListViewWrapper(ListViewWrapper listViewWrapper) {
        super.setListViewWrapper(listViewWrapper);
        mSwipeUndoTouchListener = new SwipeUndoTouchListener(listViewWrapper, mUndoCallback);
        if (!(listViewWrapper.getListView() instanceof DynamicListView)) {
            listViewWrapper.getListView().setOnTouchListener(mSwipeUndoTouchListener);
        }
    }

    public void setDismissableManager(DismissableManager dismissableManager) {
        if (mSwipeUndoTouchListener == null) {
            throw new IllegalStateException("You must call setAbsListView() first.");
        }
        mSwipeUndoTouchListener.setDismissableManager(dismissableManager);
    }

    public void setSwipeUndoTouchListener(final SwipeUndoTouchListener swipeUndoTouchListener) {
        mSwipeUndoTouchListener = swipeUndoTouchListener;
    }

    @Override
    public View getView(final int position,final View convertView, ViewGroup parent) {
        if (getListViewWrapper() == null) {
            throw new IllegalArgumentException("Call setAbsListView() on this SwipeUndoAdapter before setAdapter()!");
        }
        return super.getView(position, convertView, parent);
    }

    public void setUndoCallback(UndoCallback undoCallback) {
        mUndoCallback = undoCallback;
    }

    public UndoCallback getUndoCallback() {
        return mUndoCallback;
    }

    public void undo(final View view) {
        mSwipeUndoTouchListener.undo(view);
    }

    public void dismiss(final int position) {
        mSwipeUndoTouchListener.dismiss(position);
    }
}
