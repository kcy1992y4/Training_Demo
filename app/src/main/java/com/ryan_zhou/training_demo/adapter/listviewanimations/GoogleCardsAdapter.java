package com.ryan_zhou.training_demo.adapter.listviewanimations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryan_zhou.training_demo.R;
import com.ryan_zhou.training_demo.utils.listviewanimations.BitmapCache;

/**
 * @author chaohao.zhou
 * @Description:
 * @date 2015/10/29 11:15
 * @copyright TCL-MIE
 */
public class GoogleCardsAdapter extends ArrayAdapter<Integer> {

    private final Context mContext;
    private final BitmapCache mMemoryCache;

    public GoogleCardsAdapter(Context context) {
        mContext = context;
        mMemoryCache = new BitmapCache();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_googlecards_card, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.textView);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(mContext.getString(R.string.card_number, getItem(position) + 1));
        setImageView(viewHolder, position);
        return view;
    }

    private void setImageView(final ViewHolder viewHolder, final int position) {
        int imageResId;
        switch (getItem(position) % 5) {
            case 0:
                imageResId = R.drawable.img_nature1;
                break;
            case 1:
                imageResId = R.drawable.img_nature2;
                break;
            case 2:
                imageResId = R.drawable.img_nature3;
                break;
            case 3:
                imageResId = R.drawable.img_nature4;
                break;
            case 4:
                imageResId = R.drawable.img_nature5;
                break;
            default:
                imageResId = R.drawable.ic_launcher;
        }

        Bitmap bitmap = getBitmapFromMemoryCache(imageResId);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
            addBitmapToMemoryCache(imageResId, bitmap);
        }
        viewHolder.imageView.setImageBitmap(bitmap);
    }

    private void addBitmapToMemoryCache(final int key, final Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemoryCache(final int key) {
        return mMemoryCache.get(key);
    }

    private static class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}
