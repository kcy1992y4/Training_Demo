package com.ryan_zhou.training_demo.utils.listviewanimations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.ryan_zhou.training_demo.listener.listviewanimations.OnItemMovedListener;
import com.ryan_zhou.training_demo.utils.CommonUtils;
import com.ryan_zhou.training_demo.widget.listviewanimations.DynamicListView;
import com.ryan_zhou.training_demo.widget.listviewanimations.HoverDrawable;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 16:19
 * @copyright TCL-MIE
 */
public class DragAndDropHandler implements TouchEventHandler {

    private static final int INVALID_ID = -1;

    private final DragAndDroplistViewWrapper mWrapper;

    private final ScrollHandler mScrollHandler;

    private final SwitchViewAnimator mSwitchViewAnimator;

    private final int mSlop;

    private ListAdapter mAdapter;

    /**
     * 这个Drawable 会在用户拖动item的时候生成，这个对象只会在没有拖动的情况下为空
     */
    private HoverDrawable mHoverDrawable;

    /**
     * 被点击的拖动的View，被点击拖动后，该View将会隐藏，然后由HoverDrawable代替
     */
    private View mMobileView;

    /**
     * 对应于mMobileView的ID，由Adapter.getItemId获取，记得要重写hasStableIds()并且返回true
     */
    private long mMobileItemId;

    /**
     * 用于初始化HoverDrawable 的downY变量
     */
    private float mLastMotionEventY = -1;

    /**
     * 对应于mMobileView的position，对应于Adapter的position
     */
    private int mOriginalMobileItemPosition = AdapterView.INVALID_POSITION;

    private DraggableManager mDraggableManager;

    private OnItemMovedListener mOnItemMovedListener;

    /**
     * MotionEvent.Action_Down 的时候初始化，由于计算Move过程的偏移量
     */
    private float mDownX;

    /**
     * MotionEvent.Action_Down 的时候初始化，由于计算Move过程的偏移量
     */
    private float mDownY;

    /**
     * 当在执行交换动画的时候 为true， as result of an up / cancel event.
     */
    private boolean mIsSettlingHoverDrawable;

    public DragAndDropHandler(DynamicListView dynamicListView) {
        this(new DynamicListViewWrapper(dynamicListView));
    }

    public DragAndDropHandler(DynamicListViewWrapper dynamicListViewWrapper) {
        mWrapper = dynamicListViewWrapper;
        if (mWrapper.getAdapter() != null) {
            setAdapterInternal(mWrapper.getAdapter());
        }

        mScrollHandler = new ScrollHandler();
        // listView 设置onScrollListener
        mWrapper.setOnScrollListener(mScrollHandler);

        mDraggableManager = new DefaultDraggableManager();

        if (CommonUtils.hasLollipop()) {
            mSwitchViewAnimator = new LSwitchViewAnimator();
        } else {
            mSwitchViewAnimator = new KitKatSwitchViewAnimator();
        }

        mMobileItemId = INVALID_ID;

        ViewConfiguration vc = ViewConfiguration.get(dynamicListViewWrapper.getListView().getContext());
        mSlop = vc.getScaledTouchSlop();
    }

    public void setAdapter(final ListAdapter adapter) {
        setAdapterInternal(adapter);
    }

    private void setAdapterInternal(final ListAdapter adapter) {
        ListAdapter actualAdapter = adapter;
        if (actualAdapter instanceof WrapperListAdapter) {
            actualAdapter = ((WrapperListAdapter) actualAdapter).getWrappedAdapter();
        }

        if (!actualAdapter.hasStableIds()) {
            throw new IllegalStateException("Adapter doesn't have stable ids! Make sure your adapter has stable ids, " +
                    "and override hasStableIds() to return true.");
        }

        if (!(actualAdapter instanceof Swappable)) {
            throw new IllegalArgumentException("Adapter should implement Swappable!");
        }
        mAdapter = actualAdapter;
    }

    public void setScrollSpeed(final float speed) {
        mScrollHandler.setScrollSpeed(speed);
    }

