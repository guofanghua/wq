package com.wq.me;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.ablumActivity;
import com.wq.Adapter.viewPageAdapter;
import com.wq.fragment.myFragmentPagerAdapter;
import com.wq.model.User;
import com.wq.model.photoModel;
import com.wq.model.templateModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.httpUtil;

public class templateDetailActivity extends BaseActivity {
	public static final int RESULT_FLAG = 109;
	@ViewInject(id = R.id.temp_tabpager)
	ViewPager viewPager;
	@ViewInject(id = R.id.temp_radiogroup)
	RadioGroup radioGroup;
	@ViewInject(id = R.id.temp_btn_ok)
	Button btn_ok;
	private ArrayList<View> viewList = new ArrayList<View>();
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;
	templateModel template = new templateModel();
	private int postion = 0;
	LinearLayout layout_detail;
	// 企业详情
	TextView txt_name;
	TextView txt_des_name;
	TextView txt_size;
	TextView txt_version;
	TextView txt_user_count;
	TextView txt_contruduct;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		config = new BitmapDisplayConfig();
//		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
//				R.drawable.add_prompt1));
//		config.setLoadfailBitmap(BitmapFactory.decodeResource(getResources(),
//				R.drawable.add_prompt1));
		// 设置不可以滑动返回
		setIsFling(false);
		initData();
	}

	private void initData() {
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			template = (templateModel) b.getSerializable("tempModel");
			postion = b.getInt("position");
		}
		if (template != null) {
			initnavigation();
			// 企业详情
			layout_detail = (LinearLayout) LayoutInflater.from(this).inflate(
					R.layout.template_detail_view, null);
			initDetailTemp(layout_detail);
			viewList.add(layout_detail);
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			BitmapDisplayConfig config = new BitmapDisplayConfig();
			// config.setLoadingBitmap(BitmapFactory.decodeResource(
			// getResources(), R.drawable.add_prompt_vertical));
			for (int i = 0; i < template.getPicList().size(); i++) {
				ImageView img = new ImageView(this);
				finalBitmap.display(img, template.getPicList().get(i)
						.getImageUrl(), config);
				img.setLayoutParams(params);
				img.setScaleType(ScaleType.FIT_XY);
				img.setOnClickListener(new click());
				img.setBackgroundColor(Color.rgb(204, 204, 204));
				img.setTag(template.getPicList().get(i).getImageUrl());
				viewList.add(img);
			}
			viewPager.setAdapter(new viewPageAdapter(viewList));
			viewPager.setCurrentItem(0);
			viewPager.setOffscreenPageLimit(viewList.size());
			viewPager.setOnPageChangeListener(new myOnPageChangeListener());
		}
		for (int i = 0; i < viewList.size(); i++) {

			RadioButton rb = new RadioButton(templateDetailActivity.this);
			rb.setId(i);
			rb.setWidth(DensityUtil.dip2px(templateDetailActivity.this, 20));
			rb.setHeight(DensityUtil.dip2px(templateDetailActivity.this, 20));

			rb.setButtonDrawable(R.drawable.radio_group_mark_selector);
			rb.setClickable(false);
			radioGroup.addView(rb);
		}
		radioGroup.check(0);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AjaxParams params = new AjaxParams();
				params.put(httpUtil.USER_ID, User.id);
				params.put(httpUtil.USER_KEY, User.userKey);
				params.put(httpUtil.TEMPLATE_ID, template.getTemplateId());
				showProgress(R.string.dialog_comitting);
				httpUtil.post(httpUtil.TEMPLATE_SET_URL, params,
						new AjaxCallBack<String>() {
							private String errMsg = "";
							private String errcode = "";

							public void onStart() {

							}

							@Override
							public void onSuccess(String result) {
								try {
									JSONObject jsonResult = new JSONObject(
											result);
									errcode = jsonResult
											.getString(httpUtil.ERR_CODE);
									errMsg = jsonResult
											.getString(httpUtil.ERR_MGS);
									if (errcode
											.equals(httpUtil.errCode_success)) {
										template.setIsCertification("1");
										Bundle b = new Bundle();
										b.putSerializable("tempModel", template);
										b.putInt("position", postion);
										Intent intent = new Intent();
										intent.putExtras(b);
										setResult(RESULT_FLAG, intent);
										finish();
									}
									CommonUtil
											.showToast(
													templateDetailActivity.this,
													errMsg);
								} catch (JSONException e) {

									CommonUtil.showToast(
											templateDetailActivity.this,
											R.string.string_http_err_data);
									e.printStackTrace();
								} finally {
									hideProgress();
								}
								// TODO Auto-generated method stub
							}

							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								// TODO Auto-generated method stub
								hideProgress();
								// Log.i("fail", strMsg);

								CommonUtil.showToastHttp(
										templateDetailActivity.this, errorNo);
							}
						});
			}
		});

	}

	// 初始化企业详情界面
	private void initDetailTemp(View v) {
		txt_name = (TextView) v.findViewById(R.id.template_txt_name);
		txt_des_name = (TextView) v.findViewById(R.id.template_txt_des_name);
		txt_size = (TextView) v.findViewById(R.id.template_txt_size);
		txt_version = (TextView) v.findViewById(R.id.template_txt_version);
		txt_user_count = (TextView) v
				.findViewById(R.id.template_txt_user_count);
		txt_contruduct = (TextView) v
				.findViewById(R.id.template_txt_contruduct);
		txt_name.setText(template.getName());
		txt_des_name.setText(template.getStylist());
		txt_size.setText(template.getSpace());
		txt_version.setText(template.getVersion());
		txt_user_count.setText(template.getFrequency());
		txt_contruduct.setText(template.getResourceInfo());
	}

	// page滑动事件
	public class myOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			radioGroup.check(position % viewList.size());

			// TODO Auto-generated method stub

		}
	}

	private void initnavigation() {
		initNavitation(template.getName(), "", -1, new EditClickListener() {

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

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(templateDetailActivity.this,
					templateImageShowActivity.class);
			intent.putExtra("imgPath", String.valueOf(v.getTag()));
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.ablum_transition_in,
					R.anim.transition_in);
			// TODO Auto-generated method stub

		}

	}

}
