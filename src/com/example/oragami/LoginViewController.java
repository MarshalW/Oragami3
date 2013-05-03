package com.example.oragami;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-3
 * Time: 上午8:32
 * To change this template use File | Settings | File Templates.
 */
public class LoginViewController implements View.OnClickListener {

    protected Context context;

    protected ViewGroup rootView;

    protected List<ViewPair> viewPairList;

    protected EndCallback callback;

    public LoginViewController(Activity context, ViewGroup rootView) {
        this(context, rootView, null);
    }

    public LoginViewController(Activity context, ViewGroup rootView, EndCallback callback) {
        this.context = context;
        this.rootView = rootView;
        this.callback = callback;
        this.init();
    }

    private void init() {
        viewPairList = new ArrayList<ViewPair>();
        ViewGroup contentView = (ViewGroup) rootView.findViewById(R.id.contentView);
        for (int i = 0; i < contentView.getChildCount(); i += 2) {
            ViewPair pair = new ViewPair();
            viewPairList.add(pair);

            pair.title = contentView.getChildAt(i);
            pair.title.setOnClickListener(this);
            pair.content = contentView.getChildAt(i + 1);
        }

        postInit();
    }

    protected void postInit() {
        for (ViewPair pair : viewPairList) {
            pair.content.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        ViewPair choosePair = null;
        for (ViewPair pair : viewPairList) {
            if (view == pair.title) {
                choosePair = pair;
            }
            if (view == pair.title && pair.content.getVisibility() == View.GONE) {
                pair.content.setVisibility(View.VISIBLE);
            } else {
                pair.content.setVisibility(View.GONE);
            }
        }

        if(callback!=null){
            if (choosePair.content.getVisibility() == View.VISIBLE) {
                callback.onOpened(choosePair.content);
            } else {
                callback.onClosed(choosePair.content);
            }
        }
    }

    protected class ViewPair {
        View title, content;
        Bitmap titleBitmap;
        Bitmap[] contentBitmaps;

        void snapBitmap() {
            clearBitmaps();
            title.setDrawingCacheEnabled(true);
            titleBitmap = Bitmap.createBitmap(title.getDrawingCache());
            title.setDrawingCacheEnabled(false);

            content.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(content.getDrawingCache());
            content.setDrawingCacheEnabled(false);

            contentBitmaps = new Bitmap[]{
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight() / 2),
                    Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2,
                            bitmap.getWidth(), bitmap.getHeight() / 2)
            };

            bitmap.recycle();
        }

        void clearBitmaps() {
            if (titleBitmap != null) {
                titleBitmap.recycle();
            }
            if (contentBitmaps != null) {
                for (Bitmap bitmap : contentBitmaps) {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                }
            }
        }
    }

    public interface EndCallback {

        void onOpened(View targetView);

        void onClosed(View targetView);
    }

}
