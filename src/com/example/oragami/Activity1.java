package com.example.oragami;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 上午11:11
 * To change this template use File | Settings | File Templates.
 */
public class Activity1 extends Activity implements View.OnClickListener {

    private Button searchButton, closeButton;

    SearchViewController controller;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.a1);

        SearchViewController.EndCallback callback = new SearchViewController.EndCallback() {
            @Override
            public void opened() {
                Toast.makeText(Activity1.this, "打开..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closed() {
                Toast.makeText(Activity1.this, "关闭.", Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * 不带动画版本
         */
//        controller = new SearchViewController(this, R.id.titleView, R.id.contentView);

        /**
         * 带动画版本
         */
        controller = new AnimationSearchViewController(this, R.id.titleView, R.id.contentView, callback);

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