package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ryan_zhou.training_demo.utils.listviewanimations.AbsListViewWrapper;
import com.ryan_zhou.training_demo.utils.listviewanimations.AdapterViewUtil;
import com.ryan_zhou.training_demo.utils.listviewanimations.BaseAdapterDecorator;
import com.ryan_zhou.training_demo.utils.listviewanimations.InsertQueue;
import com.ryan_zhou.training_demo.utils.listviewanimations.Insertable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/18 20:06
 * @copyright TCL-MIE
 */
public class AnimateAdditionAdapter<T> extends BaseAdapterDecorator {

    private static final long DEFAULT_SCROLLDOWN_ANIMATION_MS = 300;

    private long mScrolldownAnimationDurationMs = DEFAULT_SCROLLDOWN_ANIMATION_MS;

    private static final long DEFAULT_INSERTION_ANIMATION_MS = 300;

    private long mInsertionAnimationDurationMs = DEFAULT_INSERTION_ANIMATION_MS;

    private static final String ALPHA = "alpha";

    private final Insertable<T> mInsertable;

    private final InsertQueue<T> mInsertQueue;

    private boolean mShouldAnimateDown = true;

    public AnimateAdditionAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
        BaseAdapter rootAdapter = getRootAdapter();
        if (!(rootAdapter instanceof Insertable)) {
            throw new IllegalArgumentException("BaseAdapter should implement Insertable!");
        }
        mInsertable = (Insertable<T>) rootAdapter;
        mInsertQueue = new InsertQueue<>(mInsertable);
    }

    @Override
    public void setAbsListView(AbsListView absListView) {
        if (absListView instanceof ListView) {
            setListView((ListView) absListView);
        } else {
            throw new IllegalArgumentException("AnimateAdditionAdapter require a ListView!");
        }
    }

    public void setListView(ListView listView) {
        setListViewWrapper(new AbsListViewWrapper(listView));
    }

    public void setShouldAnimateDown(boolean shouldAnimateDown) {
        mShouldAnimateDown = shouldAnimateDown;
    }

    public void setScrolldownAnimationDuration(long scrolldownAnimationDurationMs) {
        mScrolldownAnimationDurationMs = scrolldownAnimationDurationMs;
    }

    public void setInsertionAnimationDuration(long insertionAnimationDurationMs) {
        mInsertionAnimationDurationMs = insertionAnimationDurationMs;
    }

    public void insert(int index, T item) {
        insert(new Pair<>(index, item));
    }

    public void insert(int index, T... items) {
        Pair<Integer, T>[] pairs = new Pair[items.length];
        for (int i = 0; i < items.length; i++) {
            pairs[i] = new Pair<>(index + i, items[i]);
        }
        insert(pairs);
    }

    public void insert(Pair<Integer, T>... indexItemPairs) {
        insert(Arrays.asList(indexItemPairs));
    }

    /**
     * 在整个方法执行过程中，ListView visible的Item没有改变过
     * @param indexItemPairs
     */
    public void insert(final Iterable<Pair<Integer, T>> indexItemPairs) {
        if (getListViewWrapper() == null) {
            throw new IllegalStateException("Call setListView on this AnimateAdditionAdapter!");
        }
        // 中间插入的position
        Collection<Pair<Integer, T>> visibleViews = new ArrayList<>();
        // 上面插入的position
        Collection<Integer> insertedPositions = new ArrayList<>();
        // 下面插入的position
        Collection<Integer> insertedBelowPositions = new ArrayList<>();

        int scrollDistance = 0;
        int numInsertedAbove = 0;

        for (Pair<Integer, T> pair : indexItemPairs) {
            // 在firstVisibleItem前插入Item
            if (getListViewWrapper().getFirstVisiblePosition() > pair.first) {
                int index = pair.first;
                // 根据前面是否先插入了一个，如果是，则插入的index需要+1
                // 三个判断都实现了这样的代码，功能是如果在要插入的位置前面之前已经插入过，要根据插入的个数，偏移多少个位置
                for (int insertedPosition : insertedPositions) {
                    if (index >= insertedPosition) {
                        index++;
                    }
                }

                mInsertable.add(index, pair.second);
                insertedPositions.add(index);
                numInsertedAbove++;

                if (mShouldAnimateDown) {
                    View view = getView(pair.first, null, getListViewWrapper().getListView());
                    view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                            .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    scrollDistance -= view.getMeasuredHeight();
                }
                // 当添加Item到ListView可见的位置中
            } else if (getListViewWrapper().getLastVisiblePosition() >= pair.first || getListViewWrapper()
                    .getLastVisiblePosition() == AdapterView.INVALID_POSITION || !childrenFillAbsListView()) {
                int index = pair.first;

                for (int insertedPosition : insertedPositions) {
                    if (index >= insertedPosition) {
                        index++;
                    }
                }
                Pair<Integer, T> newPair = new Pair<>(index, pair.second);
                visibleViews.add(newPair);
            } else {
                // 因为visibleViews在这方法执行完后才执行插入
                int index = pair.first;

                for (int insertedPosition : insertedPositions) {
                    if (index >= insertedPosition) {
                        index++;
                    }
                }

                for (int queuedPosition : insertedBelowPositions) {
                    if (index >= queuedPosition) {
                        index++;
                    }
                }

                insertedBelowPositions.add(index);
                // 插入到相应的位置
                mInsertable.add(index, pair.second);
            }
        }

        if (mShouldAnimateDown) {
            ((AbsListView) getListViewWrapper().getListView()).smoothScrollBy(scrollDistance, (int)
                    (mScrolldownAnimationDurationMs) * numInsertedAbove);
        }
        mInsertQueue.insert(visibleViews);
        // 获取的还是现在的没有新添加Item的FirstVisiblePosition
        int firstVisiblePosition = getListViewWrapper().getFirstVisiblePosition();
//        View firstChild = AdapterViewUtil.getViewForPosition(getListViewWrapper() ,firstVisiblePosition);
        View firstChild = getListViewWrapper().getChildAt(0);
        int childTop = firstChild == null ? 0 : firstChild.getTop();
        ((ListView) getListViewWrapper().getListView()).setSelectionFromTop(firstVisiblePosition + numInsertedAbove,
                childTop - getListViewWrapper().getListView().getPaddingTop());
    }

    private boolean childrenFillAbsListView() {
        if (getListViewWrapper() == null) {
            throw new IllegalStateException("Call setListView on this AnimateAdditionAdapter first!");
        }

        int childrenHeight = 0;
        for (int i = 0; i < getListViewWrapper().getCount(); i++) {
            View child = getListViewWrapper().getChildAt(i);
            if (child != null) {
                childrenHeight += child.getHeight();
            }
        }
        return getListViewWrapper().getListView().getHeight() <= childrenHeight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 擦，这个Adapter只负责Animation..
        final View view = super.getView(position, convertView, parent);

        //  这样做可能会引起View重用的时候动画没有消失, 但是动画时间交短，所以没什么影响
        if (mInsertQueue.getActiveIndexes().contains(position)) {
            // 宽度至少是MATCH_PARENT
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT, View
                    .MeasureSpec.AT_MOST);
            // 高度没有限制
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View
                    .MeasureSpec.UNSPECIFIED);
            view.measure(widthMeasureSpec, heightMeasureSpec);

            int originalHeight = view.getMeasuredHeight();

            ValueAnimator heightAnimator = ValueAnimator.ofInt(1, originalHeight);
            heightAnimator.addUpdateListener(new HeightUpdater(view));

            Animator[] customAnimators = getAdditionalAnimators(view, parent);
            Animator[] animators = new Animator[customAnimators.length + 1];
            animators[0] = heightAnimator;
            System.arraycopy(customAnimators, 0, animators, 1, customAnimators.length);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);

            view.setAlpha(0);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, ALPHA, 0, 1);

            AnimatorSet allAnimatorsSet = new AnimatorSet();
            allAnimatorsSet.playSequentially(animatorSet, alphaAnimator);

            allAnimatorsSet.setDuration(mInsertionAnimationDurationMs);
            allAnimatorsSet.addListener(new ExpandAnimationListener(position));
            allAnimatorsSet.start();
        }
        return view;
    }

    protected Animator[] getAdditionalAnimators(final View view, final ViewGroup parent) {
        return new Animator[]{};
    }

    private static class HeightUpdater implements ValueAnimator.AnimatorUpdateListener {

        private final View mView;

        HeightUpdater(View view) {
            mView = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
            layoutParams.height = (Integer) animation.getAnimatedValue();
            mView.setLayoutParams(layoutParams);
        }
    }

    private class ExpandAnimationListener extends AnimatorListenerAdapter{
        private int mPosition;

        ExpandAnimationListener(int position) {
            mPosition = position;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mInsertQueue.removeActiveIndex(mPosition);
        }
    }
}
