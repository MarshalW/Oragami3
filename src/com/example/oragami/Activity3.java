package com.example.oragami;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 下午8:16
 * To change this template use File | Settings | File Templates.
 */
public class Activity3 extends Activity implements View.OnClickListener {

    private Button runButton;

    SearchViewController controller;

    boolean opened;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.a3);

        SearchViewController.EndCallback callback = new SearchViewController.EndCallback() {
            @Override
            public void opened() {
                Toast.makeText(Activity3.this, "打开..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closed() {
                Toast.makeText(Activity3.this, "关闭.", Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * 不带动画版本
         */
//        controller = new SearchViewController(this, R.id.titleView, R.id.contentView,true);

        /**
         * 带动画版本
         */
        controller = new AnimationSearchViewController(this, R.id.titleView, R.id.contentView, true, callback);

        runButton = (Button) findViewById(R.id.runButton);
        runButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (opened) {
            controller.close();
        } else {
            controller.open();
        }
        opened = !opened;
    }
}