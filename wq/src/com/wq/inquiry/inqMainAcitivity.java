package com.wq.inquiry;

import android.app.Activity;
import android.os.Bundle;

import com.endure.wq.R;
import com.wq.BaseApplication;

public class inqMainAcitivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_main_fragment);
		BaseApplication.getInstance().addActivity(this);
		// checkVersion();

	}
}
