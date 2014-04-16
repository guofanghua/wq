package com.wq.PicCheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.PicCheck.PicCheckDeatailAdapter.TextCallback;
import com.wq.me.ablumAddActivity;
import com.wq.utils.LoggerUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PicCheckDetailActivity extends BaseActivity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	List<ImageItem> dataList;
	GridView gridView;
	PicCheckDeatailAdapter adapter;//
	AlbumHelper helper;
	public static int selectCount = 0;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(PicCheckDetailActivity.this,
						"最多选择" + PicCheckMainActivity.maxSize + "张图片", 400)
						.show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_check_detail_activity);
		selectCount = 0;
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);
		for (picBitmap picitem : Bimp.picbmpList) {
			for (ImageItem item : dataList) {

				if (item.imagePath.equals(picitem.getFilePath())) {
					item.isSelected = true;
					selectCount++;
					break;
				}
			}
		}
		initNivagation(selectCount);
		initView();
	}

	/**
	 * 
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new PicCheckDeatailAdapter(PicCheckDetailActivity.this,
				dataList, selectCount, mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				// selectCount = count;
				initNivagation(count);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
			}

		});

	}

	private void initNivagation(int count) {
		initNavitation("xiangqing", "quding" + count,
				R.drawable.btn_green_clickbg, new EditClickListener() {

					@Override
					public void editClick() {
						// TODO Auto-generated method stub
						ArrayList<String> list = new ArrayList<String>();
						Collection<String> c = adapter.map.values();
						Iterator<String> it = c.iterator();
						for (; it.hasNext();) {
							list.add(it.next());
						}

						// if (Bimp.act_bool) {
						if (list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								if (Bimp.picbmpList.size() < PicCheckMainActivity.maxSize) {
									picBitmap itemBitmap = new picBitmap();
									itemBitmap.setFilePath(list.get(i));
									Bimp.picbmpList.add(itemBitmap);
								}
							}
							setResult(PicCheckMainActivity.RESULT_CODE);
							Bimp.act_bool = false;
							// }

						}

						finish();
						animOut();

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
