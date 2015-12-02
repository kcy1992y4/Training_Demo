package com.ryan_zhou.training_demo.listener.listviewanimations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ryan_zhou.training_demo.utils.listviewanimations.AdapterViewUtil;
import com.ryan_zhou.training_demo.utils.listviewanimations.DismissableManager;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewWrapper;
import com.ryan_zhou.training_demo.utils.listviewanimations.TouchEventHandler;

/**
 * @author chaohao.zhou
 * @Description: 触摸滑动不见，但View只是隐藏而已
 * @date 2015/11/2 18:54
 * @copyright TCL-MIE
 */
public abstract class SwipeTouchListener implements View.OnTouchListener, TouchEventHandler {

    private static final String TRANSLATION_X = "translationX";
    private static final String ALPHA = "alpha";

    private static final int MIN_FLING_VELOCITY_FACTOR = 16;

    private final int mSlop;

    private final int mMinFlingVelocity;

    private final int mMaxFlingVelocity;

    private final long mAnimationTime;

    private final ListViewWrapper mListViewWrapper;

    private float mMinimumAlpha;

    private int mViewWidth = 1;

    private float mDownX;

    private float mDownY;

    /**
     * 是否在触摸滑动中
     */
    private boolean mSwiping;

    /**
     * 是否可以滑动删除，false为可以滑动，但是不立即删除，一般是弹出是否删除等对话框
     */
    private boolean mCanDismissCurrent;

    private VelocityTracker mVelocityTracker;

    /**
     * The parent {@link android.view.View} being swiped.
     */
    private View mCurrentView;

    /**
     * The {@link android.view.View} that is actually being swiped.
     */
    private View mSwipingView;

    /**
     * The current position being swiped.
     */
    private int mCurrentPosition = AdapterView.INVALID_POSITION;

    /**
     * The number of items in the {@code AbsListView}, minus the pending dismissed items.
     */
    private int mVirtualListCount = -1;

    /**
     * Indicates whether the {@link android.widget.AbsListView} is in a horizontal scroll container.
     * If so, this class will prevent the horizontal scroller from receiving any touch events.
     */
    private boolean mParentIsHorizontalScrollContainer;

    /**
     * The resource id of the {@link android.view.View} that may steal touch events from their parents. Useful for example
     * when the {@link android.widget.AbsListView} is in a horizontal scroll container, but not the whole {@code AbsListView} should
     * steal the touch events.
     */
    private int mTouchChildResId;

    private DismissableManager mDismissableManager;

    /**
     * The number of active swipe animations.
     */
    private int mActiveSwipeCount;

    /**
     * Indicates whether swipe is enabled.
     */
    private boolean mSwipeEnabled = true;

    protected SwipeTouchListener(final ListViewWrapper listViewWrapper) {
        ViewConfiguration vc = ViewConfiguration.get(listViewWrapper.getListView().getContext());
        mSlop = vc.getScaledEdgeSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * MIN_FLING_VELOCITY_FACTOR;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = listViewWrapper.getListView().getContext().getResources().getInteger(android.R.integer
                .config_shortAnimTime);
        mListViewWrapper = listViewWrapper;
    }

    public void setDismissableManager(final DismissableManager dismissableManager) {
        mDismissableManager = dismissableManager;
    }

    public void setMinimumAlpha(final float minimumAlpha) {
        mMinimumAlpha = minimumAlpha;
    }

    public void setParentIsHorizontalScrollContainer() {
        mParentIsHorizontalScrollContainer = true;
        mTouchChildResId = 0;
    }

    /**
     * Sets the resource id of a child view that should be touched to engage swipe.
     * When the user touches a region outside of that view, no swiping will occur.
     *
     * @param childResId The resource id of the list items' child that the user should touch to be able to swipe the list items.
     */
    public void setTouchChild(final int childResId) {
        mTouchChildResId = childResId;
        mParentIsHorizontalScrollContainer = false;
    }

