package com.ryan_zhou.training_demo.activity.listviewanimations;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.adapter.listviewanimations.GoogleCardsAdapter;
import com.ryan_zhou.training_demo.adapter.listviewanimations.SwingBottomInAnimationAdapter;
import com.ryan_zhou.training_demo.adapter.listviewanimations.SwipeDismissAdapter;
import com.ryan_zhou.training_demo.utils.listviewanimations.OnDismissCallback;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/28 17:36
 * @copyright TCL-MIE
 */
public class GoogleCardsActivity extends BaseActivity implements OnDismissCallback {

    private static final int INITAL_DELAY_MILLIS = 300;

    private GoogleCardsAdapter mGoogleCardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlecards);

        ListView listView = (ListView)this.findViewById(R.id.activity_googlecards_listview);

        mGoogleCardsAdapter = new GoogleCardsAdapter(this);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mGoogleCardsAdapter, this));
        swingBottomInAnimationAdapter.setAbsListView(listView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITAL_DELAY_MILLIS);

        listView.setAdapter(swingBottomInAnimationAdapter);
        for (int i = 0; i < 100; i++) {
            mGoogleCardsAdapter.add(i);
        }
    }

    @Override
    public void onDismiss(ViewGroup listView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mGoogleCardsAdapter.remove(position);
        }
    }
}
