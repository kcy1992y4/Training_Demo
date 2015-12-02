package com.ryan_zhou.training_demo.activity.listviewanimations;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.adapter.listviewanimations.AlphaInAnimationAdapter;
import com.ryan_zhou.training_demo.adapter.listviewanimations.ArrayAdapter;
import com.ryan_zhou.training_demo.adapter.listviewanimations.SimpleSwipeUndoAdapter;
import com.ryan_zhou.training_demo.adapter.listviewanimations.UndoAdapter;
import com.ryan_zhou.training_demo.listener.listviewanimations.OnItemMovedListener;
import com.ryan_zhou.training_demo.utils.listviewanimations.OnDismissCallback;
import com.ryan_zhou.training_demo.utils.listviewanimations.TouchViewDraggableManager;
import com.ryan_zhou.training_demo.widget.listviewanimations.DynamicListView;

import java.util.Arrays;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 14:40
 * @copyright TCL-MIE
 */
public class DynamicListViewActivity extends MyListActivity {

    private static final String TAG = "DynamicListViewActivity";

    private static final int INITIAL_DELAY_MILLIS = 300;

    private int mNewItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamiclistview);

        DynamicListView listView = (DynamicListView) findViewById(R.id.activity_dynamiclistview_listview);
        listView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.activity_dynamiclistview_header, listView, false));

        ArrayAdapter<String> adapter = new MyListAdapter(this);
        SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(adapter, this, new MyOnDismissCallback(adapter));
        AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
        animAdapter.setAbsListView(listView);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(animAdapter);

        listView.enableDragAndDrop();
        listView.setDraggableManager(new TouchViewDraggableManager(R.id.list_row_draganddrop_touchview));
        listView.setOnItemMovedListener(new MyOnItemMovedListener(adapter));
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener(listView));

        listView.enableSimpleSwipeUndo();

        listView.setOnItemClickListener(new MyOnItemClickListener(listView));
    }

    private static class MyListAdapter extends ArrayAdapter<String> implements UndoAdapter {

        private final Context mContext;

        MyListAdapter(Context context) {
            mContext = context;
            for (int i = 0; i < 20; i++) {
                add(mContext.getString(R.string.row_number, i));
            }
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.list_row_dynamiclistview, parent, false);
            }

            ((TextView)view.findViewById(R.id.list_row_draganddrop_textview)).setText(getItem(position));

            return view;
        }

        @Override
        public View getUndoView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
            }
            return view;
        }

        @Override
        public View getUndoClickView(View view) {
            return view.findViewById(R.id.undo_row_undobutton);
        }
    }

    private class MyOnDismissCallback implements OnDismissCallback {

        private final ArrayAdapter<String> mAdapter;

        private Toast mToast;

        MyOnDismissCallback(final ArrayAdapter<String> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(ViewGroup listView, int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                mAdapter.remove(position);
            }
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(DynamicListViewActivity.this, getString(R.string.removed_positions, Arrays.toString(reverseSortedPositions)), Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    private class MyOnItemMovedListener implements OnItemMovedListener {

        private ArrayAdapter<String> mAdapter;

        private Toast mToast;

        MyOnItemMovedListener(final ArrayAdapter<String> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(int originalPosition, int newPosition) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(DynamicListViewActivity.this, getString(R.string.moved, mAdapter.getItem(newPosition), newPosition), Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        private final DynamicListView mListView;

        MyOnItemClickListener(DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListView.insert(position, getString(R.string.newly_added_item, mNewItemCount));
            mNewItemCount++;
        }
    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final DynamicListView mListView;

        MyOnItemLongClickListener(final DynamicListView listView) {
            mListView = listView;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mListView != null) {
                mListView.startDragging(position - mListView.getHeaderViewsCount());
            }
            return true;
        }
    }
}
