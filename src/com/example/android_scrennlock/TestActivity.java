package com.example.android_scrennlock;

import java.util.ArrayList;

import com.example.android_scrennlock.GestureLock.onDrawFinishendLister;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;


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

		gestureLock.setonDrawFinishendLister(new onDrawFinishendLister() {

			@Override
			public boolean onDrawfinished(ArrayList<Integer> passList) {
				StringBuilder builder = new StringBuilder();
				for (Integer i : passList) {
					builder.append(i);
				}
				if (builder.toString().equals(password)) {
					Toast.makeText(TestActivity.this, "密码正确", 1).show();
					return true;
				} else {
					Toast.makeText(TestActivity.this, "密码错误", 1).show();
					return false;
				}
				

			}
		});
	}
}
