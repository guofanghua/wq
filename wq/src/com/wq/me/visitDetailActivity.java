package com.wq.me;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.visitDetailAdapter;
import com.wq.UI.InScrolllistView;
import com.wq.UI.TypeListPopDialog;

import com.wq.model.User;
import com.wq.model.visitDetail;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class visitDetailActivity extends BaseActivity {
	@ViewInject(id = R.id.visit_detail_txt_time_type)
	TextView txt_timeType;
	@ViewInject(id = R.id.visit_detail_txt_count)
	TextView txt_count;
	@ViewInject(id = R.id.visit_detail_list)
	InScrolllistView listview;
	ArrayList<visitDetail> list = new ArrayList<visitDetail>();
	visitDetailAdapter adapter;
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
	protected String[] mPlayTypes;// 需要查询的类型
	int flag = 0;// 0表示访问网站 1表示访问名片

	protected String[] mTimeTypes;// 需要查询的时间类型
	private TypeListPopDialog typeListDialog;
	private int typeFlag = 0;// 查询的类型,0周 1月 2季度 3年
	private int timeFlag = 0;// 查询的时间类型，天，周，月，年
	private int playType = 0;
	private String allCount = "0";
	long num = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visit_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		initData();

		initnavigation();
		initListener();
	}

	private void initData() {
		mPlayTypes = getResources().getStringArray(R.array.visit_types_detail);
		mTimeTypes = getResources().getStringArray(R.array.visit_time_types);
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			typeFlag = b.getInt("typeFlag", 0);
			timeFlag = b.getInt("timeFlag", 0);
			flag = b.getInt("flag", 0);
			allCount = b.getString("count");
		}
		httpData();
	}

	private int jumpCount(long count) {
		int jumpCount = (int) (count / 45);
		if (jumpCount == 0)
			jumpCount = 1;
		return jumpCount;

	}

	private void BindUI() {
		// txt_count.setText(allCount);
		if (!TextUtils.isEmpty("allCount")) {
			Handler handler = new Handler();
			final long all = Long.parseLong(allCount);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {

					if (num < all) {
						num += jumpCount(all);
						if (num > all)
							txt_count.setText(CommonUtil.insertComma(all + "",
									0));
						else
							txt_count.setText(CommonUtil.insertComma(num + "",
									0));
						txt_count.post(this);
					}

				}
				// TODO Auto-generated method stub

			}, 100);
		}

		txt_timeType.setText(mTimeTypes[timeFlag].split("\\|")[1]);
		adapter = new visitDetailAdapter(this, list, timeFlag);
	
		listview.setAdapter(adapter);
		listview.setFocusable(false);

	}

	private void initListener() {

		// layout_title.setOnClickListener(new click());
		// img_tig.setOnClickListener(new click());
		// txt_title.setOnClickListener(new click());
	}

	/**
	 * 获取接口数据
	 * **/
	private void httpData() {

		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.visit_Type, typeFlag + "");
		params.put(httpUtil.DETAIL_FLAG, flag + "");
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.VISIT_DETAIL_URL, params,
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
							if (errcode.equals(httpUtil.errCode_success)) {
								JSONArray arr = jsonResult
										.getJSONArray("allTrafficArray");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject obj = arr.getJSONObject(i);
									visitDetail item = new visitDetail();
									item.setTime(obj.getString("date"));
									item.setCount(obj.getString("count"));
									list.add(item);
								}
								BindUI();
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

					}
				});

	}

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.visit_top_layout_title:
			case R.id.visit_top_img_tig:
			case R.id.visit_top_txt_title:
				loadTypeListDialog(mPlayTypes);
				if (typeListDialog != null)
					typeListDialog.showAsDropDown(txt_title, 0,
							DensityUtil.dip2px(visitDetailActivity.this, 10));
				break;

			}
			// TODO Auto-generated method stub

		}
	}

	/**
	 * 加载 popupwindow
	 * */
	public void loadTypeListDialog(String[] comm) {
		if (comm == null)
			return;
		img_tig.setBackgroundResource(R.drawable.add_pic_nomal);
		typeListDialog = new TypeListPopDialog(this);
		typeListDialog.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		typeListDialog.loadContentView(comm, R.drawable.btn_style_green,
				playType, new OnClickListener() {

					@Override
					public void onClick(View v) {
						playType = Integer.parseInt(String.valueOf(v.getTag()));
						LoggerUtil.i(typeFlag + ",," + v.getTag());
						txt_title.setText(mPlayTypes[playType].split("\\|")[1]);
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

		txt_title.setText(mPlayTypes[flag].split("\\|")[1]);

	}

	
}
