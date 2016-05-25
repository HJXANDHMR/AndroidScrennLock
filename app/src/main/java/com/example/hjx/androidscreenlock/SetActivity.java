package com.example.hjx.androidscreenlock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SetActivity extends Activity implements OnClickListener {

	private Button bt_reset;
	private Button bt_save;
	private GestureLock gestureLock;
	private List<Integer> passList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		
		gestureLock = (GestureLock) findViewById(R.id.LockView);
		bt_reset = (Button) findViewById(R.id.btn_reset);
		bt_save = (Button) findViewById(R.id.btn_save);
		
		bt_reset.setOnClickListener(this);
		bt_save.setOnClickListener(this);
		
		gestureLock.setonDrawFinishendLister(new GestureLock.onDrawFinishendLister() {

			@Override
			public boolean onDrawfinished(ArrayList<Integer> passList) {

				if (passList.size() < 3) {
					Toast.makeText(SetActivity.this, "密码不能小于3个点", Toast.LENGTH_SHORT).show();
					return false;
				} else {
					SetActivity.this.passList = passList;
					return true;
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reset:
			gestureLock.restPoint();
			break;
			
		case R.id.btn_save:
			if (passList != null) {
				StringBuilder builder = new StringBuilder();
				for (Integer i : passList) {
					builder.append(i);
				}
				SharedPreferences preferences = getSharedPreferences(
						"password", MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("password", builder.toString());
				editor.commit();
				Toast.makeText(SetActivity.this, "密码保存成功", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}
