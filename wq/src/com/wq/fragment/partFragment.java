package com.wq.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseActivity.EditClickListener;
import com.wq.mainActivity;
import com.wq.Adapter.partSortAdapter;
import com.wq.UI.indexLayout;
import com.wq.me.MingPListActivity;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.partner.part_DetailActivity;
import com.wq.partner.part_searchActivity;
import com.wq.partner.part_tipActivity;
import com.wq.sortListView.StringHelper;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 我的伙伴
 * */
public class partFragment extends Fragment {
	private ListView listview;
	ArrayList<partComModel> listModel = new ArrayList<partComModel>();
	ArrayList<partComModel> newlistModel = new ArrayList<partComModel>();
	// private ArrayList<partComModel> list = new ArrayList<partComModel>();
	public static boolean isUpdate = false;// 用于标示，发送广播来时，是都需要加载数据
	public static boolean broLoadFlag = true;// 用于标示resume是否需要加载数据
	public static final String PART_BRO_ACTION_NAME = "com.wq.fragment.partFragment";

	public DataReceiver dataReceiver = null;
	partSortAdapter adapter = null;
	// CustomProgressDialog progressDialog = null;
	FinalDb db;
	// sortlist
	private indexLayout layoutIndex;
	private String[] indexStr = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	TextView tv_show;

