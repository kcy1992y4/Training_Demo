package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.support.annotation.NonNull;
import android.widget.BaseAdapter;

import com.ryan_zhou.training_demo.utils.listviewanimations.Insertable;
import com.ryan_zhou.training_demo.utils.listviewanimations.Swappable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 10:18
 * @copyright TCL-MIE
 */
public abstract class ArrayAdapter<T> extends BaseAdapter implements Swappable, Insertable<T> {

    @NonNull
    private final List<T> mItems;

    private BaseAdapter mDataSetChangedSlavedAdapter;

    protected ArrayAdapter() {
        this(null);
    }

    protected ArrayAdapter(@NonNull final List<T> objects) {
        if (objects != null) {
            mItems = objects;
        } else {
            mItems = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @NonNull
    public T getItem(int position) {
        return mItems.get(position);
    }

    public List<T> getItems() {
        return mItems;
    }

    public boolean add(final T object) {
        boolean result = mItems.add(object);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public void add(int index, @NonNull T item) {
        mItems.add(index, item);
        notifyDataSetChanged();
    }

    public boolean addAll(final Collection<? extends T> collection) {
        boolean result = mItems.addAll(collection);
        notifyDataSetChanged();
        return result;
    }

    public boolean cotains(final Object object) {
        return mItems.contains(object);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public boolean remove(final Object object) {
        boolean result = mItems.remove(object);
        notifyDataSetChanged();
        return result;
    }

    public T remove(final int location) {
        T result = mItems.remove(location);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public void swapItems(int positionOne, int positionTow) {
        T firstItem = mItems.set(positionOne, getItem(positionTow));
        notifyDataSetChanged();
        mItems.set(positionTow, firstItem);
    }

    public void propagateNotifyDataSetChanged(final BaseAdapter slavedAdapter) {
        mDataSetChangedSlavedAdapter = slavedAdapter;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mDataSetChangedSlavedAdapter != null) {
            mDataSetChangedSlavedAdapter.notifyDataSetChanged();
        }
    }
}
