package com.wq.me;

import android.os.Bundle;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseActivity.EditClickListener;

public class organMainActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_com_info_activity);
		initnavigation();

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.me_string_info_tile),
				"", -1, new EditClickListener() {
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						// if (checkForm())
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

	}

}
