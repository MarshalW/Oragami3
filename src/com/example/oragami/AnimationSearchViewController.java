package com.example.oragami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 下午1:57
 * To change this template use File | Settings | File Templates.
 */
public class AnimationSearchViewController extends SearchViewController {

    private SearchAnimationView animationView;

    private Animator.AnimatorListener animatorListener;

    private long fadeDelay = 100;

    private boolean animating;

    public AnimationSearchViewController(Activity context, int titleViewId) {
        this(context, titleViewId, null);
    }

    public AnimationSearchViewController(Activity context, int titleViewId, EndCallback callback) {
        super(context, titleViewId, callback);
        this.init();
    }

    public AnimationSearchViewController(Activity context, int titleViewId, int contentViewId) {
        this(context, titleViewId, contentViewId, null);
    }

    public AnimationSearchViewController(Activity context, int titleViewId, int contentViewId, EndCallback callback) {
        super(context, titleViewId, contentViewId, callback);
        this.init();
    }

    public AnimationSearchViewController(Activity context, int titleViewId, int contentViewId, boolean horizon) {
        this(context, titleViewId, contentViewId, horizon, null);
    }

    public AnimationSearchViewController(Activity context, int titleViewId, int contentViewId, boolean horizon, EndCallback callback) {
        super(context, titleViewId, contentViewId, horizon, callback);
        this.init();
    }

    public SearchAnimationView getAnimationView() {
        return animationView;
    }

    private void init() {
        if (horizon) {
            animationView = new HorizonalSearchAnimationView(context, titleView, contentView);
        } else {
            animationView = new SearchAnimationView(context, titleView, contentView);
        }

        rootView.addView(animationView);

        animatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animating = true;
                rootView.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rootView.setVisibility(View.INVISIBLE);
                    }
                }, fadeDelay);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rootView.setVisibility(View.VISIBLE);
                if (!opened) {
                    AnimationSearchViewController.super.opened();
                } else {
                    AnimationSearchViewController.super.closed();
                }
                opened = !opened;
                animating = false;
            }
        };
    }

    @Override
    public void close() {
        if (animating) {
            return;
        }

        if (opened) {
            closed();
        }
    }

    @Override
    public void open() {
        if (animating) {
            return;
        }

        if (!opened) {
            opened();
        }
    }

    protected void opened() {
        animationView.startAnimation(true, animatorListener);
    }

    protected void closed() {
        animationView.startAnimation(false, animatorListener);
    }
}
