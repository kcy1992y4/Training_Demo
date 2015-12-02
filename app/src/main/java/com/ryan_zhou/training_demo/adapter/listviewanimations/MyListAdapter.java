package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ryan_zhou.training_demo.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 15:09
 * @copyright TCL-MIE
 */
public class MyListAdapter extends ArrayAdapter<String> implements UndoAdapter, StickyListHeadersAdapter {

    private final Context mContext;

    public MyListAdapter(final Context context) {
        mContext = context;
        for (int i = 0; i < 1000; i++) {
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
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) convertView;
        if (view == null) {
            view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.list_header, parent, false);
        }

        view.setText(mContext.getString(R.string.header, getHeaderId(position)));
        return view;
    }

    @Override
    public long getHeaderId(int position) {
        return position / 10;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) convertView;
        if (view == null) {
            view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.list_row, parent, false);
        }

        view.setText(getItem(position));
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
