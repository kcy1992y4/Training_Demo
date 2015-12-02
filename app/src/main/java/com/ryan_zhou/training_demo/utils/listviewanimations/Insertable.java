package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.support.annotation.NonNull;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 10:19
 * @copyright TCL-MIE
 */
public interface Insertable<T> {

    /**
     * Will be called to insert given {@code item} at given {@code index} in the list;
     * @param index the index the new item should be inserted at
     * @param item the item to insert
     */
    void add(int index, @NonNull T item);
}
