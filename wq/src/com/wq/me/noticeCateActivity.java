package com.wq.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.noticeCateAdapter;
import com.wq.model.notice;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;

public class noticeCateActivity extends BaseActivity {
	@ViewInject(id = R.id.list)
	ListView listview;
	String cateName = "";
	ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	noticeCateAdapter adapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_cate_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavagation();
		initData();

	}

	private void initData() {
		if (this.getIntent() != null) {
			cateName = this.getIntent().getStringExtra("cateName");
		}
		String[] arr = getResources().getStringArray(R.array.notice_cate_arr);
		for (String s : arr) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", s);
			list.add(map);
		}
		adapter = new noticeCateAdapter(this, list, cateName);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int positon, long id) {
				LoggerUtil.i(list.get(positon).get("name"));
				Intent intent = new Intent();
				intent.putExtra("cateName", list.get(positon).get("name"));
				setResult(noticeAddActivity.CATE_FLAG, intent);
				finish();
				animOut();
				// TODO Auto-generated method stub

			}
		});
	}

	private void initNavagation() {
		initNavitation(getResources().getString(R.string.notice_cate_title),
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

}
