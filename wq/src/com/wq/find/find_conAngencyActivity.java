package com.wq.find;

import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
/**
 * 联系代理
 * */
public class find_conAngencyActivity extends BaseActivity {
	@ViewInject(id = R.id.img_logo)
	ImageView img_logo;
	@ViewInject(id = R.id.txt_name)
	TextView txt_name;
	@ViewInject(id = R.id.me_rela_address)
	RelativeLayout rela_address;
	@ViewInject(id = R.id.me_rela_mobile)
	RelativeLayout rela_mobile;
	@ViewInject(id = R.id.me_rela_url)
	RelativeLayout rela_url;
	@ViewInject(id = R.id.txt_url)
	TextView txt_url;
	@ViewInject(id = R.id.txt_address)
	TextView txt_addr;
	@ViewInject(id = R.id.txt_mobile)
	TextView txt_mobile;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_con_agency_activity);
		BaseApplication.getInstance().addActivity(this);
		initListener();
		initnavigation();

	}

	private void initListener() {
		rela_url.setOnClickListener(new click());
		rela_address.setOnClickListener(new click());
		rela_mobile.setOnClickListener(new click());

	}

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.me_rela_address:
				break;
			case R.id.me_rela_mobile:
				break;
			case R.id.me_rela_url:
				break;
			}
			// TODO Auto-generated method stub

		}

	}

	private void initnavigation() {
		initNavitation(getString(R.string.find_string_title_anrency), "", -1,
				new EditClickListener() {
					
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						
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
