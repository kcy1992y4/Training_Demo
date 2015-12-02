package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ryan_zhou.training_demo.utils.listviewanimations.AnimatorUtils;
import com.ryan_zhou.training_demo.utils.listviewanimations.BaseAdapterDecorator;
import com.ryan_zhou.training_demo.utils.listviewanimations.ListViewWrapper;
import com.ryan_zhou.training_demo.widget.listviewanimations.ViewAnimator;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 15:21
 * @copyright TCL-MIE
 */
public abstract class AnimationAdapter extends BaseAdapterDecorator {

    private static final String SAVEDINSTANCESTATE_VIEWANIMATOR = "savedinstancestate_viewanimator";

    private static final String ALPHA = "alpha";

    private ViewAnimator mViewAnimator;

    private boolean mIsRootAdapter;

    private boolean mGridViewPossiblyMeasuring;

    private int mGridViewMeasuringPosition;

    protected AnimationAdapter(final BaseAdapter baseAdapter) {
        super(baseAdapter);

        mGridViewPossiblyMeasuring = true;
        mGridViewMeasuringPosition = -1;
        mIsRootAdapter = true;

        if (baseAdapter instanceof AnimationAdapter) {
            ((AnimationAdapter) baseAdapter).setIsWrapped();
        }
    }

    /**
     * 要在 setAbsListView 中触发
     * @param listViewWrapper
     */
    @Override
    public void setListViewWrapper(ListViewWrapper listViewWrapper) {
        super.setListViewWrapper(listViewWrapper);
        mViewAnimator = new ViewAnimator(listViewWrapper);
    }

    private void setIsWrapped() {
        mIsRootAdapter = false;
    }

    public void reset() {
        if (getListViewWrapper() == null) {
            throw new IllegalStateException("Call setAbsListView on this AnimationAdapter first!");
        }
        assert mViewAnimator != null;
        mViewAnimator.reset();
        mGridViewMeasuringPosition = -1;
        mGridViewPossiblyMeasuring = true;

        if (getDecoratedBaseAdapter() instanceof AnimationAdapter) {
            ((AnimationAdapter) getDecoratedBaseAdapter()).reset();
        }
    }

    public ViewAnimator getViewAnimator() {
        return mViewAnimator;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mIsRootAdapter) {
            if (getListViewWrapper() == null) {
                throw new IllegalStateException("Call setAbsListView() on this AnimationAdapter first!");
            }
            assert mViewAnimator != null;
            if (convertView != null) {
                mViewAnimator.cancelExistingAnimation(convertView);
            }
        }
        // 返回的是构造函数中Adapter的itemView，先理解为数据view吧。。
        View itemView = super.getView(position, convertView, parent);
        // 只有嵌套在最完成的AnimationAdapter 才会触发animateViewIfNecessary,其他的将会直接返回不会执行动画的ItemView
        if (mIsRootAdapter) {
            animateViewIfNecessary(position, itemView, parent);
        }
        return itemView;
    }

    private void animateViewIfNecessary(final int position, final View view, final ViewGroup parent) {
        assert mViewAnimator != null;

        /* GridView measures the first View which is returned by getView(int, View, ViewGroup), but does not use that
         View.
           On KitKat, it does this actually multiple times.
           Therefore, we animate all these first Views, and reset the last animated position when we suspect GridView
            is measuring. */
        mGridViewPossiblyMeasuring = mGridViewPossiblyMeasuring && (mGridViewMeasuringPosition == -1 ||
                mGridViewMeasuringPosition == position);

        // 将last animated position 设为-1，是使第一次以后的position=0的getView触发动画的时候满足mViewAnimator.animateViewIfNecessary的条件
        // 使之后的position=0 都执行动画，只有MGridViewPossibleMeasuring一次为false，之后都为false
        if (mGridViewPossiblyMeasuring) {
            mGridViewMeasuringPosition = position;
            mViewAnimator.setLastAnimatedPosition(-1);
        }

        // 层层嵌套，先执行嵌套在最里面的动画，这样的做法应该是便于动画组合
        // 将嵌套在里面的AnimationAdapter的Animation都获取并且让它跟嵌套在最外面的AnimationAdapter的Animation结合在一起，并执行动画。
        Animator[] childAnimators;
        if (getDecoratedBaseAdapter() instanceof AnimationAdapter) {
            childAnimators = ((AnimationAdapter) getDecoratedBaseAdapter()).getAnimators(parent, view);
        } else {
            childAnimators = new Animator[0];
        }

        Animator[] animators = getAnimators(parent, view);
        Animator alphaAnimator = ObjectAnimator.ofFloat(view, ALPHA, 0, 1);

        Animator[] concatAnimators = AnimatorUtils.concatAnimators(childAnimators, animators, alphaAnimator);
        mViewAnimator.animateViewIfNecessary(position, view, concatAnimators);
    }

    public abstract Animator[] getAnimators(ViewGroup parent, View view);

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        if (mViewAnimator != null) {
            bundle.putParcelable(SAVEDINSTANCESTATE_VIEWANIMATOR, mViewAnimator.onSaveInstanceState());
        }
        return bundle;
    }

    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            if (mViewAnimator != null) {
                mViewAnimator.onRestoreInstanceState(bundle.getParcelable(SAVEDINSTANCESTATE_VIEWANIMATOR));
            }
        }
    }
}
