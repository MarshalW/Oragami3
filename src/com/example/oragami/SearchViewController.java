package com.example.oragami;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
public class SearchViewController {
    protected Activity context;

    protected View titleView, contentView;

    protected FrameLayout rootView;

    protected boolean opened;

    protected boolean horizon;

    private int[] contentSize;

    private int titleSize;

    public SearchViewController(Activity context, int titleViewId) {
        this.context = context;
        titleView = this.context.findViewById(titleViewId);
        this.init();
    }

    public SearchViewController(Activity context, int titleViewId, int contentViewId) {
        this.context = context;
        titleView = this.context.findViewById(titleViewId);
        contentView = this.context.findViewById(contentViewId);
        this.init();
    }

    public SearchViewController(Activity context, int titleViewId, int contentViewId, boolean horizon) {
        this(context, titleViewId, contentViewId);
        this.horizon = horizon;
    }

    private void init() {
        rootView = (FrameLayout) titleView.getParent();
        titleView.setVisibility(View.INVISIBLE);
    }

    public void open() {
        if (!opened) {
            opened = true;
            opened();
        }
    }

    protected void opened() {
        initSize();
        titleView.setVisibility(View.VISIBLE);

        if (contentView != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    contentSize[0],
                    contentSize[1]
            );
            if (!this.horizon) {
                layoutParams.setMargins(0, titleSize, 0, 0);
            } else {
                layoutParams.setMargins(titleSize, 0, 0, 0);
            }
            contentView.setLayoutParams(layoutParams);
        }
    }

    public void close() {
        if (opened) {
            opened = false;
            closed();
        }
    }

    private void initSize() {
        if (contentSize == null && contentView != null) {
            contentSize = new int[]{
                    contentView.getWidth(),
                    contentView.getHeight()
            };
            titleSize = horizon ? titleView.getWidth() : titleView.getHeight();
        }
    }

    protected void closed() {
        initSize();
        titleView.setVisibility(View.INVISIBLE);

        if (contentView != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    contentSize[0],
                    contentSize[1]
            );
            layoutParams.setMargins(0, 0, 0, 0);
            contentView.setLayoutParams(layoutParams);
        }
    }
}
