package com.wq.me;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.Adapter.cardRelationAdapter;
import com.wq.Adapter.retationLogAdapter;
import com.wq.Interface.OnIconClickListener;
import com.wq.UI.XExpandableListView;
import com.wq.UI.XExpandableListView.IXExplandListViewListener;

import com.wq.model.User;
import com.wq.model.cardRelationList;
import com.wq.model.cardRelationModel;
import com.wq.model.partComModel;
import com.wq.partner.part_DetailActivity;

import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class card_relation_list_activity extends BaseActivity implements
		IXExplandListViewListener, OnIconClickListener {
	@ViewInject(id = R.id.list_main)
	XExpandableListView listview;
	ArrayList<cardRelationList> list = new ArrayList<cardRelationList>();
	cardRelationList rela = new cardRelationList();
	SimpleDateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");
	cardRelationAdapter adapter;
	private int currentPage = 0;

	private PopupWindow mPwindow;
	private LinearLayout mPContainer;
	private FinalBitmap mFbp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_relation_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		initUI();
		initPop();
		mFbp = FinalBitmap.create(this);
	}

	private void initUI() {
		adapter = new cardRelationAdapter(this, list, this);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this);
		listview.startRefresh(this);
		listview.setAdapter(adapter);
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			listview.expandGroup(i);
		}
		listview.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// TODO Auto-generated method stub
				listview.expandGroup(groupPosition);
			}
		});
	}

	// 数据分组
	private void explandData(cardRelationModel model) {

		if (list.size() == 0) {

			rela = new cardRelationList();
			rela.setTime(dateUtil.formatDate(model.getStartTime(), formattmp));
			rela.getList().add(model);
			list.add(rela);
		} else {
			int count = list.size();

			boolean flag = false;
			int i = 0;
			for (i = 0; i < count; i++) {

				if (list.get(i)
						.getTime()
						.equals(dateUtil.formatDate(model.getStartTime(),
								formattmp))) {
					flag = true;
					break;
				}
			}
			if (flag) {
				rela = list.get(i);
				rela.getList().add(model);

			} else {
				rela = new cardRelationList();
				rela.setTime(dateUtil.formatDate(model.getStartTime(),
						formattmp));
				rela.getList().add(model);
				list.add(rela);
			}
		}

	}

	private void initnavigation() {
		initNavitation(
				getResources().getString(R.string.me_custom_rela_list_title),
				getString(R.string.me_string_write), new EditClickListener() {
					@Override
					public void editClick() {
						changeViewForResult(recodeRelationLog.class, 102, 1);
					}

					@Override
					public void backClick() {
						finish();
						animOut();
					}
				});

	}

	@Override
	public void onRefresh() {
		list = new ArrayList<cardRelationList>();
		httpData();
	}

	@Override
	public void onLoadMore() {

	}

	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.CARD_PAGE, currentPage + "");
		params.put(httpUtil.CARD_ENTERPRISE_ID, "");
		httpUtil.post(httpUtil.CUSTOM_RELATION_LIST_URL, params,
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
								JSONArray arr = jsonResult
										.getJSONArray("relationshipArray");
								list.clear();
								for (int i = 0; i < arr.length(); i++) {
									cardRelationModel item = new cardRelationModel();
									JSONObject o = arr.getJSONObject(i);
									item.setContent(o.getString("content"));
									item.setId(o.getString("id"));
									item.setPlace(o.getString("place"));
									item.setStartTime(o.getString("time"));
									JSONArray arrPart = o
											.getJSONArray("enterpriseArray");

									for (int j = 0; j < arrPart.length(); j++) {
										JSONObject p = arrPart.getJSONObject(j);
										partComModel model = new partComModel();
										model.setEnterpriseId(p
												.getString("enterpriseId"));
										model.setIcon(p.getString("iconFile"));
										model.setName(p.getString("name"));
										model.setContactName(p
												.getString("contactName"));
										item.getPartlist().add(model);
									}
									explandData(item);
								}
								adapter.refresh(list);
								for (int i = 0; i < adapter.getGroupCount(); i++) {
									listview.expandGroup(i);
								}

							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							onLoad(listview);
							adapter.notifyDataSetChanged();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						onLoad(listview);
					}
				});
	}

	@Override
	public void showItemGroup(ArrayList<partComModel> data, int x, int y) {
		if (data == null || data.size() < 1)
			return;
		if (data.size() == 1) {
			Bundle b = new Bundle();
			b.putString("id", data.get(0).getEnterpriseId());
			changeView(part_DetailActivity.class, b);
		} else {
			showPop(data, x, y);
		}
	}

	public void initPop() {
		mPContainer = new LinearLayout(this);
		mPContainer.setOrientation(LinearLayout.HORIZONTAL);
		mPContainer.setBackgroundResource(R.drawable.icon_pop_bg);
		mPContainer.setGravity(Gravity.CENTER);
		mPwindow = new PopupWindow(mPContainer);
		ColorDrawable dw = new ColorDrawable(-00000);
		mPwindow.setBackgroundDrawable(dw);
	}

	public void showPop(final ArrayList<partComModel> list, int x, int y) {
		if (list == null || list.size() < 2)
			return;
		mPContainer.removeAllViews();
		List<LinearLayout> itemLtList = new ArrayList<LinearLayout>();
		LinearLayout itemLy = new LinearLayout(this);
		for (int i = 0; i < list.size(); i++) {
			if (i % 6 == 0) {
				itemLy = new LinearLayout(this);
				itemLy.setOrientation(LinearLayout.HORIZONTAL);
				itemLtList.add(itemLy);
			}
			ImageView mImage = new ImageView(this);
			mImage.setId(i + 1000);
			mFbp.display(mImage, list.get(i).getIcon(), BitmapFactory
					.decodeResource(getResources(), R.drawable.add_prompt),
					BitmapFactory.decodeResource(getResources(),
							R.drawable.add_prompt));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					(int) getResources().getDimension(R.dimen.dip30),
					(int) getResources().getDimension(R.dimen.dip30));
			lp.setMargins(10, 10, 10, 10);
			mImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Bundle b = new Bundle();
						b.putString("id", list.get(v.getId() - 1000)
								.getEnterpriseId());
						changeView(part_DetailActivity.class, b);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			mImage.setLayoutParams(lp);
			itemLy.addView(mImage);
		}
		for (int i = 0; i < itemLtList.size(); i++) {
			mPContainer.addView(itemLtList.get(i));
		}
		mPwindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPwindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPwindow.showAtLocation(findViewById(R.id.layout_main), Gravity.LEFT
				| Gravity.TOP,
				x - (int) getResources().getDimension(R.dimen.dip24), y
						+ (int) getResources().getDimension(R.dimen.dip48));
		mPwindow.setOutsideTouchable(true);
		mPwindow.setFocusable(true);
		mPwindow.update();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 102) {
			listview.startRefresh(card_relation_list_activity.this);
		}

	}
}
