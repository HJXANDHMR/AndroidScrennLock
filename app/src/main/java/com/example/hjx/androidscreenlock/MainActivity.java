package com.example.hjx.androidscreenlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    private Button bt_set;
    private Button bt_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_set = (Button) findViewById(R.id.bt_set);
        bt_test = (Button) findViewById(R.id.bt_test);

        bt_set.setOnClickListener(this);
        bt_test.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_set:
                Intent intent = new Intent(MainActivity.this, SetActivity.class);
                startActivity(intent);
                break;

            case R.id.bt_test:
                Intent intent2 = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent2);
                break;
        }
    }

}
