package com.ryan_zhou.training_demo.widget.listviewanimations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.GridView;

import com.ryan_zhou.training_demo.utils.CommonUtils;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewWrapper;

/**
 * @author chaohao.zhou
 * @Description: 是整个ListView的ViewAnimator
 * A class which decides whether given Views should be animated based on their position: each View should only be animated once.
 * It also calculates proper animation delays for the views.
 * @date 2015/10/29 15:29
 * @copyright TCL-MIE
 */
public class ViewAnimator {

    private static final String TAG = "ViewAnimator";

    private static final String SAVEDINSTANCESTATE_FIRSTANIMATEDPOSITION = "savedinstancestate_firstanimatedposition";
    private static final String SAVEDINSTANCESTATE_LASTANIMATEDPOSITION = "savedinstancestate_lastanimatedposition";
    private static final String SAVEDINSTANCESTATE_SHOULDANIMATE = "savedinstancestate_shouldanimate";

    private static final int INITIAL_DELAY_MILLIS = 150;
    private static final int DEFAULT_ANIMATION_DELAY_MILLIS = 100;
    private static final int DEFAULT_ANIMATION_DURATION_MILLIS = 300;

    private final ListViewWrapper mListViewWrapper;

    private final SparseArray<Animator> mAnimators = new SparseArray<>();

    private int mInitialDelayMillis = INITIAL_DELAY_MILLIS;
    private int mAnimationDelayMillis = DEFAULT_ANIMATION_DELAY_MILLIS;
    private int mAnimationDurationMillis = DEFAULT_ANIMATION_DURATION_MILLIS;

    private long mAnimationStartMillis;
    private int mFirstAnimatedPosition;
    private int mLastAnimatedPosition;
    private boolean mShouldAnimate = true;

    public ViewAnimator(final ListViewWrapper listViewWrapper) {
        mListViewWrapper = listViewWrapper;
        mAnimationStartMillis = -1;
        mFirstAnimatedPosition = -1;
        mLastAnimatedPosition = -1;
    }

    public void reset() {
        for (int i = 0; i < mAnimators.size(); i++) {
            mAnimators.get(mAnimators.keyAt(i)).cancel();
        }
        mAnimators.clear();
        mAnimationStartMillis = -1;
        mFirstAnimatedPosition = -1;
        mLastAnimatedPosition = -1;
        mShouldAnimate = true;
    }

    /**
     * Set the starting position for which items should animate. Given position will animate as well
     * @param position position the position
     */
    public void setShouldAnimateFromPosition(final int position) {
        enableAnimations();
        // 将mFirstAnimatedPosition和mLastAnimatedPosition设值为position的前一个位置
        mFirstAnimatedPosition = position - 1;
        mLastAnimatedPosition = position - 1;
    }

    // TODO 看什么时候调用
    public void setShouldAnimateNotVisible() {
        enableAnimations();
        mFirstAnimatedPosition = mListViewWrapper.getLastVisiblePosition();
        mLastAnimatedPosition = mListViewWrapper.getLastVisiblePosition();
    }

    public void setLastAnimatedPosition(final int lastAnimatedPosition) {
        mLastAnimatedPosition = lastAnimatedPosition;
    }

    public void setInitialDelayMillis(final int delayMillis) {
        mInitialDelayMillis = delayMillis;
    }

    public void setAnimationDelayMillis(final int delayMillis) {
        mAnimationDelayMillis = delayMillis;
    }

    public void setAnimationDurationMillis(final int durationMillis) {
        mAnimationDurationMillis = durationMillis;
    }

    public void enableAnimations() {
        mShouldAnimate = true;
    }

    public void disableAnimations() {
        mShouldAnimate = false;
    }

    /**
     * 根据View的哈希码停止View的动画，并将View的
     * @param view
     */
    public void cancelExistingAnimation(final View view) {
        int hashCode = view.hashCode();
        Animator animator = mAnimators.get(hashCode);
        if (animator != null) {
            animator.end();
            mAnimators.remove(hashCode);
        }
    }

    public void animateViewIfNecessary(final int position, final View view, final Animator[] animators) {
        if (mShouldAnimate && position > mLastAnimatedPosition) {
            if (mFirstAnimatedPosition == -1) {
                // 只赋值一次
                mFirstAnimatedPosition = position;
            }
            animateView(position, view, animators);
            // 执行完animateView后，将mLastAnimatedPosition赋值为position
            mLastAnimatedPosition = position;
        }
    }

    private void animateView(final int position, final View view, final Animator[] animators) {
        if (mAnimationStartMillis == -1) {
            mAnimationStartMillis = SystemClock.uptimeMillis();
        }

        view.setAlpha(0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.setStartDelay(calculateAnimationDelay(position));
        set.setDuration(mAnimationDurationMillis);
        set.start();

        mAnimators.put(view.hashCode(), set);
    }

    private int calculateAnimationDelay(final int position) {
        int delay;

        int lastVisiblePosition = mListViewWrapper.getLastVisiblePosition();
        int firstVisiblePosition = mListViewWrapper.getFirstVisiblePosition();

        int numberOfItemsOnScreen = lastVisiblePosition - firstVisiblePosition;
        int numberOfAnimatedItems = position - 1 - mFirstAnimatedPosition;
        // 满足下面条件，就当它为第一次开始触发动画，将delay设为初始值
        if (numberOfItemsOnScreen + 1 < numberOfAnimatedItems) {
            delay = mAnimationDelayMillis;

            if (mListViewWrapper.getListView() instanceof GridView && CommonUtils.hasHoneycomb()) {
                int numColumns = ((GridView) mListViewWrapper.getListView()).getNumColumns();
                delay += mAnimationDelayMillis * (position % numColumns);
            }
        } else {
            int delaySinceStart = (position - mFirstAnimatedPosition) * mAnimationDelayMillis;
            delay = Math.max(0, (int) (-SystemClock.uptimeMillis() + mAnimationStartMillis + mInitialDelayMillis +
                    delaySinceStart));
        }
        return delay;
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putInt(SAVEDINSTANCESTATE_FIRSTANIMATEDPOSITION, mFirstAnimatedPosition);
        bundle.putInt(SAVEDINSTANCESTATE_LASTANIMATEDPOSITION, mLastAnimatedPosition);
        bundle.putBoolean(SAVEDINSTANCESTATE_SHOULDANIMATE, mShouldAnimate);

        return bundle;
    }

    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            mFirstAnimatedPosition = bundle.getInt(SAVEDINSTANCESTATE_FIRSTANIMATEDPOSITION);
            mLastAnimatedPosition = bundle.getInt(SAVEDINSTANCESTATE_LASTANIMATEDPOSITION);
            mShouldAnimate = bundle.getBoolean(SAVEDINSTANCESTATE_SHOULDANIMATE);
        }
    }
}
