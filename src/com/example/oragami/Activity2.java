package com.example.oragami;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 下午7:52
 * To change this template use File | Settings | File Templates.
 */
public class Activity2 extends Activity  implements View.OnClickListener {

    private Button searchButton, closeButton;

    SearchViewController controller;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.a2);

        controller = new AnimationSearchViewController(this, R.id.titleView);
        ((AnimationSearchViewController)controller).getAnimationView().setDistance(15);
        ((AnimationSearchViewController)controller).getAnimationView().setDuration(400);

        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == searchButton) {
            controller.open();
        } else {
            controller.close();
        }
    }
}