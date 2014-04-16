package com.wq.me;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.UI.TypeListPopDialog;

import com.wq.model.User;
import com.wq.model.visitMain;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

/**
 * 访问统计 主界面
 * */
public class visitMainActivity extends BaseActivity {
	@ViewInject(id = R.id.visit_main_txt_day)
	TextView txt_day;
	@ViewInject(id = R.id.visiti_main_txt_all)
	TextView txt_all;
	@ViewInject(id = R.id.visit_main_txt_week)
	TextView txt_week;
	@ViewInject(id = R.id.visit_main_txt_month)
	TextView txt_month;
	@ViewInject(id = R.id.visit_main_txt_quarter)
	TextView txt_quarter;
	@ViewInject(id = R.id.visit_main_txt_year)
	TextView txt_year;
	@ViewInject(id = R.id.visit_main_rela_week)
	RelativeLayout rela_week;
	@ViewInject(id = R.id.visit_main_rela_month)
	RelativeLayout rela_month;
	@ViewInject(id = R.id.visit_main_rela_quarter)
	RelativeLayout rela_quarter;
	@ViewInject(id = R.id.visit_main_rela_year)
	RelativeLayout rela_year;
	// top界面
	@ViewInject(id = R.id.visit_top_layout_title)
	LinearLayout layout_title;
	@ViewInject(id = R.id.visit_top_txt_title)
	TextView txt_title;
	@ViewInject(id = R.id.visit_top_img_tig)
	ImageView img_tig;
	@ViewInject(id = R.id.visit_top_btn_back)
	Button btn_back;
	@ViewInject(id = R.id.visit_top_btn_edit)
	Button btn_edit;
	@ViewInject(id = R.id.visit_main_txt_title)
	TextView txt_main_title;
	protected String[] mPlayTypes;// 需要查询的类型
	private TypeListPopDialog typeListDialog;
	private int typeFlag = 0;// 查询的类型
	private int timeFlag = 0;// 查询的时间类型，天，周，月，季度，年
	private visitMain model = new visitMain();
	int num, num1, num2, num3, num4, num5 = 0;
	boolean minus = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visit_main_activity);
		BaseApplication.getInstance().addActivity(this);
		initData();
		initnavigation();
		initListener();
	}

	private void initData() {
		mPlayTypes = getResources().getStringArray(R.array.visit_types);
		typeFlag = this.getIntent().getIntExtra("flag", 0);
		if (typeFlag == 1) {
			txt_main_title.setText(getResources().getString(
					R.string.visit_main_count_all1));

		}
		showProgress(R.string.dialog_loading);
		httpData();
	}

	private void bindUI() {// 自定义字体
		Typeface typeface = Typeface.createFromAsset(this.getAssets(),
				"fonts/Helvetica Neue.ttf");
		txt_day.setTypeface(typeface);
		txt_all.setTypeface(typeface);
		Handler handler = new Handler();
		final long all = Long.parseLong(model.getAllCount());
		// 所有
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (num < all) {
					num += jumpCount(all);
					if (num > all)
						txt_all.setText(CommonUtil.insertComma(all + "", 0));
					else
						txt_all.setText(CommonUtil.insertComma(num + "", 0));
					txt_all.post(this);
				}

			}
			// TODO Auto-generated method stub

		}, 100);
		Handler handler1 = new Handler();

		final long day = Long.parseLong(model.getDayCount());
		handler1.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (num1 < day) {
					num1 += jumpCount(day);
					if (num1 > day)
						txt_day.setText(CommonUtil.insertComma(day + "", 0));
					else
						txt_day.setText(CommonUtil.insertComma(num1 + "", 0));
					txt_day.post(this);
				}

			}
			// TODO Auto-generated method stub

		}, 100);
		Handler handler2 = new Handler();

		final long week = Long.parseLong(model.getWeekCount());
		handler2.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (num2 < week) {
					num2 += jumpCount(week);
					if (num2 > week)
						txt_week.setText(CommonUtil.insertComma(week + "", 0));
					else

						txt_week.setText(CommonUtil.insertComma(num2 + "", 0));
					txt_week.post(this);
				}

			}
			// TODO Auto-generated method stub

		}, 100);
		Handler handler3 = new Handler();

		final long month = Long.parseLong(model.getMonthCount());
		handler3.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (num3 < month) {
					num3 += jumpCount(month);
					if (num3 > month)
						txt_month.setText(CommonUtil.insertComma(month + "", 0));
					else
						txt_month.setText(CommonUtil.insertComma(num3 + "", 0));
					txt_month.post(this);
				}

			}
			// TODO Auto-generated method stub

		}, 100);
		Handler handler4 = new Handler();

		final long quart = Long.parseLong(model.getQuarterCount());
		handler4.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (num4 < quart) {
					num4 += jumpCount(quart);
					if (num4 > quart)
						txt_quarter.setText(CommonUtil.insertComma(quart + "",
								0));
					else
						txt_quarter.setText(CommonUtil
								.insertComma(num4 + "", 0));
					txt_quarter.post(this);
				}

			}
			// TODO Auto-generated method stub

		}, 100);

		Handler handler5 = new Handler();

		final long year = Long.parseLong(model.getYearCount());
		handler5.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (num5 < year) {
					num5 += jumpCount(year);
					if (num5 > year)
						txt_year.setText(CommonUtil.insertComma(year + "", 0));
					else

						txt_year.setText(CommonUtil.insertComma(num5 + "", 0));
					txt_year.post(this);
				}

			}
			// TODO Auto-generated method stub

		}, 100);
	}

	private int jumpCount(long count) {
		int jumpCount = (int) (count / 45);
		if (jumpCount == 0)
			jumpCount = 1;
		return jumpCount;
		// if (count <= 50)
		// return 2;
		// else if (count > 50 && count <= 400)
		// return 5;
		// else if (count > 400 && count <= 9999) {
		// return 10;
		// } else {
		// return 100;
		// }
	}

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		showProgress(R.string.dialog_loading);
		params.put(httpUtil.FLAG, typeFlag + "");
		httpUtil.post(httpUtil.VISIT_LIST_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {
						try {

							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							jsonResult = jsonResult.getJSONObject("traffic");
							if (errcode.equals(httpUtil.errCode_success)) {
								model.setAllCount(jsonResult

								.getString("allTraffic"));
								model.setWeekCount(jsonResult
										.getString("aweekTraffic"));
								model.setMonthCount(jsonResult
										.getString("amonthTraffic"));
								model.setQuarterCount(jsonResult
										.getString("aThreeMTraffic"));
								model.setYearCount(jsonResult
										.getString("aYearTraffic"));
								model.setDayCount(jsonResult
										.getString("aDayTraffic"));
								bindUI();
							} else {

								errMsg = jsonResult.getString(httpUtil.ERR_MGS);

							}

						} catch (JSONException e) {

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
						CommonUtil.showToastHttp(visitMainActivity.this,
								errorNo);

					}
				});
	}

	private void initListener() {
		rela_week.setOnClickListener(new click());
		rela_month.setOnClickListener(new click());
		rela_quarter.setOnClickListener(new click());
		rela_year.setOnClickListener(new click());
		// layout_title.setOnClickListener(new click());
		// img_tig.setOnClickListener(new click());
		// txt_title.setOnClickListener(new click());
	}

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			Bundle b = new Bundle();
			switch (v.getId()) {
			case R.id.visit_main_rela_week:
				timeFlag = 1;
				b.putInt("timeFlag", timeFlag);
				b.putInt("typeFlag", 0);
				b.putInt("flag", typeFlag);
				b.putString("count", model.getWeekCount());
				changeView(visitDetailActivity.class, b);
				break;
			case R.id.visit_main_rela_month:
				timeFlag = 2;
				b.putInt("timeFlag", timeFlag);
				b.putInt("typeFlag", 1);
				b.putInt("flag", typeFlag);
				b.putString("count", model.getMonthCount());
				changeView(visitDetailActivity.class, b);
				break;
			case R.id.visit_main_rela_quarter:
				timeFlag = 3;
				b.putInt("timeFlag", timeFlag);
				b.putInt("typeFlag", 2);
				b.putInt("flag", typeFlag);
				b.putString("count", model.getQuarterCount());
				changeView(visitDetailActivity.class, b);
				break;
			case R.id.visit_main_rela_year:
				timeFlag = 4;
				b.putInt("timeFlag", timeFlag);
				b.putInt("typeFlag", 3);
				b.putInt("flag", typeFlag);
				b.putString("count", model.getYearCount());
				changeView(visitDetailActivity.class, b);
				break;
			case R.id.visit_top_layout_title:
			case R.id.visit_top_img_tig:
			case R.id.visit_top_txt_title:
				loadTypeListDialog(mPlayTypes);
				if (typeListDialog != null)
					typeListDialog.showAsDropDown(txt_title, 0,
							DensityUtil.dip2px(visitMainActivity.this, 10));
				break;

			}
			// TODO Auto-generated method stub

		}
	}

	public void loadTypeListDialog(String[] comm) {
		if (comm == null)
			return;
		img_tig.setBackgroundResource(R.drawable.add_pic_nomal);
		typeListDialog = new TypeListPopDialog(this, R.layout.popup_dialog);
		typeListDialog.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		typeListDialog.loadContentView(comm, R.drawable.btn_style_green,
				typeFlag, new OnClickListener() {

					@Override
					public void onClick(View v) {
						typeFlag = Integer.parseInt(String.valueOf(v.getTag()));
						LoggerUtil.i(typeFlag + ",," + v.getTag());
						txt_title.setText(mPlayTypes[typeFlag].split("\\|")[1]);
						typeListDialog.dismiss();
						// TODO Auto-generated method stub

					}
				});
		// 为弹出框设置自定义的布局文件
		typeListDialog.setContentView();
		typeListDialog.setFocusable(true);
		typeListDialog.setBackgroundDrawable(new BitmapDrawable());// 一定要设置背景才起点击外面关闭才起作用
		typeListDialog.setOutsideTouchable(true);
		typeListDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				img_tig.setBackgroundResource(R.drawable.title_down_arrow);
			}
		});
	}

	private void initnavigation() {
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				animOut();
				// TODO Auto-generated method stub

			}
		});

		txt_title.setText(mPlayTypes[typeFlag].split("\\|")[1]);

	}

}
