package com.wq.me;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.cardRelaPartSortAdapter;
import com.wq.Adapter.cardRelaPartSortAdapter.ContListener;

import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.sortListView.StringHelper;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

public class card_relation_select_people_activity extends BaseActivity
		implements ContListener {
	private ListView listview;
	ArrayList<partComModel> listModel = new ArrayList<partComModel>();
	ArrayList<partComModel> newlistModel = new ArrayList<partComModel>();
	private LinearLayout layoutIndex;
	private String[] indexStr = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	int checkCount = 0;
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	TextView tv_show;
	FinalDb db;
	cardRelaPartSortAdapter adapter;

	// 顶部
	@ViewInject(id = R.id.top_btn_back)
	Button btn_back;
	@ViewInject(id = R.id.top_btn_edit)
	Button btn_ok;
	@ViewInject(id = R.id.txt_tip)
	TextView txt_tip;
	ArrayList<partComModel> resultList = new ArrayList<partComModel>();
	private int selectNum = -1;// 最多限制选择人数

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_relation_select_people_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		initNavigation();
		initUI();

	}

	@SuppressWarnings("unchecked")
	private void initUI() {
		if (this.getIntent().getSerializableExtra("result") != null) {

			resultList = (ArrayList<partComModel>) this.getIntent()
					.getSerializableExtra("result");

		}
		selectNum = this.getIntent().getIntExtra("Num", -1);

		tv_show = (TextView) findViewById(R.id.tv);
		listview = (ListView) findViewById(R.id.part_list_main);
		layoutIndex = (LinearLayout) findViewById(R.id.layout_index);
		refreshLoclaData();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View parent,
					int position, long arg3) {

				count(position);

			}

		});
	}

	private void initNavigation() {
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				animDown();

			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<partComModel> list = new ArrayList<partComModel>();
				if (newlistModel != null) {
					for (int i = 0; i < newlistModel.size(); i++) {
						partComModel model = newlistModel.get(i);
						if (model.isCheck()) {

							list.add(model);
						}

					}
				}
				Intent intent = new Intent();
				Bundle bd = new Bundle();
				bd.putSerializable("result", list);
				intent.putExtras(bd);
				setResult(102, intent);
				finish();
				animDown();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//Intent intent = new Intent();
			//setResult(102, intent);
			finish();
			animDown();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * isRefresh 是否 刷新
	 * */
	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		httpUtil.post(httpUtil.PART_LIST_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

						// if (progressDialog == null)
						// progressDialog = new CustomProgressDialog(
						// card_relation_select_people_activity.this);
						// if (mainActivity.currIndex == 1) {
						// progressDialog.create(R.string.dialog_loading);
						// progressDialog.setCanceledOnTouchOutside(false);
						// progressDialog.show();
						// }

					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							listModel.clear();
							newlistModel.clear();
							if (errcode.equals(httpUtil.errCode_success)) {
								JSONArray arr = jsonResult.getJSONArray("data");
								for (int i = 0; i < arr.length(); i++) {
									JSONObject o = arr.getJSONObject(i);
									partComModel item = new partComModel();
									item.setPid(o.getString("id"));
									item.setEnterpriseId(o
											.getString("enterpriseId"));
									item.setIcon(o.getString("icon"));
									item.setName(o.getString("name"));
									item.setTelePhone(o.getString("telePhone"));
									item.setMobile(o.getString("moblie"));
									item.setContactName(o
											.getString("contactName"));
									item.setOccupation(o
											.getString("occupation"));
									item.setFlag("0");
									CheckModel(item);
								}
								initsortList();
								if (adapter == null) {
									adapter = new cardRelaPartSortAdapter(
											card_relation_select_people_activity.this,
											card_relation_select_people_activity.this);
									listview.setAdapter(adapter);
								} else

									adapter.notifyDataSetChanged();
							} else if (errcode.equals(httpUtil.errCode_nodata)) {
								CommonUtil
										.showToast(
												card_relation_select_people_activity.this,
												errMsg);
							} else {
								// noData(1);
								CommonUtil
										.showToast(
												card_relation_select_people_activity.this,
												errMsg);
							}

						} catch (JSONException e) {

							// noData(1);
							CommonUtil.showToast(
									card_relation_select_people_activity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							// progressDialog.cancel();

						}

						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// noData(1);

						// TODO Auto-generated method stub
						// progressDialog.cancel();

						CommonUtil.showToastHttp(
								card_relation_select_people_activity.this,
								errorNo);
					}
				});

	}

	// 遍历数据库，看此条记录是否存在本地

	private void CheckModel(partComModel model) {
		boolean flag = false;
		int position = 0;
		for (int i = 0; i < listModel.size(); i++) {
			if (listModel.get(i).getEnterpriseId()
					.equals(model.getEnterpriseId())) {
				flag = true;
				position = i;
				break;
			}

		}
		if (flag) {
			listModel.set(position, model);
			try {
				db.update(model);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			listModel.add(model);
			try {
				db.save(model);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

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

			int layoutHeight = DensityUtil
					.intScreenHeight(card_relation_select_people_activity.this)
					- DensityUtil.dip2px(
							card_relation_select_people_activity.this, 40)
					- DensityUtil.dip2px(
							card_relation_select_people_activity.this, 105)
					- DensityUtil
							.statusHeight(card_relation_select_people_activity.this);
			height = layoutHeight / indexStr.length;
			getIndexView();
			flag = true;
		}
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
		int padding = DensityUtil.dip2px(
				card_relation_select_people_activity.this, 5);
		for (int i = 0; i < indexStr.length; i++) {
			final TextView tv = new TextView(
					card_relation_select_people_activity.this);
			tv.setLayoutParams(params);
			tv.setText(indexStr[i]);
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
		LoggerUtil.i("ssssssssssssss");
		// isFlash = false;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				listModel.clear();
				StringBuilder sb = new StringBuilder();
				sb.append("flag='0'");
				sb.append(" and ");
				sb.append("userId='");
				sb.append(User.id);
				sb.append("'");
				// 获取数据
				try {
					if (db.findAllByWhere(partComModel.class, sb.toString())
							.size() > 0) {
						listModel = (ArrayList<partComModel>) db
								.findAllByWhere(partComModel.class,
										sb.toString());
					}
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
				}
				initsortList();
				Message msg = h.obtainMessage();
				msg.sendToTarget();

			}
		}).start();

	}

	Handler h = new Handler() {
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			// 构建一条数据
			if (listModel.size() <= 0)
				httpData();
			else {

				synchronized (this) {
					if (adapter == null) {
						adapter = new cardRelaPartSortAdapter(
								card_relation_select_people_activity.this,
								card_relation_select_people_activity.this);
						listview.setAdapter(adapter);
					} else
						adapter.notifyDataSetChanged();
					// isFlash = true;
				}
			}

		}

	};

	@Override
	public void count(int position) {
		if (selectNum == 1) {
			ArrayList<partComModel> list = new ArrayList<partComModel>();
			list.add(newlistModel.get(position));
			Intent intent = new Intent();
			Bundle bd = new Bundle();
			bd.putSerializable("result", list);
			intent.putExtras(bd);
			setResult(102, intent);
			finish();
			animDown();
		} else if (checkCount < selectNum || selectNum == -1) {

			partComModel model = newlistModel.get(position);

			if (!model.isCheck()) {
				checkCount++;
				model.setCheck(true);
			} else {
				checkCount--;
				model.setCheck(false);
			}

			if (checkCount == 0)
				txt_tip.setVisibility(View.GONE);
			else {
				txt_tip.setVisibility(View.VISIBLE);
				txt_tip.setText(checkCount + "");
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public int getCount() {

		if (newlistModel != null) {
			if (resultList.size() > 0) {
				for (partComModel item : newlistModel) {
					for (partComModel item1 : resultList) {
						if (item.getEnterpriseId().equals(
								item1.getEnterpriseId())) {
							item.setCheck(true);
							checkCount++;
							break;
						}
					}
				}
				if (checkCount > 0) {
					txt_tip.setVisibility(View.VISIBLE);
					txt_tip.setText(checkCount + "");
				}
				resultList.clear();
			}
			return newlistModel.size();
		} else {
			return 0;
		}
	}

	@Override
	public partComModel getItem(int position) {

		if (newlistModel != null) {
			return newlistModel.get(position);
		} else {
			return null;
		}
	}

}
