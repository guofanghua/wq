package com.wq.find;

import net.endure.framework.annotation.view.ViewInject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;

public class captureOtherShowActivity extends BaseActivity {
	@ViewInject(id = R.id.txt_show)
	TextView txt_show;
	String resultStr = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cap_other_show_activity);
		BaseApplication.getInstance().addActivity(this);
		resultStr = this.getIntent().getStringExtra("result");
		if (!TextUtils.isEmpty(resultStr))
			txt_show.setText(resultStr);
	}

}
