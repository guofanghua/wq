package com.wq.localAblumScan;

import java.util.ArrayList;
import java.util.List;

import net.endure.framework.annotation.view.ViewInject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;
import com.wq.UI.MMAlert;

public class ablum_main_activity extends BaseActivity implements AlbCallBack {

	List<PicAlbumMdl> list = new ArrayList<PicAlbumMdl>();
	@ViewInject(id = R.id.gw_main)
	GridView mGw;
	private AlbumAdapter mAdapter;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_ablum_main_activity);
		BaseApplication.getInstance().addActivity(this);
		PicAlbumUtils.getAlbumListFromDevices(this, this);
		

	}

	private void initUI() {
		mAdapter = new AlbumAdapter(this, list);
		mGw.setAdapter(mAdapter);
		mGw.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(ablum_main_activity.this,
						ablum_detail_activity.class);
				Bundle bd = new Bundle();
				bd.putSerializable("data", list.get(position));
			
				i.putExtras(bd);
				startActivityForResult(i, 101);
			}
		});
	}

	private class click implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void callBackData(final List<PicAlbumMdl> list) {
		// TODO Auto-generated method stub
		// 在此处做返回数据操作
		this.list = list;
		if (list == null)
			return;
		initUI();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 101) {
			ArrayList<PicItemMdl> list = (ArrayList<PicItemMdl>) data
					.getExtras().getSerializable("data");
			// 具体选择的图片
			if (list != null && list.size() > 0) {
				Intent intent = new Intent();
				Bundle bd = new Bundle();
				bd.putSerializable("data", list);
				intent.putExtras(bd);
				setResult(MMAlert.FLAG_CHOOSE_IMG, intent);

			}
			finish();
		}
	}

}
