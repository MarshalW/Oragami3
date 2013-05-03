package com.example.oragami;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 下午9:31
 * To change this template use File | Settings | File Templates.
 */
public class Activity4 extends Activity {

    LoginViewController loginViewController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a4);

        LoginViewController.EndCallback callback = new LoginViewController.EndCallback() {
            @Override
            public void onOpened(View targetView) {
                Toast.makeText(Activity4.this, "打开视图: " + targetView, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClosed(View targetView) {
                Toast.makeText(Activity4.this, "关闭视图: " + targetView, Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * 不带动画版本
         */
//        loginViewController = new LoginViewController(this, (ViewGroup) findViewById(R.id.rootView), callback);

        /**
         * 带动画版本
         */
        loginViewController = new AnimationLoginViewController(this, (ViewGroup) findViewById(R.id.rootView), callback);
    }
}