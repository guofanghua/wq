package com.wq.me;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.webviewShowActivity;
import com.wq.Adapter.collectListAdapter;
import com.wq.Adapter.collectListAdapter.onRightItemClickListener;
import com.wq.Adapter.proListAdapter;
import com.wq.UI.SwipeListView;
import com.wq.UI.TypeListPopDialog;
import com.wq.find.CaptureActivity;
import com.wq.model.User;
import com.wq.model.meCollectModel;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.productList;
import com.wq.model.product;
import com.wq.partner.part_yqaActivity;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;

/**
 * 我的收藏
 * */
public class collectActivity extends BaseActivity {

	@ViewInject(id = R.id.me_list_collect)
	SwipeListView listview;
	ArrayList<meCollectModel> list = new ArrayList<meCollectModel>();
	collectListAdapter adapter;
	FinalDb db;
	protected String[] mPlayTypes;// 需要查询的类型

	@ViewInject(id = R.id.top_btn_edit)
	Button btn_top;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_collect_activity);
		BaseApplication.getInstance().addActivity(this);
		db = FinalDb.create(this);
		mPlayTypes = getResources().getStringArray(R.array.me_collect_arr);
		initnavigation();
		localData();
		// 点击删除按钮
		adapter.setOnRightItemClickListener(new onRightItemClickListener() {

			@Override
			public void onRightItemClick(View v, int position) {
				// TODO Auto-generated method stub
				meCollectModel itemCollectModel = list.get(position);
				try {
					db.deleteByWhere(meCollectModel.class, "pid='"
							+ itemCollectModel.getPid() + "'");
					list.remove(position);
					showRightDialog(getString(R.string.me_collect_del_success_warn));
				} catch (Exception ex) {
					ex.printStackTrace();
					showToast(R.string.me_collect_del_err_warn);
				} finally {
					// 关闭所有的listview
					listview.hindAllRightView();
					adapter.notifyDataSetChanged();
				}
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Bundle bundle = new Bundle();

				if (list.get(position).getType() <= 1) {
					bundle.putBoolean("isShare", true);
					if (list.get(position).getType() == 1) {
						bundle.putString("mymsg", String.format(
								getString(R.string.me_name_card_msg1),
								httpUtil.mpURL(list.get(position).geteId())));
					}
					bundle.putSerializable("meCollectModel", list.get(position));
					changeView(webviewShowActivity.class, bundle);
				} else {
					bundle.putSerializable("meCollectModel", list.get(position));
					changeView(MeCollectShowActivity.class, bundle);
				}
				// TODO Auto-generated method stub
			}
		});

	}

	private void localData() {
		if (db.checkmyTableExist(meCollectModel.class)) {
			list = (ArrayList<meCollectModel>) db.findAllByWhere(
					meCollectModel.class, "userId='" + User.id + "'",
					"time desc");
		}
		adapter = new collectListAdapter(collectActivity.this, list,
				listview.getRightViewWidth());
		listview.setAdapter(adapter);

	}

	private void initnavigation() {
		LayoutParams params = new LayoutParams(DensityUtil.dip2px(this, 35),
				DensityUtil.dip2px(this, 35));
		initNavitation(getResources().getString(R.string.me_string_wdsc), "",
				R.drawable.add_title_pic_click, params,
				new EditClickListener() {
					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						loadTypeListDialog(mPlayTypes);
						if (typeListDialog != null)
							typeListDialog
									.showAsDropDown(btn_top, 0, DensityUtil
											.dip2px(collectActivity.this, 2));
					}

					@Override
					public void backClick() {

						// TODO Auto-generated method stub
						finish();
						animOut();
					}
				});

	}

	// 加载popup
	private TypeListPopDialog typeListDialog;

	public void loadTypeListDialog(String[] comm) {
		if (comm == null)
			return;
		typeListDialog = new TypeListPopDialog(this,
				R.layout.card_name_popup_dialog);
		typeListDialog.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		typeListDialog.loadCollectView(comm, handler);
		// 为弹出框设置自定义的布局文件
		typeListDialog.setCardContentView();
		typeListDialog.setFocusable(true);
		typeListDialog.setBackgroundDrawable(new BitmapDrawable());// 一定要设置背景才起点击外面关闭才起作用
		typeListDialog.setOutsideTouchable(true);
		// 消失是出发的事件
		typeListDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub

			}
		});
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			// 完善资料
			case 1:
				Bundle bundle = new Bundle();
				bundle.putInt("type", msg.what + 1);
				changeViewForResult(MeCollectWZActivity.class, bundle, 200, 1);
				break;

			}
			typeListDialog.dismiss();
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			// 添加
			if (resultCode == 200) {
				meCollectModel collectModel = (meCollectModel) data
						.getSerializableExtra("meCollectModel");
				if (collectModel != null) {
					list.add(0, collectModel);
					listview.hindAllRightView();
					adapter.notifyDataSetChanged();
					showRightDialog(R.string.me_collect_success_warn);
				}

			}
		}
	}

}
