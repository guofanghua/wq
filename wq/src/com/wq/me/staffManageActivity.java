package com.wq.me;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout.LayoutParams;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.partListAdapter;
import com.wq.Adapter.partSortAdapter;
import com.wq.Adapter.partTipSortAdapter;
import com.wq.Adapter.staffSortAdapter;
import com.wq.UI.CustomProgressDialog;
import com.wq.UI.MMAlert;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XListView;
import com.wq.UI.indexLayout;
import com.wq.UI.MMAlert.onitemClick;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;
import com.wq.UI.XListView.IXListViewListener;
import com.wq.model.User;
import com.wq.model.mmAlertModel;
import com.wq.model.partComModel;
import com.wq.partner.part_DetailActivity;
import com.wq.partner.part_tipActivity;
import com.wq.sortListView.StringHelper;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/**
 * 显示主动加我为伙伴的企业列表
 * 
 * */

public class staffManageActivity extends BaseActivity {
	@ViewInject(id = R.id.list_main)
	ListView listview;
	ArrayList<partComModel> listModel = new ArrayList<partComModel>();
	ArrayList<partComModel> newlistModel = new ArrayList<partComModel>();
	staffSortAdapter adapter = null;
	@ViewInject(id = R.id.layout_index)
	indexLayout layoutIndex;
	private String[] indexStr = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	@ViewInject(id = R.id.tv)
	TextView tv_show;
	FinalDb db;
	ArrayList<mmAlertModel> mmList = new ArrayList<mmAlertModel>();
	String[] arr = null;

