package com.ryan_zhou.training_demo.widget.listviewanimations;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ryan_zhou.training_demo.adapter.listviewanimations.AnimateAdditionAdapter;
import com.ryan_zhou.training_demo.adapter.listviewanimations.SwipeUndoAdapter;
import com.ryan_zhou.training_demo.listener.listviewanimations.OnItemMovedListener;
import com.ryan_zhou.training_demo.listener.listviewanimations.SwipeDismissTouchListener;
import com.ryan_zhou.training_demo.listener.listviewanimations.SwipeTouchListener;
import com.ryan_zhou.training_demo.listener.listviewanimations.SwipeUndoTouchListener;
import com.ryan_zhou.training_demo.utils.CommonUtils;
import com.ryan_zhou.training_demo.utils.listviewanimations.BaseAdapterDecorator;
import com.ryan_zhou.training_demo.utils.listviewanimations.DismissableManager;
import com.ryan_zhou.training_demo.utils.listviewanimations.DragAndDropHandler;
import com.ryan_zhou.training_demo.utils.listviewanimations.DraggableManager;
import com.ryan_zhou.training_demo.utils.listviewanimations.DynamicListViewWrapper;
import com.ryan_zhou.training_demo.utils.listviewanimations.Insertable;
import com.ryan_zhou.training_demo.utils.listviewanimations.OnDismissCallback;
import com.ryan_zhou.training_demo.utils.listviewanimations.TouchEventHandler;
import com.ryan_zhou.training_demo.utils.listviewanimations.UndoCallback;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 15:55
 * @copyright TCL-MIE
 */
public class DynamicListView extends ListView {

    private static final String TAG = "DynamicListView";

    private final MyOnScrollListener mMyOnScrollListener;

    private DragAndDropHandler mDragAndDropHandler;

    private SwipeTouchListener mSwipeTouchListener;

    /**
     * 现在执行点击事件的Handler，可能是mDragAndDropHandler或者是mSwipeTouchListener
     */
    private TouchEventHandler mCurrentHandlingTouchEventHandler;

    private AnimateAdditionAdapter<Object> mAnimateAdditionAdapter;

    private SwipeUndoAdapter mSwipeUndoAdapter;


    public DynamicListView(Context context) {
        this(context, null);
    }

    public DynamicListView(Context context, AttributeSet atts) {
        this(context, atts, Resources.getSystem().getIdentifier("listViewStyle", "attr", "android"));
    }

    public DynamicListView(Context context, AttributeSet atts, int defStyle) {
        super(context, atts, defStyle);

        mMyOnScrollListener = new MyOnScrollListener();
        super.setOnScrollListener(mMyOnScrollListener);
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        if (onTouchListener instanceof SwipeTouchListener) {
            return;
        }
        super.setOnTouchListener(onTouchListener);
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mMyOnScrollListener.addOnScrollListener(onScrollListener);
    }

    public void enableDragAndDrop() {
        if (!CommonUtils.hasIceCreamSandwich()) {
            throw new UnsupportedOperationException("Drag and drop is only supported API levels 14 and up!");
        }
        mDragAndDropHandler = new DragAndDropHandler(this);
    }

    public void disableDragAndDrop() {
        mDragAndDropHandler = null;
    }

    public void enableSwipeToDismiss(final OnDismissCallback onDismissCallback) {
        mSwipeTouchListener = new SwipeDismissTouchListener(new DynamicListViewWrapper(this), onDismissCallback);
    }

    public void enableSwipeUndo(UndoCallback undoCallback) {
        mSwipeTouchListener = new SwipeUndoTouchListener(new DynamicListViewWrapper(this), undoCallback);
    }

    public void enableSimpleSwipeUndo() {
        if (mSwipeUndoAdapter == null) {
            throw new IllegalStateException("enableSimpleSwipeUndo requires a SwipeUndoAdapter to be set as an " +
                    "adapter");
        }
        mSwipeTouchListener = new SwipeUndoTouchListener(new DynamicListViewWrapper(this), mSwipeUndoAdapter
                .getUndoCallback());
        mSwipeUndoAdapter.setSwipeUndoTouchListener((SwipeUndoTouchListener) mSwipeTouchListener);
    }

