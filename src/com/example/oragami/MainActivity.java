package com.example.oragami;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button button1,button2,button3,button4;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);

        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        if (view == button1) {
            intent.setClass(this, Activity1.class);
        }
        if (view == button2) {
            intent.setClass(this, Activity2.class);
        }
        if (view == button3) {
            intent.setClass(this, Activity3.class);
        }
        if (view == button4) {
            intent.setClass(this, Activity4.class);
        }
        startActivity(intent);
    }
}
