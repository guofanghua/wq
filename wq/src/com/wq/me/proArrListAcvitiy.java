package com.wq.me;

import java.util.ArrayList;

import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.BaseActivity.EditClickListener;
import com.wq.Adapter.proAddAttrAdapter;
import com.wq.UI.InScrolllistView;
import com.wq.model.User;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.utils.CommonUtil;

public class proArrListAcvitiy extends BaseActivity {
	ArrayList<proAttr> list = new ArrayList<proAttr>();
	proAddAttrAdapter adapter;
	@ViewInject(id = R.id.listview)
	InScrolllistView listView;
	@ViewInject(id = R.id.txt_add_attr)
	TextView txt_add;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pro_attr_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initNavigation();
		initData();
	}

	private void initData() {
		if (this.getIntent().getSerializableExtra("proArrtList") != null) {
			list = (ArrayList<proAttr>) this.getIntent().getSerializableExtra(
					"proArrtList");
		}
		if (list.size() == 0) {
			String[] defaultAttr = getResources().getStringArray(
					R.array.name_pro_attr);
			for (int i = 0; i < defaultAttr.length; i++) {
				proAttr itemAttr = new proAttr();
				itemAttr.setId(i + "");
				itemAttr.setAttrKey(defaultAttr[i]);
				list.add(itemAttr);
			}
		}
		adapter = new proAddAttrAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("list", list);
				bundle.putInt("position", position);
				changeViewForResult(proAttrEditActivity.class, bundle, 101, 1);
				// TODO Auto-generated method stub

			}
		});
		txt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Bundle bundle = new Bundle();
				bundle.putSerializable("list", list);
				bundle.putInt("position", -1);
				changeViewForResult(proAttrEditActivity.class, bundle, 101, 1);
			}
		});
	}

	private void initNavigation() {
		initNavitation(getString(R.string.me_string_product_attr),
				getString(R.string.string_save),
				R.drawable.title_btn_right_click, new EditClickListener() {
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						ArrayList<proAttr> tmplist = new ArrayList<proAttr>();
						for (proAttr item : list) {
							if (!TextUtils.isEmpty(item.getAttrValue())) {
								tmplist.add(item);
							}
						}
						if (tmplist.size() == 0)
							return;
						Intent intent = new Intent();
						intent.putExtra("list", tmplist);
						setResult(100, intent);
						finish();

					}

					@Override
					public void backClick() {
						finish();
						animOut();
						// TODO Auto-generated method stub

					}
				});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		{
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == 101) {
				if (data != null) {
					int position = data.getIntExtra("position", -1);
					proAttr item = (proAttr) data
							.getSerializableExtra("attrModel");
					if (position < 0) {
						list.add(item);
					} else {
						list.set(position, item);
					}
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