    /**
     * 开始拖动，初始化mMobileItemView和mHoverdrawable等数据
     * @param position
     */
    public void startDragging(int position) {
        // 已经在拖动了，直接返回
        if (mMobileItemId != INVALID_ID) {
            return;
        }

        if (mLastMotionEventY < 0) {
            throw new IllegalStateException("User must be touching the DynamicListView!");
        }

        if (mAdapter == null) {
            throw new IllegalStateException("This DynamicListView has no adapter set!");
        }

        if (position < 0 || position >= mAdapter.getCount()) {
            return;
        }

        mMobileView = mWrapper.getChildAt(position - mWrapper.getFirstVisiblePosition()  + mWrapper.getHeaderViewsCount());
        if (mMobileView != null) {
            mOriginalMobileItemPosition = position;
            mMobileItemId = mAdapter.getItemId(position);
            mHoverDrawable = new HoverDrawable(mMobileView, mLastMotionEventY);
            mMobileView.setVisibility(View.INVISIBLE);
        }
    }

    public void setDraggableManager(DraggableManager draggableManager) {
        mDraggableManager = draggableManager;
    }

    public void setOnItemMovedListener(OnItemMovedListener onItemMovedListener) {
        mOnItemMovedListener = onItemMovedListener;
    }

    private int getPositionForId(long itemId) {
        View v = getViewForId(itemId);
        if (v == null) {
            return AdapterView.INVALID_POSITION;
        } else {
            return mWrapper.getPositionForView(v);
        }
    }

    private View getViewForId(long itemId) {
        ListAdapter adapter = mAdapter;
        if (itemId == INVALID_ID || adapter == null) {
            return null;
        }
        int firstVisiblePosition = mWrapper.getFirstVisiblePosition();
        View result = null;
        for (int i = 0; i < mWrapper.getChildCount() && result == null; i++) {
            int position = firstVisiblePosition + i;
            if (position - mWrapper.getHeaderViewsCount() >= 0) {
                long id = adapter.getItemId(position - mWrapper.getHeaderViewsCount());
                if (id == itemId) {
                    result = mWrapper.getChildAt(i);
                }
            }
        }
        return result;
    }

    private void switchViews(View switchView, long switchId, float translationY) {
        assert mHoverDrawable != null;
        assert mAdapter != null;
        assert mMobileView != null;
        final int switchViewPosition = mWrapper.getPositionForView(switchView);
        int mobileViewPosition = mWrapper.getPositionForView(mMobileView);

        ((Swappable) mAdapter).swapItems(switchViewPosition - mWrapper.getHeaderViewsCount(), mobileViewPosition -
                mWrapper.getHeaderViewsCount());
        // 此时位置已经交换了。
        ((BaseAdapter) mAdapter).notifyDataSetChanged();
        // 移动的量为switchView的高度, 只会修改记录mHoverDrawable初始状态（点击的位置和左顶端Y坐标），事件mHoverDrawable已经通过moveEvent执行了
        mHoverDrawable.shift(switchView.getHeight());
        // 这个位移量为HoverDrawable的高度，注意：switchId没有改变过，就算执行了swapItems方法，switchId还是指向之前的View
        mSwitchViewAnimator.animateSwitchView(switchId, translationY);
    }

