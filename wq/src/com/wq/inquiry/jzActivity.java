package com.wq.inquiry;

import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.ablumActivity;
import com.wq.webviewShowActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.UI.MMAlert;
import com.wq.me.ablumAddActivity;
import com.wq.me.companyDetailActivity;
import com.wq.me.noticeAddActivity;
import com.wq.me.photoActivity;
import com.wq.me.productAddActivity;
import com.wq.me.templateListActivity;
import com.wq.me.visitMainActivity;
import com.wq.model.User;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

/**
 * 建站
 * */
public class jzActivity extends BaseActivity {
	@ViewInject(id = R.id.me_rela_wdcp)
	RelativeLayout rela_wdcp;// 我的产品
	@ViewInject(id = R.id.me_rela_xc)
	RelativeLayout rela_xc;// 企业相册
	@ViewInject(id = R.id.me_rela_hdyh)
	RelativeLayout rela_hdyh;// 活动优惠
	@ViewInject(id = R.id.me_rela_wdmb)
	RelativeLayout rela_wdmb;// 我的模板
	@ViewInject(id = R.id.me_rela_wqxx)
	RelativeLayout rela_wszl;// 完善资料

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jz_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavigation();
		initUI();
	}

	private void initUI() {

		rela_wdcp.setOnClickListener(new onclick());
		rela_xc.setOnClickListener(new onclick());
		rela_hdyh.setOnClickListener(new onclick());
		rela_wdmb.setOnClickListener(new onclick());
		rela_wszl.setOnClickListener(new onclick());

	}

	public class onclick implements OnClickListener {
		// /storage/sdcard0/wqCrop/temp.png
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			// 企业相册
			case R.id.me_rela_xc:
				Intent init_photo = new Intent(jzActivity.this,
						ablumAddActivity.class);
				startActivity(init_photo);
				animUp();
				break;
			// 活动优惠
			case R.id.me_rela_hdyh:
				Intent init_notice = new Intent(jzActivity.this,
						noticeAddActivity.class);
				startActivity(init_notice);
				animUp();
				break;
			// 我的产品
			case R.id.me_rela_wdcp:
				Intent intent_product = new Intent(jzActivity.this,
						productAddActivity.class);
				startActivity(intent_product);
				animUp();
				break;
			// 我的模板
			case R.id.me_rela_wdmb:
				Intent intent_template = new Intent(jzActivity.this,
						templateListActivity.class);
				startActivity(intent_template);
				animIn();
				break;
			case R.id.me_rela_wqxx:
				Intent intent_wqxx = new Intent(jzActivity.this,
						companyDetailActivity.class);
				startActivity(intent_wqxx);
				animIn();
			}

		}
		// TODO Auto-generated method stub

	}

	private void initNavigation() {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				jzActivity.this, 60), DensityUtil.dip2px(jzActivity.this, 35));
		initNavigation(R.drawable.title_btn_back_click,
				getString(R.string.string_back),
				getString(R.string.linq_add_title), "", "",
				R.drawable.btn_see_click, params, new EditClickListener() {

					@Override
					public void editClick() {
						if (TextUtils.isEmpty(User.enterpriseNet)
								|| User.enterpriseNet.indexOf("http:") < 0) {
							return;
						}
						changeView(webviewShowActivity.class);
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
