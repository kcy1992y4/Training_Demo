package com.ryan_zhou.training_demo.activity.listviewanimations;

import android.os.Bundle;
import android.widget.ListView;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.adapter.listviewanimations.MyListAdapter;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/11/11 14:43
 * @copyright TCL-MIE
 */
public class MyListActivity extends BaseActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);
        mListView = (ListView)this.findViewById(R.id.activity_mylist_listview);
    }

    public ListView getListView() {
        return mListView;
    }

    protected MyListAdapter createListAdapter() {
        return new MyListAdapter(this);
    }
}
