package com.wq.me;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.loginActivity;
import com.wq.webviewShowActivity;
import com.wq.Adapter.nameTemplateListAdapter;
import com.wq.Adapter.templateListAdapter;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.fragment.inqFragment;
import com.wq.fragment.meFragment;
import com.wq.model.Company;
import com.wq.model.User;
import com.wq.model.nameCardMb;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.templateModel;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

public class nameCardplateListActivity extends BaseActivity {
	@ViewInject(id = R.id.grid_main)
	GridView grid_main;
	ArrayList<nameCardMb> list = new ArrayList<nameCardMb>();

	nameTemplateListAdapter adapter;

	// templateListAdapter
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.name_card_template_list_activity);
		BaseApplication.getInstance().addActivity(this);
		initnavigation();
		adapter = new nameTemplateListAdapter(this, list);
		grid_main.setAdapter(adapter);
		httpData();
		grid_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (list.get(position).getCardId().equals(User.nameCardTempId)) {
					return;
				} else
					httpUpDateData(position);
				// TODO Auto-generated method stub
			}
		});
	}

	private void initnavigation() {
		initNavitation(getString(R.string.me_name_card_temp_title), "", -1,
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

	// 获取模版
	private void httpData() {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.NAME_CARD_GET_URL, params,
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
								JSONArray templateArr = jsonResult
										.getJSONArray("businessCardArray");
								for (int i = 0; i < templateArr.length(); i++) {
									JSONObject o = templateArr.getJSONObject(i);
									nameCardMb item = new nameCardMb();
									item.setCardId(o.getString("cardId"));
									item.setFrontImgUrl(o
											.getString("frontImgUrl"));
									item.setReverseImgUrl(o
											.getString("reverseImgUrl"));
									list.add(item);
								}
								adapter.notifyDataSetChanged();

							} else {
								showToast(errMsg);
							}
						} catch (JSONException e) {
							showToast(R.string.string_http_err_data);
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

						CommonUtil.showToastHttp(
								nameCardplateListActivity.this, errorNo);
					}
				});
	}

	// 选择模板
	private void httpUpDateData(final int position) {
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.NAME_CARD_ID, list.get(position).getCardId());
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.NAME_CARD_SET_URL, params,
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
								adapter.notifyDataSetChanged();
								User.nameCardTempId = list.get(position)
										.getCardId();
								sharedPreferenceUtil
										.saveCompany(nameCardplateListActivity.this);
								// meFragment.isUpdate = true;
								// inqFragment.isUpdate = true;
								// me_name_card_activity.isUpdate = true;
								Bundle b = new Bundle();
								b.putString("title",
										getString(R.string.linq_title_wqkj));
								b.putBoolean("isShare", true);
								b.putString("url", httpUtil.mpURL(User.id));
								b.putString("mymsg", String.format(
										getString(R.string.me_name_card_msg1),
										httpUtil.mpURL(User.id)));
								// b.putString("id", User.id);
								b.putInt("flag", 1);
								changeView(webviewShowActivity.class, b);
								animIn();
							}

						} catch (JSONException e) {

							showToast(

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
						CommonUtil.showToastHttp(
								nameCardplateListActivity.this, errorNo);
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == templateDetailActivity.RESULT_FLAG) {
			if (data == null)
				return;
			int position = data.getIntExtra("position", -1);
			if (position >= 0) {
				// 更新修改的模板

				// templateModel item = (templateModel) data
				// .getSerializableExtra("tempModel");
				// User.templateId = item.getTemplateId();
				//
				// sharedPreferenceUtil
				// .saveCompany(nameCardplateListActivity.this);
				// LoggerUtil.i(position + ",," + item.getTemplateId() + ","
				// + User.templateId);
				// list.set(position, item);
				// adapter.notifyDataSetChanged();
			}

		}

	}
}
