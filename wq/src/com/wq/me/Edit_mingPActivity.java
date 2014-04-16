package com.wq.me;

import java.lang.ref.WeakReference;

import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.model.MingPMdl;
import com.wq.utils.LoggerUtil;
import com.wq.utils.SDCardUtils;

public class Edit_mingPActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.img_mp)
	private ImageView mMingPImg;
	@ViewInject(id = R.id.et_bz)
	private EditText mEtBz;

	private static final String mpDbPath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/.wqdb/mingp.db";

	private String mPathMP;
	private FinalDb fDb;
	WeakReference<Bitmap> bmpSoft = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_mingp);
		initnavigation();
		Bundle bd = getIntent().getExtras();
		if (bd != null) {
			mPathMP = bd.getString("path");
			bmpSoft = new WeakReference<Bitmap>(
					BitmapFactory.decodeFile(mPathMP));

			mMingPImg.setImageBitmap(bmpSoft.get());

		}

		if (!SDCardUtils.ExistSDCard()) {
			showToast("请先插入sd卡");
			return;
		}

		fDb = FinalDb.create(this, mpDbPath);

	}

	private void initnavigation() {
		initNavitation(getResources().getString(R.string.mingp_bz), "保存",
				new EditClickListener() {
					@Override
					public void editClick() {
						String bz = mEtBz.getText().toString();
						if (bz.trim().length() < 1) {
							showToast("您还没有填写备注信息");
							return;
						}
						if (mPathMP == null || mPathMP.trim().length() < 1)
							return;
						if (fDb != null) {
							MingPMdl mod = new MingPMdl();
							mod.setBzStr(bz);
							mod.setPath(mPathMP);
							fDb.save(mod);
							changeView(MingPListActivity.class);
							if (bmpSoft != null && bmpSoft.get() != null
									&& !bmpSoft.get().isRecycled()) {
								bmpSoft.get().recycle();
								mMingPImg = null;
								System.gc();
							}
							finish();

							animOut();
						}
					}

					@Override
					public void backClick() {
						if (bmpSoft != null && bmpSoft.get() != null
								&& !bmpSoft.get().isRecycled()) {
							bmpSoft.get().recycle();
						}
						finish();
						animOut();
					}
				});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (bmpSoft != null && bmpSoft.get() != null
					&& !bmpSoft.get().isRecycled()) {
				mMingPImg = null;
				bmpSoft.get().recycle();
				System.gc();

			}
			finish();
			animOut();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {

	}

	public void onDestory() {
		super.onDestory();

		if (bmpSoft != null && bmpSoft.get() != null
				&& !bmpSoft.get().isRecycled()) {

			bmpSoft.get().recycle();
			mMingPImg = null;
			System.gc();
		}
	}

}
