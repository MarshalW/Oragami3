package com.example.oragami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-3
 * Time: 上午9:14
 * To change this template use File | Settings | File Templates.
 */
public class AnimationLoginViewController extends LoginViewController implements View.OnLayoutChangeListener {

    private View contentView, parent;

    private LoginAnimationView animationView;

    public AnimationLoginViewController(Activity context, ViewGroup rootView) {
        this(context, rootView, null);
    }

    public AnimationLoginViewController(Activity context, ViewGroup rootView, EndCallback callback) {
        super(context, rootView, callback);

        this.animationView = new LoginAnimationView(context, contentView, viewPairList);
        rootView.addView(animationView);
    }

    @Override
    protected void postInit() {
        contentView = rootView.findViewById(R.id.contentView);
        parent = (View) contentView.getParent();
        parent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3000));
        contentView.setVisibility(View.INVISIBLE);
        contentView.addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        contentView.removeOnLayoutChangeListener(this);

        for (ViewPair pair : viewPairList) {
            pair.snapBitmap();
        }
        super.postInit();
        parent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        contentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(final View view) {
        animationView.startAnimation(getViewPair(view), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                AnimationLoginViewController.super.onClick(view);
            }
        });
    }

    private ViewPair getViewPair(View titleView) {
        for (ViewPair pair : viewPairList) {
            if (pair.title == titleView) {
                return pair;
            }
        }
        return null;
    }
}