	// mmList = CommonUtil.getSplitmmAlertModel(arr);
	// 无数据
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.staff_manage_activity);
		BaseApplication.getInstance().addActivity(this);
		arr = getResources().getStringArray(R.array.work_check_rep_arr);
		mmList = CommonUtil.getSplitmmAlertModel(arr);
		db = FinalDb.create(this);
		initNavigation();
		initUI();

	}

	private void initUI() {
		adapter = new staffSortAdapter(this, newlistModel);
		adapter.setClick(new staffSortAdapter.DetialItemClick() {

			@Override
			public void onClick(int position) {
				// TODO Auto-generated method stub
				partComModel item = newlistModel.get(position - 2);
				if (item.getPycontactName().length() > 1) {

					Bundle b = new Bundle();
					b.putString("id", item.getEnterpriseId());
					b.putString("channel", item.getChannel());
					b.putBoolean("isUpdate", item.getType() == 1 ? true : false);
					changeView(part_DetailActivity.class, b);
				}
			}
		});
		listview.setAdapter(adapter);
		refreshLoclaData();
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				MMAlert.showAlertListView(staffManageActivity.this, mmList,
						true, new onitemClick() {

							@Override
							public void onItemClick(int position,
									mmAlertModel item) {
								if (item.getIndex().equals("1")) {
									Bundle bundle = new Bundle();
									bundle.putInt("currInex", 0);
									changeView(WorkRepListActivity.class,
											bundle);
								} else if (item.getIndex().equals("2")) {
									Bundle bundle = new Bundle();
									bundle.putInt("currInex", 1);
									changeView(WorkRepListActivity.class,
											bundle);
								}
								// TODO Auto-generated method stub

							}
						});
			}
		});

	}

	/**
	 * isRefresh 是否 刷新
	 * */
	int allCount = 0;

	private void initNavigation() {
		initNavitation(getString(R.string.part_string_wdyg), "", -1,
				new EditClickListener() {

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

	/**
	 * 获取排序后的新数据
	 * 
	 * @param persons
	 * @return
	 */
	public String[] sortIndex(ArrayList<partComModel> list) {
		TreeSet<String> set = new TreeSet<String>();
		// 获取初始化数据源中的首字母，添加到set中

		for (partComModel item : list) {
			if (TextUtils.isEmpty(item.getContactName())) {
				set.add("#");
			} else
				set.add(StringHelper.getPinYinHeadChar(item.getContactName())
						.substring(0, 1));
		}
		// 新数组的长度为原数据加上set的大小
		String[] names = new String[list.size() + set.size()];
		ArrayList<String> name1 = new ArrayList<String>();
		String[] names2 = new String[list.size() + set.size()];
		int i = 0;

		for (String s : set) {
			names[i++] = s;
		}
		String[] pinYinNames = new String[list.size()];
		for (int j = 0; j < list.size(); j++) {
			list.get(j).setPycontactName(
					StringHelper.getPingYin(list.get(j).getContactName()
							.toString(), list.get(j).getEnterpriseId()));

			pinYinNames[j] = StringHelper
					.getPingYin(list.get(j).getContactName().toString(), list
							.get(j).getEnterpriseId());
		}
		// 将原数据拷贝到新数据中
		System.arraycopy(pinYinNames, 0, names, set.size(), pinYinNames.length);
		// 自动按照首字母排序
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
		int j = 0;
		for (String s : names) {

			if ((s.equals("#") || s.startsWith("#@&")) && !TextUtils.isEmpty(s)) {
				name1.add(s);
			} else if (!TextUtils.isEmpty(s))
				names2[j++] = s;
		}

		for (int k = 0; k < name1.size(); k++) {

			names2[j++] = name1.get(k);

		}

		return names2;
	}

	private int height;// 字体高度
	private boolean flag = false;

	/**
	 * 绘制索引列表
	 */
	public void getIndexView() {
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, height);
		int padding = DensityUtil.dip2px(this, 5);
		// int testSize = DensityUtil.dip2px(this, 35);
		// .i("padding+testsize" + padding + ",,," + testSize);
		for (int i = 0; i < indexStr.length; i++) {
			final TextView tv = new TextView(this);
			tv.setLayoutParams(params);
			tv.setText(indexStr[i]);
			// tv.setTextSize(testSize);
			tv.setPadding(padding, 0, padding, 0);
			layoutIndex.addView(tv);
			layoutIndex.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					{
						float y = event.getY();
						int index = (int) (y / height);

						if (index > -1 && index < indexStr.length) {// 防止越界
							String key = indexStr[index];
							if (selector.containsKey(key)) {
								int pos = selector.get(key);
								if (listview.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
									listview.setSelectionFromTop(
											pos
													+ listview
															.getHeaderViewsCount(),
											0);
								} else {
									listview.setSelectionFromTop(pos, 0);// 滑动到第一项
								}

							}
						}
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							layoutIndex.setBackgroundColor(Color
									.parseColor("#606060"));
							break;

						case MotionEvent.ACTION_MOVE:
							tv_show.setVisibility(View.VISIBLE);
							if (index < indexStr.length && index > -1)
								tv_show.setText(indexStr[index]);
							break;
						case MotionEvent.ACTION_UP:
							layoutIndex.setBackgroundColor(Color
									.parseColor("#00ffffff"));
							tv_show.setVisibility(View.GONE);
							break;
						}
						return true;
					}

				}
			});
		}
	}

	// 获取本地数据库
	public void refreshLoclaData() {
		listModel.clear();
		StringBuilder sb = new StringBuilder();
		sb.append("flag='0'");
		sb.append(" and ");
		sb.append("userId='");
		sb.append(User.id);
		sb.append("'");
		// 获取数据
		try {
			if (db.findAllByWhere(partComModel.class, sb.toString()).size() > 0) {
				listModel.addAll((ArrayList<partComModel>) db.findAllByWhere(
						partComModel.class, sb.toString()));
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
		}

		initsortList();
		Message msg = h.obtainMessage();
		msg.sendToTarget();

	}

	Handler h = new Handler() {
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			// 构建一条数据
			if (listModel.size() <= 0) {
			}
			// httpData();
			else {

				synchronized (this) {
					if (adapter == null) {
						adapter = new staffSortAdapter(
								staffManageActivity.this, newlistModel);
						listview.setAdapter(adapter);
					} else
						adapter.notifyDataSetChanged();
					// isFlash = true;
				}
			}

		}

	};

	// 遍历数据库，看此条记录是否存在本地
	// 排序并绑定数据
	private void initsortList() {
		String[] allNames = sortIndex(listModel);

		sortList(allNames);

		selector = new HashMap<String, Integer>();
		for (int j = 0; j < indexStr.length; j++) {// 循环字母表，找出newPersons中对应字母的位置
			for (int i = 0; i < newlistModel.size(); i++) {
				if (newlistModel.get(i).getContactName().equals(indexStr[j])) {
					selector.put(indexStr[j], i);
				}
			}
		}
	}

	/**
	 * 重新排序获得一个新的List集合
	 * 
	 * @param allNames
	 */
	private void sortList(String[] allNames) {
		newlistModel.clear();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < allNames.length; i++) {
			if (allNames[i].length() != 1) {
				for (int j = 0; j < listModel.size(); j++) {
					if (allNames[i].equals(listModel.get(j).getPycontactName())) {
						// 解决同名的bug
						if (sb.toString().indexOf(
								listModel.get(j).getEnterpriseId()) < 0) {
							partComModel p = listModel.get(j);
							newlistModel.add(p);
							sb.append(listModel.get(j).getEnterpriseId());
						}
					}
				}
			} else {
				newlistModel.add(new partComModel(allNames[i]));
			}
		}
	}

	public void onStart() {
		super.onStart();
		if (!flag) {// 这里为什么要设置个flag进行标记，我这里不先告诉你们，请读者研究，因为这对你们以后的开发有好处

			int layoutHeight = DensityUtil.intScreenHeight(this)
					- DensityUtil.dip2px(this, 40)
					- DensityUtil.dip2px(this, 105)
					- DensityUtil.statusHeight(this);
			height = layoutHeight / indexStr.length;
			getIndexView();
			flag = true;
		}
	}
}
