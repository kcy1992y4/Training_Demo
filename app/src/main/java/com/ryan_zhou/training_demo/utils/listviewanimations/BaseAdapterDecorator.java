package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.ryan_zhou.training_demo.adapter.listviewanimations.ArrayAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 13:56
 * @copyright TCL-MIE
 */
public abstract class BaseAdapterDecorator extends BaseAdapter implements SectionIndexer, Swappable, Insertable,
        ListViewWrapperSetter {

    private final BaseAdapter mDecoratedBaseAdapter;

    private ListViewWrapper mListViewWrapper;

    protected BaseAdapterDecorator(final BaseAdapter baseAdapter) {
        mDecoratedBaseAdapter = baseAdapter;
    }

    public BaseAdapter getDecoratedBaseAdapter() {
        return mDecoratedBaseAdapter;
    }

    protected BaseAdapter getRootAdapter() {
        BaseAdapter adapter = mDecoratedBaseAdapter;
        while (adapter instanceof BaseAdapterDecorator) {
            adapter = ((BaseAdapterDecorator) adapter).getDecoratedBaseAdapter();
        }
        return adapter;
    }

    public void setAbsListView(final AbsListView absListView) {
        setListViewWrapper(new AbsListViewWrapper(absListView));
    }

    public ListViewWrapper getListViewWrapper() {
        return mListViewWrapper;
    }

    @Override
    public void setListViewWrapper(ListViewWrapper listViewWrapper) {
        mListViewWrapper = listViewWrapper;

        if (mDecoratedBaseAdapter instanceof ListViewWrapperSetter) {
            ((ListViewWrapperSetter) mDecoratedBaseAdapter).setListViewWrapper(listViewWrapper);
        }
    }

    @Override
    public int getCount() {
        return mDecoratedBaseAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mDecoratedBaseAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mDecoratedBaseAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mDecoratedBaseAdapter.getView(position, convertView, parent);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mDecoratedBaseAdapter.areAllItemsEnabled();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return mDecoratedBaseAdapter.getDropDownView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return mDecoratedBaseAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mDecoratedBaseAdapter.getViewTypeCount();
    }

    @Override
    public boolean hasStableIds() {
        return mDecoratedBaseAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mDecoratedBaseAdapter.isEmpty();
    }

    @Override
    public void notifyDataSetChanged() {
        if (!(mDecoratedBaseAdapter instanceof ArrayAdapter<?>)) {
            mDecoratedBaseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetInvalidated() {
        mDecoratedBaseAdapter.notifyDataSetInvalidated();
    }

    /**
     * 绑定同一个DataSetObserver，使之可以在notifyDataSetChanged后更新数据
     * @param observer
     */
    @Override
    public void registerDataSetObserver(@NonNull final DataSetObserver observer) {
        mDecoratedBaseAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDecoratedBaseAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int result = 0;
        if (mDecoratedBaseAdapter instanceof SectionIndexer) {
            result = ((SectionIndexer) mDecoratedBaseAdapter).getPositionForSection(sectionIndex);
        }
        return result;
    }

    @Override
    public int getSectionForPosition(int position) {
        int result = 0;
        if (mDecoratedBaseAdapter instanceof SectionIndexer) {
            result = ((SectionIndexer)mDecoratedBaseAdapter).getSectionForPosition(position);
        }
        return result;
    }

    @Override
    public Object[] getSections() {
        Object[] result = new Object[0];
        if (mDecoratedBaseAdapter instanceof SectionIndexer) {
            result = ((SectionIndexer)mDecoratedBaseAdapter).getSections();
        }
        return result;
    }

    @Override
    public void swapItems(int positionOne, int positionTow) {
        if (mDecoratedBaseAdapter instanceof Swappable) {
            ((Swappable)mDecoratedBaseAdapter).swapItems(positionOne, positionTow);
        } else {
            Log.w("ListViewAnimations", "Warning: swapItems called on an adapter that does not implement Swappable!");
        }
    }

    @Override
    public void add(int index, @NonNull Object item) {
        if (mDecoratedBaseAdapter instanceof Insertable) {
            ((Insertable)mDecoratedBaseAdapter).add(index, item);
        } else {
            Log.w("ListViewAnimations", "Warning: add called on an adapter that does not implement Insertable!");
        }
    }
}
