package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.widget.BaseAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 10:20
 * @copyright TCL-MIE
 */
public interface Swappable {

    /**
     * Swaps the item on the first adapter position with thie item on teh second adapter position
     * Be sure to call {@link BaseAdapter#notifyDataSetChanged()} if appropriate when implementing this method
     * @param positionOne First adapter position
     * @param positionTow Second adapter position
     */
    void swapItems(int positionOne, int positionTow);
}
