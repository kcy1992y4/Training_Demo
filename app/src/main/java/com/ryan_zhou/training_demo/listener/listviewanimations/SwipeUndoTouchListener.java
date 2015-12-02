package com.ryan_zhou.training_demo.listener.listviewanimations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.ryan_zhou.training_demo.utils.listviewanimations.AdapterViewUtil;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewAnimationsUtils;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewWrapper;
import com.ryan_zhou.training_demo.utils.listviewanimations.UndoCallback;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/20 20:11
 * @copyright TCL-MIE
 */
public class SwipeUndoTouchListener extends SwipeDismissTouchListener {

    private static final String ALPHA = "alpha";

    private static final String TRANSLATION_X = "translationX";

    private UndoCallback mCallback;

    private final Collection<Integer> mUndoPositions = new LinkedList<>();

    private final Map<Integer, View> mUndoViews  = new HashMap<>();

    private List<Integer> mDismissedPositions = new LinkedList<>();

    private final Collection<View> mDismissedViews = new LinkedList<>();

    public SwipeUndoTouchListener(ListViewWrapper listViewWrapper, UndoCallback callback) {
        super(listViewWrapper, callback);
        mCallback = callback;
    }

    // TODO:实现完的时候看看具体是什么意思
    @Override
    protected boolean willLeaveDataSetOnFling(View view, int position) {
        return mUndoPositions.contains(position);
    }

    @Override
    protected void afterViewFling(View view, int position) {
        if (mUndoPositions.contains(position)) {
            mUndoPositions.remove(position);
            mUndoViews.remove(position);
            performDismiss(view, position);
            hideUndoView(view);
        } else {
            mUndoPositions.add(position);
            mUndoViews.put(position, view);
            mCallback.onUndoShow(view, position);
            showUndoView(view);
            restoreViewPresentation(view);
        }
    }

    @Override
    protected void afterCancelSwipe(View view, int position) {
        finalizeDismiss();
    }

    @Override
    protected void performDismiss(View view, int position) {
        super.performDismiss(view, position);
        mDismissedViews.add(view);
        mDismissedPositions.add(position);

        mCallback.onDismiss(view, position);
    }

    /**
     * 有Undo的Item
     * @return
     */
    public boolean hasPendingItems() {
        return !mUndoPositions.isEmpty();
    }

    public void dimissPending() {
        for (int position : mUndoPositions) {
            performDismiss(mUndoViews.get(position), position);
        }
    }

    private void showUndoView(View view) {
        mCallback.getPrimaryView(view).setVisibility(View.GONE);

        View undoView = mCallback.getUndoView(view);
        undoView.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(undoView, ALPHA, 0f, 1f).start();
    }

    private void hideUndoView(View view) {
        mCallback.getPrimaryView(view).setVisibility(View.VISIBLE);
        mCallback.getUndoView(view).setVisibility(View.GONE);
    }

    @Override
    protected void finalizeDismiss() {
        if (getActiveDismissCount() == 0 && getActiveSwipeCount() == 0) {
            restoreViewPresentations(mDismissedViews);
            notifyCallback(mDismissedPositions);

            Collection<Integer> newUndoPositions = ListViewAnimationsUtils.processDeletions(mUndoPositions, mDismissedPositions);
            mUndoPositions.clear();
            mUndoPositions.addAll(newUndoPositions);

            mDismissedViews.clear();
            mDismissedPositions.clear();
        }
    }

    @Override
    protected void restoreViewPresentation(View view) {
        // TODO:调试看看是否执行了两次，尝试一下执行一次的效果，觉得应该执行一次就可以了
        super.restoreViewPresentation(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = 0;
        view.setLayoutParams(layoutParams);
    }

    public void undo(View view) {
        int position = AdapterViewUtil.getPositionForView(getListViewWrapper(), view);
        mUndoPositions.remove(position);

        View primaryView = mCallback.getPrimaryView(view);
        View undoView = mCallback.getUndoView(view);

        primaryView.setVisibility(View.VISIBLE);

        ObjectAnimator undoAlphaAnimator = ObjectAnimator.ofFloat(undoView, ALPHA, 1f, 0f);
        ObjectAnimator primaryAlphaAnimator = ObjectAnimator.ofFloat(primaryView, ALPHA, 0f, 1f);
        ObjectAnimator primaryXAnimator = ObjectAnimator.ofFloat(primaryView, TRANSLATION_X, primaryView.getWidth(), 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(undoAlphaAnimator, primaryAlphaAnimator, primaryXAnimator);
        animatorSet.addListener(new UndoAnimatorListener(undoView));
        animatorSet.start();

        mCallback.onUndo(view, position);
    }

    private class UndoAnimatorListener extends AnimatorListenerAdapter {
        private final View mUndoView;

        UndoAnimatorListener(View undoView) {
            mUndoView = undoView;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mUndoView.setVisibility(View.GONE);
            finalizeDismiss();
        }
    }
}