    @Override
    public boolean isInteracting() {
        return mMobileItemId != INVALID_ID;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        if (!mIsSettlingHoverDrawable) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionEventY = event.getY();
                    handled = handleDownEvent(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mLastMotionEventY = event.getY();
                    handled = handleMoveEvent(event);
                    break;
                case MotionEvent.ACTION_UP:
                    handled = handleUpEvent();
                    mLastMotionEventY = -1;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    handled = handleCancelEvent();
                    mLastMotionEventY = -1;
                    break;
                default:
                    handled = false;
                    break;
            }
        }
        return handled;
    }

    private boolean handleDownEvent(MotionEvent event) {
        mDownX = event.getRawX();
        mDownY = event.getRawY();
        return true;
    }

    private boolean handleMoveEvent(MotionEvent event) {
        boolean handled = false;
        float deltax = event.getRawX() - mDownX;
        float deltaY = event.getRawY() - mDownY;
        // 第一次操作Move事件，此时mHoverDrawable 为空
        if (mHoverDrawable == null && Math.abs(deltax) > mSlop && Math.abs(deltaY) > mSlop) {
            int position = mWrapper.pointToPosition((int) event.getX(), (int) event.getY());
            if (position != AdapterView.INVALID_POSITION) {
                View downView = mWrapper.getChildAt(position - mWrapper.getFirstVisiblePosition());
                assert downView != null;
                if (mDraggableManager.isDraggable(downView, position - mWrapper.getHeaderViewsCount(), event.getX() -
                        downView.getX(), event.getY() - downView.getY())) {
                    startDragging(position - mWrapper.getHeaderViewsCount());
                    handled = true;
                }
            }
        } else if (mHoverDrawable != null) {
            // 执行HoverDrawable的移动
            mHoverDrawable.handleMoveEvent(event);
            // 因为是对应每一个Move事件都执行这个方法，所以不用担心一次移动跨越了几个位置
            switchIfNecessary();
            mWrapper.getListView().invalidate();
            handled = true;
        }
        return handled;
    }

    private void switchIfNecessary() {
        if (mHoverDrawable == null || mAdapter == null) {
            return;
        }

        int position = getPositionForId(mMobileItemId);
        long aboveItemId = position - 1 - mWrapper.getHeaderViewsCount() >= 0 ? mAdapter.getItemId(position - 1 -
                mWrapper.getHeaderViewsCount()) : INVALID_ID;
        long belowItemId = position + 1 - mWrapper.getHeaderViewsCount() <= mAdapter.getCount() ? mAdapter.getItemId
                (position + 1 - mWrapper.getHeaderViewsCount()) : INVALID_ID;
        final long switchId = mHoverDrawable.isMovingUpwards() ? aboveItemId : belowItemId;
        View switchView = getViewForId(switchId);

        // 获取mHoverDrawable的偏移值
        final int deltaY = mHoverDrawable.getDeltaY();
        // 如果移动的距离大于一个mHoverDrawable的高度
        if (switchView != null && Math.abs(deltaY) > mHoverDrawable.getIntrinsicHeight()) {
            switchViews(switchView, switchId, mHoverDrawable.getIntrinsicHeight() * (deltaY < 0 ? -1 : 1));
        }

        mScrollHandler.handleMobileCellScroll();

        mWrapper.getListView().invalidate();
    }

    private boolean handleUpEvent() {
        if (mMobileView == null) {
            return false;
        }
        assert mHoverDrawable != null;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(mHoverDrawable.getTop(), (int) mMobileView.getY());
        SettleHoverDrawableAnimatorListener listener = new SettleHoverDrawableAnimatorListener(mHoverDrawable,
                mMobileView);
        valueAnimator.addUpdateListener(listener);
        valueAnimator.addListener(listener);
        valueAnimator.start();

        int newPosition = getPositionForId(mMobileItemId) - mWrapper.getHeaderViewsCount();
        if (mOriginalMobileItemPosition != newPosition && mOnItemMovedListener != null) {
            // TODO: 看具体OnItemMovedListener的实现
            mOnItemMovedListener.onItemMoved(mOriginalMobileItemPosition, newPosition);
        }
        return true;
    }

    private boolean handleCancelEvent() {
        return handleUpEvent();
    }

    /**
     * 在ListView中绘制mHoverDrawable
     * @param canvas
     */
    public void dispatchDraw(final Canvas canvas) {
        if (mHoverDrawable != null) {
            mHoverDrawable.draw(canvas);
        }
    }

    private static class DefaultDraggableManager implements DraggableManager {

        @Override
        public boolean isDraggable(View view, int position, float x, float y) {
            return false;
        }
    }

    private interface SwitchViewAnimator {
        /**
         * @param switchId 要被 被动 移动的View
         * @param translationY 一般是 主动 移动的View的高度
         */
        void animateSwitchView(long switchId, float translationY);
    }

    /**
     * A {@link SwitchViewAnimator} for versions KitKat and below.
     * This class immediately updates {@link #mMobileView} to be the newly mobile view.
     * 立即更新mMobileView的位置
     */
    private class KitKatSwitchViewAnimator implements SwitchViewAnimator {

        @Override
        public void animateSwitchView(long switchId, float translationY) {
            assert mMobileView != null;
            mWrapper.getListView().getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener
                    (mMobileView, switchId, translationY));
            mMobileView = getViewForId(mMobileItemId);
        }

        private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

            private final View mPreviousMobileView;

            private final long mSwitchId;

            private final float mTranslationY;

            AnimateSwitchViewOnPreDrawListener(final View previousMobileView, long switchId, float translationY) {
                mPreviousMobileView = previousMobileView;
                mSwitchId = switchId;
                mTranslationY = translationY;
            }

            @Override
            public boolean onPreDraw() {
                mWrapper.getListView().getViewTreeObserver().removeOnPreDrawListener(this);

                View switchView = getViewForId(mSwitchId);
                if (switchView != null) {
                    switchView.setTranslationY(mTranslationY);
                    switchView.animate().translationY(0).start();
                }
                // 这里又将switchView弄可见
                mPreviousMobileView.setVisibility(View.VISIBLE);
                // 由于改变了mMobileView的值
                if (mMobileView != null) {
                    mMobileView.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        }
    }

    /**
     * A {@link SwitchViewAnimator} for versions L and above.
     * This class updates {@link #mMobileView} only after the next frame has been drawn.
     * 在下一帧绘制的时候才会改变mMobileView
     */
    private class LSwitchViewAnimator implements SwitchViewAnimator {

        @Override
        public void animateSwitchView(long switchId, float translationY) {
            mWrapper.getListView().getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener
                    (switchId, translationY));
        }

        private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

            private final long mSwitchId;

            private final float mTranslationY;

            AnimateSwitchViewOnPreDrawListener(final long switchId, final float translationY) {
                mSwitchId = switchId;
                mTranslationY = translationY;
            }

            @Override
            public boolean onPreDraw() {
                mWrapper.getListView().getViewTreeObserver().removeOnPreDrawListener(this);

                View switchView = getViewForId(mSwitchId);
                if (switchView != null) {
                    switchView.setTranslationY(mTranslationY);
                    switchView.animate().translationY(0).start();
                }

                assert mMobileView != null;
                mMobileView.setVisibility(View.VISIBLE);
                mMobileView = getViewForId(mMobileItemId);
                assert mMobileView != null;
                mMobileView.setVisibility(View.INVISIBLE);
                return true;
            }
        }
    }

    /**
     * A class which handles scrolling for this {@code DynamicListView} when dragging an item.
     */
    private class ScrollHandler implements AbsListView.OnScrollListener {

        private static final int SMOOTH_SCROLL_DP = 3;

        private final int mSmoothScrollPx;

        private float mScrollSpeedFactor = 1.0f;

        private int mPreviousFirstVisibleItem = -1;

        private int mPreviousLastVisibleItem = -1;

        private int mCurrentFirstVisibleItem;

        private int mCurrentLastVisibleItem;

        ScrollHandler() {
            Resources r = mWrapper.getListView().getResources();
            mSmoothScrollPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SMOOTH_SCROLL_DP, r
                    .getDisplayMetrics());
        }

        void setScrollSpeed(float scrollSpeedFactor) {
            mScrollSpeedFactor = scrollSpeedFactor;
        }

        /**
         * Scrolls the {@code DynamicListView} if the hover drawable is above or below the bounds of the {@code
         * ListView}.
         */
        void handleMobileCellScroll() {
            if (mHoverDrawable == null || mIsSettlingHoverDrawable) {
                return;
            }
            Rect r = mHoverDrawable.getBounds();
            int offset = mWrapper.computeVerticalScrollOffset();
            int height = mWrapper.getListView().getHeight();
            int extent = mWrapper.computeVerticalScrollExtent();
            int range = mWrapper.computeVerticalScrollRange();
            int hoverViewTop = r.top;
            int hoverHeight = r.height();

            int scrollPx = (int) Math.max(1, mSmoothScrollPx * mScrollSpeedFactor);
            if (hoverViewTop <= 0 && offset > 0) {
                mWrapper.smoothScrollBy(-scrollPx, 0);
            } else if (hoverViewTop + hoverHeight >= height && offset + extent < range) {
                mWrapper.smoothScrollBy(scrollPx, 0);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mCurrentFirstVisibleItem = firstVisibleItem;
            mCurrentLastVisibleItem = firstVisibleItem + visibleItemCount;

            mPreviousFirstVisibleItem = mPreviousFirstVisibleItem == -1 ? mCurrentFirstVisibleItem :
                    mPreviousFirstVisibleItem;
            mPreviousLastVisibleItem = mPreviousLastVisibleItem == -1 ? mCurrentLastVisibleItem :
                    mPreviousLastVisibleItem;

            // 当平常的滚动，mHoverDrawable 为 null，因此不执行内部代码
            if (mHoverDrawable != null) {
                assert mMobileView != null;
                float y = mMobileView.getY();
                mHoverDrawable.onScroll(y);
            }
            // 当平常的滚动，mHoverDrawable 为 null，而下面两个方法都要在mHoverDrawable不为null 的情况下才可以执行
            // 当在执行交换动画的时候，mIsSettlingHoverDrawable 为ture
            if (!mIsSettlingHoverDrawable) {
                checkAndHandleFirstVisibleCellChange();
                checkAndHandleLastVisibleCellChange();
            }

            mPreviousFirstVisibleItem = mCurrentFirstVisibleItem;
            mPreviousLastVisibleItem = mCurrentLastVisibleItem;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && mHoverDrawable != null) {
                handleMobileCellScroll();
            }
        }

        /**
         * 在头出现新的Item
         * TODO:这样直接替换效果不会不好吗？
         */
        private void checkAndHandleFirstVisibleCellChange() {
            if (mHoverDrawable == null || mAdapter == null || mCurrentFirstVisibleItem >= mPreviousFirstVisibleItem) {
                return;
            }
            int position = getPositionForId(mMobileItemId);
            if (position == AdapterView.INVALID_POSITION) {
                return;
            }
            long switchItemId = position - 1 - mWrapper.getHeaderViewsCount() >= 0 ? mAdapter.getItemId(position - 1
                    - mWrapper.getHeaderViewsCount()) : INVALID_ID;

            View switchView = getViewForId(switchItemId);
            if (switchView != null) {
                switchViews(switchView, switchItemId, -switchView.getHeight());
            }
        }

        /**
         * 在尾出现新的Item
         */
        private void checkAndHandleLastVisibleCellChange() {
            if (mHoverDrawable == null || mAdapter == null || mCurrentLastVisibleItem <= mPreviousLastVisibleItem) {
                return;
            }

            int position = getPositionForId(mMobileItemId);
            if (position == AdapterView.INVALID_POSITION) {
                return;
            }

            long switchItemId = position + 1 - mWrapper.getHeaderViewsCount() < mAdapter.getCount() ? mAdapter.getItemId(position + 1 - mWrapper.getHeaderViewsCount()) : INVALID_ID;
            View switchView = getViewForId(switchItemId);
            if (switchView != null) {
                switchViews(switchView, switchItemId, switchView.getHeight());
            }
        }
    }

    private class SettleHoverDrawableAnimatorListener extends AnimatorListenerAdapter implements ValueAnimator
            .AnimatorUpdateListener {

        private final HoverDrawable mAnimatingHoverDrawable;

        private final View mAnimatingMobileView;

        private SettleHoverDrawableAnimatorListener(final HoverDrawable animatingHoverDrawable, final View
                animatingMobileView) {
            mAnimatingHoverDrawable = animatingHoverDrawable;
            mAnimatingMobileView = animatingMobileView;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mIsSettlingHoverDrawable = true;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mAnimatingHoverDrawable.setTop((Integer) animation.getAnimatedValue());
            mWrapper.getListView().postInvalidate();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAnimatingMobileView.setVisibility(View.VISIBLE);

            mHoverDrawable = null;
            mMobileView = null;
            mMobileItemId = INVALID_ID;
            mOriginalMobileItemPosition = AdapterView.INVALID_POSITION;

            mIsSettlingHoverDrawable = false;
        }
    }
}
