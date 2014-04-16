package com.wq.find;

import android.os.Bundle;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;

/** 招商 */
public class findZSActivity extends BaseActivity  {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_zs_activity);
		BaseApplication.getInstance().addActivity(this);

		initNavigation();

	}

	private void initNavigation() {
		initNavitation(getString(R.string.find_zs_title), "", -1,
				new EditClickListener() {
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						finish();
						animOut();
						// TODO Auto-generated method stub
					}
				});
	}
}