	// 获取动态数据更新ui的handler

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isUpdate = false;
		View v = inflater
				.inflate(R.layout.part_main_fragment, container, false);
		db = FinalDb.create(getActivity());
		initnavigation(v);
		initUI(v);
		registerBoradcastReceiver();
		return v;
	}

	private void initUI(View v) {
		tv_show = (TextView) v.findViewById(R.id.tv);
		listview = (ListView) v.findViewById(R.id.part_list_main);
		adapter = new partSortAdapter(getActivity(), newlistModel);
		listview.setAdapter(adapter);
		layoutIndex = (indexLayout) v.findViewById(R.id.layout_index);
		refreshLoclaData();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (position == 0) {
					Intent intent = new Intent(getActivity(),
							part_tipActivity.class);
					getActivity().startActivity(intent);
					animIn();
				} else if (position == 1) {
					Intent intent = new Intent(getActivity(),
							MingPListActivity.class);
					getActivity().startActivity(intent);
				} else {
					partComModel item = newlistModel.get(position - 2);
					if (item.getPycontactName().length() > 1) {
						Intent intent = new Intent(getActivity(),
								part_DetailActivity.class);
						Bundle b = new Bundle();
						b.putString("id", item.getEnterpriseId());
						b.putString("channel", item.getChannel());
						b.putBoolean("isUpdate", item.getType() == 1 ? true
								: false);
						intent.putExtras(b);
						getActivity().startActivity(intent);
						animIn();
					}
				}
			}
		});
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

					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							ArrayList<partComModel> tempList = new ArrayList<partComModel>();
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
									tempList.add(item);
								}
								isUpdate = false;
								broLoadFlag = false;
								if (tempList.size() > 0) {
									Intent broadcast = new Intent();
									broadcast
											.setAction(mainActivity.deyServiceBor);
									broadcast.putExtra("newMeAttention",
											tempList);
									getActivity().sendOrderedBroadcast(
											broadcast, null);
								}
							}

						} catch (JSONException e) {
							isUpdate = true;
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
						isUpdate = true;
						// TODO Auto-generated method stub
						// progressDialog.cancel();

					}
				});

	}

	public void initnavigation(View v) {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(
				getActivity(), 35), DensityUtil.dip2px(getActivity(), 35));

		BaseActivity.initNavigation(v, -1, "",
				getResources().getString(R.string.menu_mpl), null,
				R.drawable.add_title_pic_click, params,
				new EditClickListener() {

					@Override
					public void editClick() {
						Intent init_notice = new Intent(getActivity(),
								part_searchActivity.class);
						getActivity().startActivity(init_notice);
						animIn();
						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						// TODO Auto-generated method stub
					}
				});
	}

	public void animIn() {
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version > 5) {
			getActivity().overridePendingTransition(R.anim.base_slide_right_in,
					R.anim.transition_in);
		}
	}

	public void onResume() {
		super.onResume();
		registerBoradcastReceiver();
		if ((User.bArr[2] || User.bArr[3]) && mainActivity.currIndex == 1) {
			// adapter.notifyDataSetChanged();
			// 改变状态
		}

	}

	public void onDestory() {
		super.onDestroy();
		unregisterBroadcaseReceiver();

	}

	// 注册广播
	public void registerBoradcastReceiver() {
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(PART_BRO_ACTION_NAME);
			filter.addAction(mainActivity.deyServiceBor);
			getActivity().registerReceiver(dataReceiver, filter);
		}

	}

	// 注销广播
	public void unregisterBroadcaseReceiver() {
		if (dataReceiver != null) {
			getActivity().unregisterReceiver(dataReceiver);
			dataReceiver = null;
		}
	}

	private class DataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(PART_BRO_ACTION_NAME)) {

				// 刷新，获取数据
				// 获取数据成功后，
				if (newlistModel.size() <= 2) {
					httpData();
				}
			} else if (action.equals(mainActivity.deyServiceBor)) {
				boolean HasUpdate = false;
				// 有最新关注我的伙伴
				if (intent.getSerializableExtra("newAttention") != null) {
					ArrayList<partComModel> list = (ArrayList<partComModel>) intent
							.getSerializableExtra("newAttention");
					for (int i = 0; i < list.size(); i++) {
						try {
							StringBuilder b = new StringBuilder();
							b.append("flag = '1'");
							b.append(" and ");
							b.append("userId='");
							b.append(User.id);
							b.append("'");
							b.append(" and ");
							b.append("enterpriseId ='");
							b.append(list.get(i).getEnterpriseId());
							b.append("'");
							if (db.findAllByWhere(partComModel.class,
									b.toString()).size() > 0) {
								db.update(list.get(i));
							} else
								db.save(list.get(i));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

				}
				// 有最新取消关注我的伙伴
				if (intent.getStringExtra("cancelId") != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("flag='1'");
					sb.append(" and ");
					sb.append("enterpriseId in ( ");
					sb.append(intent.getStringExtra("cancelId"));
					sb.append(" )");
					db.deleteByWhere(partComModel.class, sb.toString());

				}
				// 名片更新
				if (intent.getSerializableExtra("mplistModel") != null) {
					ArrayList<partComModel> list = (ArrayList<partComModel>) intent
							.getSerializableExtra("mplistModel");
					for (int i = 0; i < newlistModel.size(); i++) {
						partComModel item = newlistModel.get(i);
						for (partComModel item1 : list) {
							if (item.getEnterpriseId().equals(
									item1.getEnterpriseId())) {
								item1.setPid(item.getPid());
								item1.setTime(item.getTime());
								LoggerUtil.i("kkkkk=" + item1.getContactName()
										+ ",,," + item1.getOccupation());
								newlistModel.set(i, item1);
								HasUpdate = true;
								try {
									db.update(item1, "pid='" + item.getPid()
											+ "'");
								} catch (Exception ex) {
									ex.printStackTrace();
								}

								break;
							}
						}
					}

				}

				// 有我最新关注的伙伴
				if (intent.getSerializableExtra("newMeAttention") != null) {
					ArrayList<partComModel> list = (ArrayList<partComModel>) intent
							.getSerializableExtra("newMeAttention");
					for (int i = 0; i < list.size(); i++) {
						StringBuilder sb = new StringBuilder();
						sb.append("flag='0'");
						sb.append(" and ");
						sb.append("userId='");
						sb.append(User.id);
						sb.append("'");
						sb.append(" and ");
						sb.append("enterpriseId = '");
						sb.append(list.get(i).getEnterpriseId());
						sb.append("'");
						try {
							if (db.findAllByWhere(partComModel.class,
									sb.toString()).size() > 0) {
								db.update(list.get(i), sb.toString());
							} else {

								db.save(list.get(i));
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						HasUpdate = true;
					}

				}

				// 有我最新取消的伙伴
				if (intent.getStringExtra("meCancelId") != null) {

					StringBuilder sb = new StringBuilder();
					sb.append("flag='0'");
					sb.append(" and ");
					sb.append("userId='");
					sb.append(User.id);
					sb.append("'");
					sb.append(" and ");
					sb.append("enterpriseId in ( ");
					sb.append(intent.getStringExtra("meCancelId"));
					sb.append(" )");
					try {
						db.deleteByWhere(partComModel.class, sb.toString());
					} catch (Exception e) {
						// TODO: handle exception
					}
					HasUpdate = true;
				}
				if (HasUpdate) {
					refreshLoclaData();

				}
				if (adapter == null || adapter.txt_tip == null)
					return;
				// adapter.notifyDataSetChanged();
				if (User.bArr[2]) {
					adapter.txt_tip.setVisibility(View.VISIBLE);
					adapter.txt_tip.setText(User.tipCountArr[1]);
				} else
					adapter.txt_tip.setVisibility(View.GONE);
			}

		}
	}

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

			int layoutHeight = DensityUtil.intScreenHeight(getActivity())
					- DensityUtil.dip2px(getActivity(), 40)
					- DensityUtil.dip2px(getActivity(), 105)
					- DensityUtil.statusHeight(getActivity());
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
		int padding = DensityUtil.dip2px(getActivity(), 5);
		// int testSize = DensityUtil.dip2px(getActivity(), 35);
		// .i("padding+testsize" + padding + ",,," + testSize);
		for (int i = 0; i < indexStr.length; i++) {
			final TextView tv = new TextView(getActivity());
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
		listModel.add(initMyComModel());
		// listModel.add(object)
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
			if (listModel.size() <= 0)
				httpData();
			else {
				broLoadFlag = false;
				isUpdate = false;
				synchronized (this) {
					if (adapter == null) {
						adapter = new partSortAdapter(getActivity(),
								newlistModel);
						listview.setAdapter(adapter);
					} else
						adapter.notifyDataSetChanged();
					// isFlash = true;
				}
			}

		}

	};

	public void onPause() {
		super.onPause();
		unregisterBroadcaseReceiver();
	}

	// 把自身加入进去
	private partComModel initMyComModel() {
		partComModel myComModel = new partComModel();
		myComModel.initData();
		return myComModel;
	}
}
