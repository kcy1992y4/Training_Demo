package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/19 10:50
 * @copyright TCL-MIE
 */
public class InsertQueue<T> {

    private final Insertable<T> mInsertable;

    /**
     * 不可重复
     */
    private final Collection<AtomicInteger> mActiveIndexs = new HashSet<>();

    private final List<Pair<Integer, T>> mPendingItemsToInsert = new ArrayList<>();

    public InsertQueue(Insertable<T> insertable) {
        mInsertable = insertable;
    }

    /**
     * 当mActiveIndexs为空的时候，证明没有活动的Item，因此可以插入，但是当mPendingItemsToInsert不为空，代表有已经有Item排在前面，应该优先插入前面的
     *
     * @param index
     * @param item
     */
    public void insert(int index, final T item) {
        if (mActiveIndexs.isEmpty() && mPendingItemsToInsert.isEmpty()) {
            mActiveIndexs.add(new AtomicInteger(index));
            mInsertable.add(index, item);
        } else {
            mPendingItemsToInsert.add(new Pair<Integer, T>(index, item));
        }
    }

    public void insert(Pair<Integer, T>... indexItemPair) {
        insert(Arrays.asList(indexItemPair));
    }

    public void insert(Collection<Pair<Integer, T>> indexItemPairs) {
        if (mActiveIndexs.isEmpty() && mPendingItemsToInsert.isEmpty()) {
            for (Pair<Integer, T> pair : indexItemPairs) {
                for (AtomicInteger existing : mActiveIndexs) {
                    // TODO: 连续插入两个，位置可能不如我们想的那样。。
                    if (existing.intValue() >= pair.first) {
                        // 由于在前面插入Item的原因，position的位置都偏移了，因此需要校正position
                        existing.incrementAndGet();
                    }
                }
                mActiveIndexs.add(new AtomicInteger(pair.first));
                mInsertable.add(pair.first, pair.second);
            }
        } else {
            mPendingItemsToInsert.addAll(indexItemPairs);
        }
    }

    public void removeActiveIndex(int index) {
        boolean found = false;
        for (Iterator<AtomicInteger> iterator = mActiveIndexs.iterator(); iterator.hasNext() && !found;) {
            if (iterator.next().get() == index) {
                iterator.remove();
                found = true;
            }
        }
        if (mActiveIndexs.isEmpty()) {
            insertPending();
        }
    }

    public void clearActive() {
        mActiveIndexs.clear();
        insertPending();
    }

    private void insertPending() {
        for (Pair<Integer, T> pair : mPendingItemsToInsert) {
            for (AtomicInteger existing : mActiveIndexs) {
                if (existing.intValue() >= pair.first) {
                    existing.incrementAndGet();
                }
            }
            mActiveIndexs.add(new AtomicInteger(pair.first));
            mInsertable.add(pair.first, pair.second);
        }
        mPendingItemsToInsert.clear();
    }

    public Collection<Integer> getActiveIndexes() {
        Collection<Integer> result = new HashSet<>();
        for (AtomicInteger i : mActiveIndexs) {
            result.add(i.get());
        }
        return result;
    }

    public List<Pair<Integer, T>> getPendingItemsToInsert() {
        return mPendingItemsToInsert;
    }

}
