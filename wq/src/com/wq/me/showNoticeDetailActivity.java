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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.imageSwitchAdapter;
import com.wq.Adapter.viewPageAdapter;
import com.wq.fragment.myFragmentPagerAdapter;
import com.wq.mainActivity.myOnPageChangeListener;
import com.wq.model.User;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class showNoticeDetailActivity extends BaseActivity {
	@ViewInject(id = R.id.show_notice_detail_txt_title)
	TextView txt_title;
	@ViewInject(id = R.id.show_notice_detail_txt_content)
	TextView txt_content;
	@ViewInject(id = R.id.layout_index)
	LinearLayout layout_index;

	@ViewInject(id = R.id.txt_index)
	TextView txt_index;
	@ViewInject(id = R.id.scroll_main)
	ScrollView scroll_main;

	notice model = new notice();
	private int count_drawble = 0; // 总共的图片大小
	private int cur_index = 0; // 当前图片位置
	private boolean isalive = true;
	private static int MSG_UPDATE = 1;

	// viewpager
	@ViewInject(id = R.id.tabpager)
	ViewPager mTabPager;
	@ViewInject(id = R.id.frame_img)
	FrameLayout frame_img;
	private ArrayList<View> imageViewList = new ArrayList<View>();
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;
	// 从生意圈里点击进入
	String noticeId = "";
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_notice_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		config = new BitmapDisplayConfig();
		layout_index.getBackground().setAlpha(150);
		txt_index.setTextColor(Color.argb(255, 255, 255, 255));
		setViewTouchListener(scroll_main);
		initNavigation();
		initData();
	}

	private void initData() {
		noticeId = this.getIntent().getStringExtra("noticeId");
		if (TextUtils.isEmpty(noticeId)) {
			model = (notice) this.getIntent().getSerializableExtra(
					"noticeModel");
			if (model != null) {
				BindUI();
			}
		}
		// 获取数据
		else {
			httpData();
		}
	}

	// 根据公告id获取公告内容

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.NOTICE_ID, noticeId);
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.NOTICE_DETAIL_URL, params,
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
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							if (errcode.equals(httpUtil.errCode_success)) {
								JSONObject object = jsonResult
										.getJSONObject("promotion");

								model.setTime(object.getString("time"));
								model.setId(object.getString("id"));
								model.setTitle(object.getString("title"));
								model.setContent(object.getString("content"));
								JSONArray picArr = object
										.getJSONArray("imgFileArray");
								for (int j = 0; j < picArr.length(); j++) {
									JSONObject picObj = picArr.getJSONObject(j);
									photoModel pic = new photoModel();
									pic.setFlag(0);
									pic.setId(picObj.getString("imgId"));
									pic.setImageUrl(picObj.getString("imgUrl"));

									pic.setShareModel(model);
									pic.setShareType(photoModel.NOTICE_SHARE_FLAG);
									model.getPic().add(pic);
								}
								BindUI();
							} else {
								CommonUtil.showToast(
										showNoticeDetailActivity.this, errMsg);
							}
						} catch (JSONException e) {

							CommonUtil.showToast(showNoticeDetailActivity.this,
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
						CommonUtil.showToastHttp(showNoticeDetailActivity.this,
								errorNo);
					}
				});

	}

	private void BindUI() {

		txt_title.setText(model.getTitle());
		txt_content.setText(model.getContent());
		count_drawble = model.getPic().size();

		if (count_drawble <= 0) {
			frame_img.setVisibility(View.GONE);
			return;
		}
		for (int i = 0; i < model.getPic().size(); i++) {
			ImageView img = new ImageView(this);
			LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			img.setBackgroundColor(Color.rgb(238, 238, 238));
			img.setScaleType(ScaleType.CENTER_CROP);
			img.setLayoutParams(param);
			img.setTag(i);
			img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					scanPhoto(model.getPic(),
							Integer.parseInt(String.valueOf(v.getTag())),
							false, false, null);
					// TODO Auto-generated method stub

				}
			});
			finalBitmap.display(img, model.getPic().get(i).getImageUrl(),
					config);
			imageViewList.add(img);
		}
		mTabPager.setAdapter(new viewPageAdapter(imageViewList));
		mTabPager.setCurrentItem(0);
		txt_index.setText((1) + "/" + count_drawble);

		// mTabPager.setOffscreenPageLimit(4);

		// mTabPager.setOffscreenPageLimit(4);//不进行预加载
		mTabPager.setOnPageChangeListener(new myOnPageChangeListener());
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (isalive) {
					cur_index = cur_index % count_drawble; // 图片区间[0,count_drawable)

					// msg.arg1 = cur_index
					Message msg = mhandler.obtainMessage(MSG_UPDATE, cur_index,
							0);
					mhandler.sendMessage(msg);
					// 更新时间间隔为 4s
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cur_index++; // 放置在Thread.sleep(2000)
									// ；防止mhandler处理消息的同步性，导致cur_index>=count_drawble
				}
			}
		}).start();
		setViewTouchListener(scroll_main);
	}

	// 通过handler来更新主界面
	// mgallery.setSelection(positon),选中第position的图片，然后调用OnItemSelectedListener监听改变图像
	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE) {
				mTabPager.setCurrentItem(msg.arg1, true);
				// 直接更改图片 ，不触发Gallery.OnItemSelectedListener监听
				// imgSwitcher.setBackgroundResource(imgAdapter.getResId(msg.arg1));
			}
		}
	};

	private void initNavigation() {
		initNavitation(getString(R.string.me_string_notice_detail_title), "",
				"", -1, new EditClickListener() {

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

	public void onDestroy() {
		super.onDestroy();
		isalive = false;
	}

	// 图片轮播监听事件
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
			cur_index = position;
			txt_index.setText((cur_index + 1) + "/" + count_drawble);
			// mTabImg.startAnimation(anim);
			// TODO Auto-generated method stub

		}
	}
}