    public void notifyDataSetChanged() {
        if (mListViewWrapper.getAdapter() != null) {
            mVirtualListCount = mListViewWrapper.getCount() - mListViewWrapper.getHeaderViewsCount();
        }
    }

    public boolean isSwiping() {
        return mSwiping;
    }

    public ListViewWrapper getListViewWrapper() {
        return mListViewWrapper;
    }

    public void enableSwipe() {
        mSwipeEnabled = true;
    }

    public void disableSwipe() {
        mSwipeEnabled = false;
    }

    /**
     * Calling this method has the same effect as manually swiping an item off the screen.
     * @param position
     */
    public void fling(final int position) {
        int firstVisiblePosition = mListViewWrapper.getFirstVisiblePosition();
        int lastVisiblePosition = mListViewWrapper.getLastVisiblePosition();

        if (position < firstVisiblePosition || position > lastVisiblePosition) {
            throw new IllegalArgumentException("View for position " + position + " not visible!");
        }

        View downView = AdapterViewUtil.getViewForPosition(mListViewWrapper, position);
        if (downView == null) {
            throw new IllegalStateException("No view found for position " + position);
        }

        flingView(downView, position, true);

        mActiveSwipeCount++;
        mVirtualListCount--;
    }

    @Override
    public boolean isInteracting() {
        return mSwiping;
    }

    // TODO:看什么时候调用
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouch(null, event);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (mListViewWrapper.getAdapter() == null) {
            return false;
        }
        // 第一次触摸，初始化mVirtualListCount
        if (mVirtualListCount == -1 || mActiveSwipeCount == 0) {
            mVirtualListCount = mListViewWrapper.getCount() - mListViewWrapper.getHeaderViewsCount();
        }
        if (mViewWidth < 2) {
            mViewWidth = mListViewWrapper.getListView().getWidth();
        }