    public void disableSwipeToDismiss() {
        mSwipeTouchListener = null;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        ListAdapter wrappedAdapter = adapter;
        mSwipeUndoAdapter = null;
        if (adapter instanceof BaseAdapter) {
            BaseAdapter rootAdapter = (BaseAdapter) wrappedAdapter;
            while (rootAdapter instanceof BaseAdapterDecorator) {
                if (rootAdapter instanceof SwipeUndoAdapter) {
                    mSwipeUndoAdapter = (SwipeUndoAdapter) rootAdapter;
                }
                rootAdapter = ((BaseAdapterDecorator) rootAdapter).getDecoratedBaseAdapter();
            }

            if (rootAdapter instanceof Insertable) {
                mAnimateAdditionAdapter = new AnimateAdditionAdapter((BaseAdapter) wrappedAdapter);
                mAnimateAdditionAdapter.setListView(this);
                wrappedAdapter = mAnimateAdditionAdapter;
            }
        }

        super.setAdapter(wrappedAdapter);

        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setAdapter(wrappedAdapter);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mCurrentHandlingTouchEventHandler == null) {
            boolean firstTimeInteracting = false;
            // 不支持拖拉操作，如果有Item在Undo状态
            if (!(mSwipeTouchListener instanceof SwipeUndoTouchListener) || !((SwipeUndoTouchListener)
                    mSwipeTouchListener).hasPendingItems()) {
                if (mDragAndDropHandler != null) {
                    mDragAndDropHandler.onTouchEvent(event);
                    firstTimeInteracting = mDragAndDropHandler.isInteracting();
                    if (firstTimeInteracting) {
                        // 如果在拖动交互中，则取消滑动交互
                        mCurrentHandlingTouchEventHandler = mDragAndDropHandler;
                        sendCancelEvent(mSwipeTouchListener, event);
                    }
                }
            }

            if (mCurrentHandlingTouchEventHandler == null && mSwipeTouchListener != null) {
                mSwipeTouchListener.onTouchEvent(event);
                firstTimeInteracting = mSwipeTouchListener.isInteracting();
                if (firstTimeInteracting) {
                    // 如果在滑动交互中，则取消拖动交互
                    mCurrentHandlingTouchEventHandler = mSwipeTouchListener;
                    sendCancelEvent(mDragAndDropHandler, event);
                }
            }
            // 如果在滑动或拖动交互中，则取消ListView的滚动交互
            if (firstTimeInteracting) {
                MotionEvent cancelEvent = MotionEvent.obtain(event);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                super.onTouchEvent(cancelEvent);
            }
            // 如果是拖动或滑动交互，则返回true，下一次将直接调用该类的onTouchEvent
            return firstTimeInteracting || super.dispatchTouchEvent(event);
        } else {
            // 已经在出来拖动或者滑动事件
            return onTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCurrentHandlingTouchEventHandler != null) {
            mCurrentHandlingTouchEventHandler.onTouchEvent(event);
        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            mCurrentHandlingTouchEventHandler = null;
        }
        return mCurrentHandlingTouchEventHandler != null || super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 在ListView中绘制mHoverDrawable
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.dispatchDraw(canvas);
        }
    }

    private void sendCancelEvent(TouchEventHandler touchEventHandler, MotionEvent motionEvent) {
        if (touchEventHandler != null) {
            MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
            cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
            touchEventHandler.onTouchEvent(cancelEvent);
        }
    }

    @Override
    public int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    public void insert(final int index, final Object item) {
        if (mAnimateAdditionAdapter == null) {
            throw new IllegalStateException("Adapter should implement Insertable!");
        }
        mAnimateAdditionAdapter.insert(index, item);
    }

    public <T> void insert(Pair<Integer, T>... indexItemPairs) {
        if (mAnimateAdditionAdapter == null) {
            throw new IllegalStateException("Adapter should implement Insertable!");
        }
        ((AnimateAdditionAdapter<T>) mAnimateAdditionAdapter).insert(indexItemPairs);
    }

    public <T> void insert(Iterable<Pair<Integer, T>> indexItemPairs) {
        if (mAnimateAdditionAdapter == null) {
            throw new IllegalStateException("Adapter should implement Insertable!");
        }
        ((AnimateAdditionAdapter<T>) mAnimateAdditionAdapter).insert(indexItemPairs);
    }

    public void setDraggableManager(DraggableManager draggableManager) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setDraggableManager(draggableManager);
        }
    }

    public void setOnItemMovedListener(final OnItemMovedListener onItemMovedListener) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setOnItemMovedListener(onItemMovedListener);
        }
    }

    public void startDragging(final int position) {
        if (mSwipeTouchListener instanceof SwipeUndoTouchListener && ((SwipeUndoTouchListener) mSwipeTouchListener)
                .hasPendingItems()) {
            return;
        }

        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.startDragging(position);
        }
    }

    public void setScrollSpeed(float speed) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.setScrollSpeed(speed);
        }
    }

    public void setDismissableManager(DismissableManager dismissableManager) {
        if (mSwipeTouchListener != null) {
            mSwipeTouchListener.setDismissableManager(dismissableManager);
        }
    }

    public void fling(int position) {
        if (mSwipeTouchListener != null) {
            mSwipeTouchListener.fling(position);
        }
    }

    public void setSwipeTouchChild(final int childResId) {
        if (mSwipeTouchListener != null) {
            mSwipeTouchListener.setTouchChild(childResId);
        }
    }

    public void setMinimumAlpha(float minimumAlpha) {
        if (mSwipeTouchListener != null) {
            mSwipeTouchListener.setMinimumAlpha(minimumAlpha);
        }
    }

    public void dismiss(int position) {
        if (mSwipeTouchListener != null) {
            if (mSwipeTouchListener instanceof SwipeDismissTouchListener) {
                ((SwipeDismissTouchListener) mSwipeTouchListener).dismiss(position);
            } else {
                throw new IllegalStateException("Enabled swipe functionality does not support dismiss");
            }
        }
    }

    public void undo(View view) {
        if (mSwipeTouchListener != null) {
            if (mSwipeTouchListener instanceof SwipeUndoTouchListener) {
                ((SwipeUndoTouchListener) mSwipeTouchListener).undo(view);
            } else {
                throw new IllegalStateException("Enabled swipe functionality does not support undo");
            }
        }
    }

    private class MyOnScrollListener implements OnScrollListener {

        private final Collection<OnScrollListener> mOnScrollListeners = new HashSet<>();

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            for (OnScrollListener onScrollListener : mOnScrollListeners) {
                onScrollListener.onScrollStateChanged(view, scrollState);
            }
            // TODO: 在滚动过程中，UndoPosition直接消失？？
            if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                if (mSwipeTouchListener instanceof SwipeUndoTouchListener) {
                    ((SwipeUndoTouchListener) mSwipeTouchListener).dimissPending();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            for (OnScrollListener onScrollListener : mOnScrollListeners) {
                onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

        public void addOnScrollListener(OnScrollListener onScrollListener) {
            mOnScrollListeners.add(onScrollListener);
        }
    }

}
