package com.wq.me;

import net.endure.framework.FinalBitmap;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;

public class templateImageShowActivity extends BaseActivity {
	@ViewInject(id = R.id.img_show)
	ImageView img;
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp_img_show_detail_activity);
		BaseApplication.getInstance().addActivity(this);
		config = new BitmapDisplayConfig();
//		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
//				R.drawable.add_prompt));
		finalBitmap = FinalBitmap.create(this);
		String url = this.getIntent().getStringExtra("imgPath");
		if (!TextUtils.isEmpty(url)) {
			finalBitmap.display(img, url, config);
		}
		
		img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, R.anim.ablum_transition_out);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(0, R.anim.ablum_transition_out);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
