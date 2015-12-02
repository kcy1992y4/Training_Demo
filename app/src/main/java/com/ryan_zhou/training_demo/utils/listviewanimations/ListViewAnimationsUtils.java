package com.ryan_zhou.training_demo.utils.listviewanimations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/23 11:06
 * @copyright TCL-MIE
 */
public class ListViewAnimationsUtils {

    private ListViewAnimationsUtils() {

    }

    /**
     * 在UndoPositions中删除dismissPositions,并计算偏移量，修正UndoPosition位置
     * @param positions
     * @param dismissedPositions
     * @return
     */
    public static Collection<Integer> processDeletions(Collection<Integer> positions, int[] dismissedPositions) {
        List<Integer> dismissedList = new ArrayList<>();
        for (int position : dismissedPositions) {
            dismissedList.add(position);
        }
        return processDeletions(positions, dismissedList);
    }

    /**
     * 在UndoPositions中删除dismissPositions,并计算偏移量，修正UndoPosition位置
     * @param positions
     * @param dismissedPositions
     * @return
     */
    public static Collection<Integer> processDeletions(Collection<Integer> positions, List<Integer> dismissedPositions) {
        Collection<Integer> result = new ArrayList<>(positions);
        Collections.sort(dismissedPositions, Collections.reverseOrder());
        Collection<Integer> newUndoPositions = new ArrayList<>();
        for (int position : dismissedPositions) {
            for (Iterator<Integer> iterator = result.iterator(); iterator.hasNext();) {
                int undoPosition = iterator.next();
                if (undoPosition > position) {
                    iterator.remove();
                    newUndoPositions.add(undoPosition - 1);
                } else if (undoPosition == position) {
                    iterator.remove();
                } else {
                    newUndoPositions.add(undoPosition);
                }
            }
            result.clear();
            result.addAll(newUndoPositions);
            newUndoPositions.clear();
        }
        return result;
    }

}
