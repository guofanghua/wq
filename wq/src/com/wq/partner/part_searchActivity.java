package com.wq.partner;

import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;

public class part_searchActivity extends BaseActivity {
	@ViewInject(id = R.id.part_rela_qyh)
	LinearLayout layout_qyh;// 搜企业号
	@ViewInject(id = R.id.part_rela_name)
	LinearLayout layout_name;// 搜企业名
	@ViewInject(id = R.id.part_rela_jylx)
	LinearLayout layout_jylx;// 搜经营类型
	@ViewInject(id = R.id.part_rela_yqa)
	LinearLayout layout_yqa;
	@ViewInject(id = R.id.part_rela_qq)
	LinearLayout layout_qq;// 从qq好友列表获取
	@ViewInject(id = R.id.part_rela_sina)
	LinearLayout layout_sina;// 从新浪微博列表获取
	@ViewInject(id = R.id.find_rela1)
	ScrollView scorll_main;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.part_search_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavigation();
		initListener();

	}

	private void initListener() {
		layout_qyh.setOnClickListener(new click());
		layout_name.setOnClickListener(new click());
		layout_jylx.setOnClickListener(new click());
		layout_qq.setOnClickListener(new click());
		layout_sina.setOnClickListener(new click());
		layout_yqa.setOnClickListener(new click());
		setViewTouchListener(scorll_main);

	}

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle b = new Bundle();
			switch (v.getId()) {

			case R.id.part_rela_qyh:
				b.putString("title", getString(R.string.part_string_search_qyh));
				b.putInt("searchType", 0);
				b.putString("channel", "0");
				changeView(part_searchDetailActivity.class, b);
				break;
			case R.id.part_rela_name:
				b.putString("title",
						getString(R.string.part_string_search_name));
				b.putInt("searchType", 1);
				b.putString("channel", "1");
				changeView(part_searchDetailActivity.class, b);
				break;
			case R.id.part_rela_jylx:
				b.putString("title",
						getString(R.string.part_string_search_jylx));
				b.putInt("searchType", 2);
				b.putString("channel", "2");
				changeView(part_searchDetailActivity.class, b);
				break;
			case R.id.part_rela_yqa:
				changeView(part_yqaActivity.class);
				break;
			case R.id.part_rela_qq:
				break;
			case R.id.part_rela_sina:
				break;

			}

		}

	}

	/**
	 * 初始化top
	 * */
	private void initNavigation() {
		initNavitation(getString(R.string.part_string_search_tittle), "", -1,
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
