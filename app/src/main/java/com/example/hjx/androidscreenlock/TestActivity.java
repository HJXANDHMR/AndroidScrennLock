package com.example.hjx.androidscreenlock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class TestActivity extends Activity {
    private GestureLock gestureLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testview);

        gestureLock = (GestureLock) findViewById(R.id.gestureLock1);
        SharedPreferences preferences = getSharedPreferences("password",
                MODE_PRIVATE);
        final String password = preferences.getString("password", "");

        gestureLock.setonDrawFinishendLister(new GestureLock.onDrawFinishendLister() {

            @Override
            public boolean onDrawfinished(ArrayList<Integer> passList) {
                StringBuilder builder = new StringBuilder();
                for (Integer i : passList) {
                    builder.append(i);
                }
                if (builder.toString().equals(password)) {
                    Toast.makeText(TestActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(TestActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }
}