        boolean result;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                result = handleDownEvent(view, event);
                break;
            case MotionEvent.ACTION_MOVE:
                result = handleMoveEvent(view, event);
                break;
            case MotionEvent.ACTION_CANCEL:
                result = handleCancelEvent();
                break;
            case MotionEvent.ACTION_UP:
                result = handleUpEvent(event);
                break;
            default:
                result = false;
        }
        return result;
    }

    private boolean handleDownEvent(final View view, final MotionEvent motionEvent) {
        if (!mSwipeEnabled) {
            return false;
        }
        View downView = findDownView(motionEvent);
        if (downView == null) {
            return false;
        }

        int downPosition = AdapterViewUtil.getPositionForView(mListViewWrapper, downView);
        mCanDismissCurrent = isDismissable(downPosition);

        if (mCurrentPosition == downPosition || downPosition >= mVirtualListCount) {
            return false;
        }

        if (view != null) {
            view.onTouchEvent(motionEvent);
        }

        disableHorizontalScrollContainerIfNecessary(motionEvent, downView);

        mDownX = motionEvent.getX();
        mDownY = motionEvent.getY();
        mCurrentView = downView;
        mSwipingView = getSwipeView(downView);
        mCurrentPosition = downPosition;

        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(motionEvent);
        return true;
    }

    private boolean handleMoveEvent(final View view, final MotionEvent motionEvent) {
        if (mVelocityTracker == null || mCurrentView == null) {
            return false;
        }

        mVelocityTracker.addMovement(motionEvent);

        float deltaX = motionEvent.getX() - mDownX;
        float deltaY = motionEvent.getY() - mDownY;

        // 当滑动的X距离大于mSlop并且大于Y的移动距离，才会将mSwiping置为true
        if (Math.abs(deltaX) > mSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
            if (!mSwiping) {
                mActiveSwipeCount++;
                onStartSwipe(mCurrentView, mCurrentPosition);
            }
            mSwiping = true;
            mListViewWrapper.getListView().requestDisallowInterceptTouchEvent(true);
            // TODO:看是否触发该listener的onTouchEvent事件
            if (view != null) {
                MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL | motionEvent.getActionIndex() << MotionEvent
                        .ACTION_POINTER_INDEX_SHIFT);
                view.onTouchEvent(cancelEvent);
                cancelEvent.recycle();
            }
        }

        if (mSwiping) {
            if (mCanDismissCurrent) {
                mSwipingView.setTranslationX(deltaX);
                mSwipingView.setAlpha(Math.max(mMinimumAlpha, Math.min(1, 1 - 2 * Math.abs(deltaX) / mViewWidth)));
            } else {
                mSwipingView.setTranslationX(deltaX * 0.1f);
            }
            return true;
        }
        return false;
    }

    /**
     * 处理取消事件
     * @return
     */
    private boolean handleCancelEvent() {
        if (mVelocityTracker == null || mCurrentView == null) {
            return false;
        }

        if (mCurrentPosition != AdapterView.INVALID_POSITION && mSwiping) {
            onCancelSwipe(mCurrentView, mCurrentPosition);
            restoreCurrentViewTranslation();
        }
        reset();
        return false;
    }

    private boolean handleUpEvent(final MotionEvent motionEvent) {
        if (mVelocityTracker == null || mCurrentView == null) {
            return false;
        }
        //  在滑动中
        if (mSwiping) {
            boolean shouldDismiss = false;
            boolean dismissToRight = false;

            if (mCanDismissCurrent) {
                float deltaX = motionEvent.getX() - mDownX;

                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);

                float velocityX = Math.abs(mVelocityTracker.getXVelocity());
                float velocityY = Math.abs(mVelocityTracker.getYVelocity());

                if (Math.abs(deltaX) > mViewWidth / 2) {
                    shouldDismiss = true;
                    dismissToRight = deltaX > 0;
                } else if (mMinFlingVelocity <= velocityX && velocityX <= mMaxFlingVelocity && velocityY < velocityX) {
                    shouldDismiss = true;
                    dismissToRight = mVelocityTracker.getXVelocity() > 0;
                }
            }
            if (shouldDismiss) {
                beforeViewFling(mCurrentView, mCurrentPosition);
                if (willLeaveDataSetOnFling(mCurrentView, mCurrentPosition)) {
                    mVirtualListCount--;
                }
                flingCurrentView(dismissToRight);
            } else {
                onCancelSwipe(mCurrentView, mCurrentPosition);
                restoreCurrentViewTranslation();
            }
        }
        reset();
        return false;
    }

    /**
     * 根据点击的位置，返回ListView被点击的Item
     *
     * @param motionEvent
     * @return
     */
    private View findDownView(final MotionEvent motionEvent) {
        Rect rect = new Rect();
        int childCount = mListViewWrapper.getChildCount();
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        View downView = null;
        for (int i = 0; i < childCount && downView == null; i++) {
            View child = mListViewWrapper.getChildAt(i);
            if (child != null) {
                child.getHitRect(rect);
                if (rect.contains(x, y)) {
                    downView = child;
                }
            }
        }
        return downView;
    }

    private boolean isDismissable(final int position) {
        if (mListViewWrapper.getAdapter() == null) {
            return false;
        }
        if (mDismissableManager != null) {
            long downId = mListViewWrapper.getAdapter().getItemId(position);
            return mDismissableManager.isDismissable(downId, position);
        }
        return true;
    }

    private void disableHorizontalScrollContainerIfNecessary(final MotionEvent motionEvent, final View view) {
        if (mParentIsHorizontalScrollContainer) {
            mListViewWrapper.getListView().requestDisallowInterceptTouchEvent(true);
        } else if (mTouchChildResId != 0) {
            mParentIsHorizontalScrollContainer = false;

            final View childView = view.findViewById(mTouchChildResId);
            if (childView != null) {
                final Rect childRect = getChidViewRect(mListViewWrapper.getListView(), childView);
                if (childRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    // 拦截子View的touchEvent
                    mListViewWrapper.getListView().requestDisallowInterceptTouchEvent(true);
                }
            }
        }
    }

    private void flingCurrentView(final boolean flingToRight) {
        if (mCurrentView != null) {
            flingView(mCurrentView, mCurrentPosition, flingToRight);
        }
    }

    private void flingView(final View view, final int position, final boolean flingToRight) {
        if (mViewWidth < 2) {
            mViewWidth = mListViewWrapper.getListView().getWidth();
        }

        View swipeView = getSwipeView(view);
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(swipeView, TRANSLATION_X, flingToRight ? mViewWidth :
                -mViewWidth);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(swipeView, ALPHA, 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(xAnimator, alphaAnimator);
        animatorSet.setDuration(mAnimationTime);
        animatorSet.addListener(new FlingAnimatorListener(view, position));
        animatorSet.start();
    }

    /**
     *  动画恢复原来的状态
     */
    private void restoreCurrentViewTranslation() {
        if (mCurrentView == null) {
            return;
        }

        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(mSwipingView, TRANSLATION_X, 0);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mSwipingView, ALPHA, 1);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(xAnimator, alphaAnimator);
        animatorSet.setDuration(mAnimationTime);
        animatorSet.addListener(new RestoreAnimatorListener(mCurrentView, mCurrentPosition));
        animatorSet.start();
    }

    /**
     * 将所有的数据重新置为原始状态，在放手或者取消的时候会触发
     */
    private void reset() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
        mVelocityTracker = null;
        mDownX = 0;
        mDownY = 0;
        mCurrentView = null;
        mSwipingView = null;
        mCurrentPosition = AdapterView.INVALID_POSITION;
        mSwiping = false;
        mCanDismissCurrent = false;
    }


    private static Rect getChidViewRect(final View parentView, final View childView) {
        Rect childRect = new Rect(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
        if (!parentView.equals(childView)) {
            View workingChildView = childView;
            ViewGroup parent;
            // parentView 一般是ListView，childView可能是Item里面的子View，所以要 循环找出Item
            // 这时的ChildViewRect才是真的ViewRect
            while (!(parent = (ViewGroup) workingChildView).getParent().equals(parentView)) {
                childRect.offset(parent.getLeft(), parent.getTop());
                workingChildView = parentView;
            }
        }
        return childRect;
    }

    protected View getSwipeView(final View view) {
        return view;
    }

    protected void onStartSwipe(final View view, final int position) {

    }

    protected void onCancelSwipe(final View view, final int position) {

    }

    protected void afterCancelSwipe(final View view, final int position) {

    }

    protected void beforeViewFling(final View view, final int position) {

    }

    // TODO: 什么意思呢
    protected abstract boolean willLeaveDataSetOnFling(View view, int position);

    /**
     * 在滑动删除View后触发
     * @param view
     * @param position
     */
    protected abstract void afterViewFling(View view, int position);

    /**
     * 恢复View的初始状态
     * @param view
     */
    protected void restoreViewPresentation(final View view) {
        View swipedView = getSwipeView(view);
        swipedView.setAlpha(1);
        swipedView.setTranslationX(0);
    }

    protected int getActiveSwipeCount() {
        return mActiveSwipeCount;
    }

    private class FlingAnimatorListener extends AnimatorListenerAdapter {
        private final View mView;
        private final int mPosition;

        private FlingAnimatorListener(final View view, final int position) {
            mView = view;
            mPosition = position;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mActiveSwipeCount--;
            afterViewFling(mView, mPosition);
        }
    }

    private class RestoreAnimatorListener extends AnimatorListenerAdapter {

        private final View mView;

        private final int mPosition;

        private RestoreAnimatorListener(final View view, final int position) {
            mView = view;
            mPosition = position;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mActiveSwipeCount--;
            afterCancelSwipe(mView, mPosition);
        }
    }
}
