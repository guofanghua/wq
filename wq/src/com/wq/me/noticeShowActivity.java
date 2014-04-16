package com.wq.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalDb;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.editActivity;
import com.wq.Adapter.addImagHorListviewAdapter;
import com.wq.UI.ClipActivity;
import com.wq.UI.MMAlert;
import com.wq.UI.horizontalListview.HorizontalVariableListView;
import com.wq.model.User;
import com.wq.model.ZHModel;
import com.wq.model.myObject;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.DialogUtils;
import com.wq.utils.LoggerUtil;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;

public class noticeShowActivity extends BaseActivity {

	@ViewInject(id = R.id.me_grid_ablum)
	HorizontalVariableListView horListview;
	@ViewInject(id = R.id.me_add_notice_edit_bt)
	TextView edit_title;
	@ViewInject(id = R.id.me_add_notice_edit_detail)
	TextView edit_detail;
	@ViewInject(id = R.id.me_add_notice_edit_lx)
	TextView edit_lx;
	// ArrayList<photoModel> listpic = new ArrayList<photoModel>();
	addImagHorListviewAdapter adapter;
	String strTitle = "";// 顶部菜单

	ZHModel model = null;
	int type = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_show_activity);
		BaseApplication.getInstance().addActivity(this);
		initData();
		initNavigation();

	}

	private void initData() {
		model = new ZHModel();
		Bundle b = this.getIntent().getExtras();
		if (b != null && b.getSerializable("zhModel") != null) {
			model = (ZHModel) b.getSerializable("zhModel");
			type = b.getInt("type", 0);
			bindUI();
		}

	}

	private void bindUI() {
		if (type == 0) {
			strTitle = getResources().getString(R.string.find_zh_title);
		} else
			strTitle = getResources().getString(R.string.find_zs_title);
		adapter = new addImagHorListviewAdapter(this, model.getPicList(),
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						scanPhoto(model.getPicList(), msg.what, false, false,
								null);
					}
				});
		horListview.setAdapter(adapter);
		edit_title.setText(model.getTitle());
		edit_detail.setText(model.getContent());
		if (type == 0)
			edit_lx.setText(R.string.find_zh_title);
		else
			edit_lx.setText(R.string.find_zs_title);
	}

	private void initNavigation() {
		initNavitation(strTitle, "", -1, new EditClickListener() {

			@Override
			public void editClick() {
			}

			@Override
			public void backClick() {
				finish();
				animOut();
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			finish();
			animOut();

			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
}
