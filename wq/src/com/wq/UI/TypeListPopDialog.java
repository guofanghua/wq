package com.wq.UI;

import java.util.ArrayList;

import com.endure.wq.R;
import com.wq.utils.CommonUtil;
import com.wq.utils.DensityUtil;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author Administrator
 * 
 */
public class TypeListPopDialog extends PopupWindow {
	private Context mContext;
	private View mRoot;
	private LinearLayout mLayout;

	public TypeListPopDialog(Context context) {
		super(context);
		mContext = context;
		mRoot = LayoutInflater.from(context).inflate(R.layout.popup_dialog,
				null);
		mLayout = (LinearLayout) mRoot.findViewById(R.id.layout_betTypeList);

	}

	// 我的名片选择和首页pop样式
	public TypeListPopDialog(Context context, int mRootId) {
		super(context);
		mContext = context;
		mRoot = LayoutInflater.from(context).inflate(mRootId, null);
		mLayout = (LinearLayout) mRoot.findViewById(R.id.layout_betTypeList);
	}

	public void setContentView() {
		super.setContentView(mRoot);
		this.setWindowLayoutMode(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
	}

	public void setCardContentView() {
		super.setContentView(mRoot);

		this.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	/** 图片和文字的下拉 */
	public void loadContentView(String[] types, int[] ImageIdArr,
			final Handler handler) {
		if (null == types && types.length == 0)
			return;
		for (int i = 0; i < types.length; i++) {
			String[] ss = types[i].split("\\|");
			if (ss.length != 2)
				continue;
			LinearLayout v = (LinearLayout) LayoutInflater.from(mContext)
					.inflate(R.layout.name_card_popup_item, null);
			ImageView img_logo = (ImageView) v.findViewById(R.id.img_logo);
			TextView txt_title = (TextView) v.findViewById(R.id.txt_title);
			View spltline = (View) v.findViewById(R.id.view_split);
			v.setTag(ss[0]);
			if (ImageIdArr == null || ImageIdArr.length == 0) {
				img_logo.setVisibility(View.GONE);
			} else if (ImageIdArr.length >= i)
				img_logo.setBackgroundResource(ImageIdArr[i]);

			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = handler.obtainMessage();
					msg.what = Integer.parseInt(v.getTag().toString());
					msg.sendToTarget();
				}
			});
			if (i < types.length) {
				txt_title.setText(ss[1]);
			}
			if (i == types.length - 1) {
				spltline.setVisibility(View.GONE);
			}

			mLayout.addView(v);
		}
	}

	/** 我的收藏 */
	public void loadCollectView(String[] types, final Handler handler) {
		if (null == types && types.length == 0)
			return;
		for (int i = 0; i < types.length; i++) {
			LinearLayout v = (LinearLayout) LayoutInflater.from(mContext)
					.inflate(R.layout.name_card_popup_item, null);
			ImageView img_logo = (ImageView) v.findViewById(R.id.img_logo);
			TextView txt_title = (TextView) v.findViewById(R.id.txt_title);
			View spltline = (View) v.findViewById(R.id.view_split);
			v.setTag(i);
			// 文字
			if (i == 0) {
				img_logo.setBackgroundResource(R.drawable.coloect_wz);
			}
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = handler.obtainMessage();
					msg.what = Integer.parseInt(v.getTag().toString());
					msg.sendToTarget();
				}
			});
			if (i < types.length) {

				String[] ss = types[i].split("\\|");
				if (ss.length != 2)
					continue;
				v.setTag(ss[0]);
				txt_title.setText(ss[1]);
			}
			if (i == types.length - 1) {
				spltline.setVisibility(View.GONE);
			}

			mLayout.addView(v);
		}
	}

	/**
	 * 访问数据统计
	 * */
	public void loadContentView(String[] types, int resId, int flag,
			View.OnClickListener click) {
		if (null == types && types.length == 0)
			return;
		for (int i = 0; i < types.length; i++) {
			FrameLayout v = (FrameLayout) LayoutInflater.from(mContext)
					.inflate(R.layout.popup_item, null);
			ImageView img_logo = (ImageView) v.findViewById(R.id.img_logo);
			TextView txt_title = (TextView) v.findViewById(R.id.txt_title);
			ImageView img_select = (ImageView) v.findViewById(R.id.img_select);
			View spltline = (View) v.findViewById(R.id.view_split);
			// 访问空间量
			if (i == 0) {
				img_logo.setBackgroundResource(R.drawable.visit_kj_press);
			} else if (i == 1) {
				img_logo.setBackgroundResource(R.drawable.visit_notice_press);
			} else if (i == 2) {
				img_logo.setBackgroundResource(R.drawable.visit_product_press);
			}
			if (i == flag)
				img_select.setVisibility(View.VISIBLE);
			else
				img_select.setVisibility(View.GONE);
			v.setOnClickListener(click);
			if (i < types.length) {

				String[] ss = types[i].split("\\|");
				if (ss.length != 2)
					continue;
				v.setTag(ss[0]);
				txt_title.setText(ss[1]);
			}
			if (i == types.length - 1) {
				spltline.setVisibility(View.GONE);
			}

			mLayout.addView(v);
		}
	}

}
