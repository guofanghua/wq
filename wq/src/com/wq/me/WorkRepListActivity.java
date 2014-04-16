package com.wq.me;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.custom.vg.list.OnItemClickListener;
import com.endure.wq.R;
import com.endure.wq.R.string;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.retationLogAdapter;
import com.wq.BaseActivity.EditClickListener;
import com.wq.UI.MMAlert;
import com.wq.UI.MMAlert.alertItemclickListener;
import com.wq.UI.MMAlert.onitemClick;
import com.wq.fragment.findFragment;
import com.wq.fragment.inqFragment;
import com.wq.fragment.meFragment;
import com.wq.fragment.myFragmentPagerAdapter;
import com.wq.fragment.partFragment;
import com.wq.fragment.workWdbgFragment;
import com.wq.fragment.workWdcsFragment;
import com.wq.fragment.workWdshFragment;
import com.wq.model.mmAlertModel;
import com.wq.model.partComModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;

public class WorkRepListActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private ImageView ivBottomLine;
	private TextView tvTabActivity, tvTabGroups, tvTabFriends;
	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;
	private int position_three;
	ArrayList<mmAlertModel> mmList = new ArrayList<mmAlertModel>();

	// public static String comm_bro_name = "work_bro_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.work_rep_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		currIndex = this.getIntent().getIntExtra("currInex", 0);
		InitWidth();
		InitTextView();
		InitViewPager();
	}

	private void InitTextView() {

		tvTabActivity = (TextView) findViewById(R.id.tv_tab_activity);
		tvTabGroups = (TextView) findViewById(R.id.tv_tab_groups);
		tvTabFriends = (TextView) findViewById(R.id.tv_tab_friends);
		// tvTabChat = (TextView) findViewById(R.id.tv_tab_chat);
		tvTabActivity.setOnClickListener(new MyOnClickListener(0));
		tvTabGroups.setOnClickListener(new MyOnClickListener(1));
		tvTabFriends.setOnClickListener(new MyOnClickListener(2));
		// tvTabChat.setOnClickListener(new MyOnClickListener(3));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		fragmentsList = new ArrayList<Fragment>();
		workWdbgFragment wdbgFragment = new workWdbgFragment();
		workWdshFragment wdshFragment = new workWdshFragment();
		workWdcsFragment wdcsFragment = new workWdcsFragment();
		fragmentsList.add(wdbgFragment);
		fragmentsList.add(wdshFragment);
		fragmentsList.add(wdcsFragment);
		mPager.setAdapter(new myFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));
		mPager.setCurrentItem(currIndex);
		mPager.setOffscreenPageLimit(3);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		Log.d(TAG, "cursor imageview width=" + bottomLineWidth);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);

		position_one = (int) (screenW / 3.0);
		position_two = position_one * 2;
		position_three = position_one * 3;
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					// tvTabGroups.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two, 0, 0, 0);
					// tvTabFriends.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three, 0, 0, 0);
					// tvTabChat.setTextColor(getResources().getColor(R.color.lightwhite));
				}
				// tvTabActivity.setTextColor(getResources().getColor(R.color.white));
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_one, 0, 0);
					// tvTabActivity.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_one, 0, 0);
					// tvTabFriends.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_one, 0, 0);
					// tvTabChat.setTextColor(getResources().getColor(R.color.lightwhite));
				}
				// tvTabGroups.setTextColor(getResources().getColor(R.color.white));
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_two, 0, 0);
					// tvTabActivity.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_two, 0, 0);
					// tvTabGroups.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_two, 0, 0);
					// tvTabChat.setTextColor(getResources().getColor(R.color.lightwhite));
				}
				// tvTabFriends.setTextColor(getResources().getColor(R.color.white));
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_three, 0, 0);
					// tvTabActivity.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_three, 0, 0);
					// tvTabGroups.setTextColor(getResources().getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_three, 0, 0);
					// tvTabFriends.setTextColor(getResources().getColor(R.color.lightwhite));
				}
				// tvTabChat.setTextColor(getResources().getColor(R.color.white));
				break;
			}
			if (currIndex != arg0) {
				sendBroadCast(arg0, 0, "");
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			ivBottomLine.startAnimation(animation);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void initnavigation() {

		final String[] arr = getResources()
				.getStringArray(R.array.work_rep_arr);
		mmList = CommonUtil.getSplitmmAlertModel(arr);

		BaseActivity.initNavigation(this, R.drawable.title_btn_back_click,
				getString(R.string.string_back),
				getResources().getString(R.string.me_string_gzbg),
				getResources().getString(R.string.me_string_write),
				R.drawable.title_btn_right_click, null,
				new EditClickListener() {
					@Override
					public void editClick() {
						MMAlert.showAlertListView(WorkRepListActivity.this,
								mmList, true, new onitemClick() {

									@Override
									public void onItemClick(int position,
											mmAlertModel item) {
										if (item.getIndex().equals("1")) {
											Bundle bundle = new Bundle();
											bundle.putInt("type", 0);

											changeViewForResult(
													WorkRepAddActivity.class,
													bundle, 100);
										} else if (item.getIndex().equals("2")) {
											Bundle bundle = new Bundle();
											bundle.putInt("type", 1);
											changeViewForResult(
													WorkRepAddActivity.class,
													bundle, 101);
										}
										// TODO Auto-generated method stub

									}
								});

					}

					@Override
					public void backClick() {
						finish();
						// animOut();
					}
				});
	}

	// 向fragment发送广播
	private void sendBroadCast(int index, int type, String id) {
		Intent intent = new Intent();
		intent.putExtra("type", type);
		if (index == 0) {
			intent.setAction(workWdbgFragment.BRO_ACTION_NAME);
		} else if (index == 1) {
			intent.setAction(workWdshFragment.BRO_ACTION_NAME);
		} else if (index == 2)
			intent.setAction(workWdcsFragment.BRO_ACTION_NAME);
		// else if (index == -1) {
		// intent.putExtra("id", id);
		// LoggerUtil.i("id000="+id);
		// intent.setAction(comm_bro_name);
		//
		// }
		this.sendBroadcast(intent);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {

			if (resultCode == 200) {

				showRightDialog(getString(R.string.me_string_work_sp_success),
						true);
				sendBroadCast(-1, 0, data.getStringExtra("id"));
			}

		}
	}

}
