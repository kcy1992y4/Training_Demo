package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ryan_zhou.training_demo.utils.listviewanimations.BaseAdapterDecorator;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewAnimationsUtils;
import com.ryan_zhou.training_demo.utils.listviewanimations.OnDismissCallback;
import com.ryan_zhou.training_demo.utils.listviewanimations.UndoCallback;
import com.ryan_zhou.training_demo.widget.listviewanimations.SwipeUndoView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/26 19:56
 * @copyright TCL-MIE
 */
public class SimpleSwipeUndoAdapter extends SwipeUndoAdapter implements UndoCallback {

    private final Context mContext;

    private final OnDismissCallback mOnDismissCallback;

    private final UndoAdapter mUndoAdapter;

    private final Collection<Integer> mUndoPositions = new ArrayList<>();

    public SimpleSwipeUndoAdapter(BaseAdapter adapter, Context context, OnDismissCallback dismissCallback) {
        super(adapter, null);
        setUndoCallback(this);

        BaseAdapter undoAdapter = adapter;
        while (undoAdapter instanceof BaseAdapterDecorator) {
            undoAdapter = ((BaseAdapterDecorator) undoAdapter).getDecoratedBaseAdapter();
        }

        if (!(undoAdapter instanceof  UndoAdapter)) {
            throw new IllegalStateException("BaseAdapter must implement UndoAdapter!");
        }

        mUndoAdapter = (UndoAdapter) undoAdapter;
        mContext = context;
        mOnDismissCallback = dismissCallback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeUndoView view = (SwipeUndoView) convertView;
        if (view == null) {
            view = new SwipeUndoView(mContext);
        }
        // TODO:为什么是这样取View
        View primaryView = super.getView(position, view.getPrimaryView(), view);
        view.setPrimaryView(primaryView);

        View undoView = mUndoAdapter.getUndoView(position, view.getUndoView(), view);
        view.setUndoView(undoView);

        mUndoAdapter.getUndoClickView(undoView).setOnClickListener(new UndoClickListener(view, position));

        boolean isInUndoState = mUndoPositions.contains(position);
        primaryView.setVisibility(isInUndoState ? View.GONE : View.VISIBLE);
        undoView.setVisibility(isInUndoState ? View.VISIBLE : View.GONE);

        return view;
    }

    @Override
    public View getPrimaryView(View view) {
        View primaryView = ((SwipeUndoView) view).getPrimaryView();
        if (primaryView == null) {
            throw new IllegalStateException("primaryView == null");
        }
        return primaryView;
    }

    @Override
    public View getUndoView(View view) {
        View undoView = ((SwipeUndoView)view).getUndoView();
        if (undoView == null) {
            throw new IllegalStateException("undoView == null");
        }
        return undoView;
    }

    @Override
    public void onUndoShow(View view, int position) {
        mUndoPositions.add(position);
    }

    @Override
    public void onUndo(View view, int position) {
        mUndoPositions.remove(position);
    }

    @Override
    public void onDismiss(View view, int position) {
        mUndoPositions.remove(position);
    }

    @Override
    public void onDismiss(ViewGroup listView, int[] reverseSortedPositions) {
        mOnDismissCallback.onDismiss(listView, reverseSortedPositions);

        Collection<Integer> newUndoPosition = ListViewAnimationsUtils.processDeletions(mUndoPositions, reverseSortedPositions);
        mUndoPositions.clear();
        mUndoPositions.addAll(newUndoPosition);
    }

    private class UndoClickListener implements View.OnClickListener {

        private final SwipeUndoView mView;

        private final int mPosition;

        UndoClickListener(final SwipeUndoView view, int position) {
            mView = view;
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            undo(mView);
        }
    }
}
