package com.ryan_zhou.training_demo.listener.listviewanimations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.ryan_zhou.training_demo.utils.listviewanimations.AdapterViewUtil;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewWrapper;
import com.ryan_zhou.training_demo.utils.listviewanimations.OnDismissCallback;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chaohao.zhou
 * @Description: 触摸滑动删除，ItemView也删除
 * @date 2015/11/10 14:28
 * @copyright TCL-MIE
 */
public class SwipeDismissTouchListener extends SwipeTouchListener {

    /**
     * The callback which gets notified of dismissed items.
     */
    private final OnDismissCallback mCallback;

    /**
     * dismiss animation 的持续事件
     */
    private final long mDismissAnimationTime;

    /**
     * The View s this have been dismissed
     */
    private final Collection<View> mDismissedViews = new LinkedList<>();

    /**
     * the position that the view s have been dismissed
     */
    private final List<Integer> mDismissedPositions = new LinkedList<>();

    /**
     * 滑动删除进行中的数量
     */
    private int mActiveDismissCount;

    private final Handler mHandler = new Handler();

    public SwipeDismissTouchListener(final ListViewWrapper listViewWrapper, final OnDismissCallback callback) {
        super(listViewWrapper);
        mCallback = callback;
        mDismissAnimationTime = listViewWrapper.getListView().getContext().getResources().getInteger(android.R
                .integer.config_shortAnimTime);
    }

    public void dismiss(final int position) {
        fling(position);
    }

    @Override
    public void fling(int position) {
        int firstVisiblePosition = getListViewWrapper().getFirstVisiblePosition();
        int lastVisiblePosition = getListViewWrapper().getLastVisiblePosition();
        if (firstVisiblePosition <= position && position <= lastVisiblePosition) {
            super.fling(position);
        } else if (position > lastVisiblePosition) {
            directDismiss(position);
        } else {
            dismissAbove(position);
        }
    }

    protected void directDismiss(final int position) {
        mDismissedPositions.add(position);
        finalizeDismiss();
    }

    private void dismissAbove(final int position) {
        View view = AdapterViewUtil.getViewForPosition(getListViewWrapper(), getListViewWrapper()
                .getFirstVisiblePosition());

        if (view != null) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                    .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int scrollDistance = view.getMeasuredHeight();
            // 先向下滚动一个Item的位置
            getListViewWrapper().smoothScrollBy(scrollDistance, (int) mDismissAnimationTime);
            mHandler.postDelayed(new RestoreScrollRunnable(scrollDistance, position), mDismissAnimationTime);
        }
    }

    @Override
    protected void afterCancelSwipe(View view, int position) {
        finalizeDismiss();
    }

    @Override
    protected boolean willLeaveDataSetOnFling(View view, int position) {
        return true;
    }

    @Override
    protected void afterViewFling(View view, int position) {
        performDismiss(view, position);
    }

    /**
     * 执行下面Item向上补位动画
     * @param view
     * @param position
     */
    protected void performDismiss(final View view, final int position) {
        mDismissedViews.add(view);
        mDismissedPositions.add(position);

        ValueAnimator animator = ValueAnimator.ofInt(view.getHeight(), 1).setDuration(mDismissAnimationTime);
        animator.addUpdateListener(new DismissAnimatorUpdateListener(view));
        animator.addListener(new DismissAnimatorListener());
        animator.start();

        mActiveDismissCount++;
    }

    /**
     * 当必须的时候，通知 {@link OnDismissCallback} 在Adapter中删除已经滑动删除的View,并将View的高度设为0
     */
    protected void finalizeDismiss() {
        if (mActiveDismissCount == 0 && getActiveSwipeCount() == 0) {
            restoreViewPresentations(mDismissedViews);
            // 通知Adapter 删除已经滑动删除的Item
            notifyCallback(mDismissedPositions);

            mDismissedViews.clear();
            mDismissedPositions.clear();
        }
    }

    protected void notifyCallback(final List<Integer> dismissedPositions) {
        if (!dismissedPositions.isEmpty()) {
            Collections.sort(dismissedPositions, Collections.reverseOrder());

            int[] dismissPositions = new int[dismissedPositions.size()];
            int i = 0;
            for (Integer dismissedPosition : dismissedPositions) {
                dismissPositions[i] = dismissedPosition;
                i++;
            }
            // 根据Position删除Adapter中的Item
            mCallback.onDismiss(getListViewWrapper().getListView(), dismissPositions);
        }
    }

    protected void restoreViewPresentations(final Iterable<View> views) {
        for (View view : views) {
            restoreViewPresentation(view);
        }
    }

    /**
     * 恢复原来状态，如原始位置，原始透明度，并将View的高度设为0
     *
     * @param view
     */
    @Override
    protected void restoreViewPresentation(View view) {
        super.restoreViewPresentation(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = 0;
        view.setLayoutParams(layoutParams);
    }

    protected int getActiveDismissCount() {
        return mActiveDismissCount;
    }

    public long getDismissAnimationTime() {
        return mDismissAnimationTime;
    }

    private static class DismissAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private final View mView;

        DismissAnimatorUpdateListener(View view) {
            mView = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
            layoutParams.height = (Integer) animation.getAnimatedValue();
            mView.setLayoutParams(layoutParams);
        }
    }

    private class DismissAnimatorListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            mActiveDismissCount--;
            finalizeDismiss();
        }
    }

    private class RestoreScrollRunnable implements Runnable {

        private final int mScrollDistance;
        private final int mPosition;

        RestoreScrollRunnable(final int scrollDistance, final int position) {
            mScrollDistance = scrollDistance;
            mPosition = position;
        }

        @Override
        public void run() {
            // 由于之前的向下滚动是模拟操作，所以要将滚动的偏移量返回
            getListViewWrapper().smoothScrollBy(-mScrollDistance, 1);
            // 当返回到之前的位置的时候，同时通知Adapter删除相应的位置，这样就可以实现删除上移的动作
            directDismiss(mPosition);
        }
    }
}
