package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;

import com.wq.model.photoModel;
import com.wq.model.productList;
import com.wq.model.product;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

import android.content.Context;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class proListAdapter extends BaseExpandableListAdapter {
	ArrayList<productList> list = null;
	Context context;
	LayoutInflater inflater;

	ViewHolderChild viewHolderChild = new ViewHolderChild();
	public FinalBitmap finalBitmap;
	BitmapDisplayConfig displayConfig = null;

	int width = 0;
	int height = 0;
	Handler handler;

	public proListAdapter(Context context, ArrayList<productList> list,
			Handler imgHandler) {
		width = DensityUtil.dip2px(context, 90);
		height = DensityUtil.dip2px(context, 90);
		this.context = context;
		this.list = list;

		inflater = LayoutInflater.from(context);
		displayConfig = new BitmapDisplayConfig();
		finalBitmap = FinalBitmap.create(context);
		displayConfig.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setBitmapHeight(height);
		displayConfig.setBitmapWidth(width);
		this.handler = imgHandler;

		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub

		return list.get(groupPosition).getProList().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	/**
	 * 返回子试图
	 * */
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final product item = list.get(groupPosition).getProList()
				.get(childPosition);
		if (convertView == null) {
			viewHolderChild = new ViewHolderChild();
			convertView = inflater.inflate(R.layout.product_list_item, null);
			viewHolderChild.layout_content = (LinearLayout) convertView
					.findViewById(R.id.ec_child_item_layout_content);
			viewHolderChild.layout_pic = (FrameLayout) convertView
					.findViewById(R.id.ec_child_item_layout_img);
			viewHolderChild.txt_content = (TextView) convertView
					.findViewById(R.id.ec_child_item_txt_content);
			viewHolderChild.txt_day = (TextView) convertView
					.findViewById(R.id.ec_child_item_txt_day);
			viewHolderChild.txt_month = (TextView) convertView
					.findViewById(R.id.ec_child_item_txt_month);
			viewHolderChild.txt_picCount = (TextView) convertView
					.findViewById(R.id.ec_child_itme_txt_pic_count);
			viewHolderChild.txt_title = (TextView) convertView
					.findViewById(R.id.ec_child_item_txt_title);
			viewHolderChild.layout_send = (LinearLayout) convertView
					.findViewById(R.id.layout_re_send_main);
			convertView.setTag(viewHolderChild);
		} else
			viewHolderChild = (ViewHolderChild) convertView.getTag();
		if (childPosition == 0) {
			viewHolderChild.txt_day.setVisibility(View.VISIBLE);
			viewHolderChild.txt_month.setVisibility(View.GONE);
			viewHolderChild.txt_day.setText(item.getCateName());

		} else {
			viewHolderChild.txt_day.setVisibility(View.INVISIBLE);
			viewHolderChild.txt_month.setVisibility(View.INVISIBLE);
		}
		viewHolderChild.txt_day.setText(item.getCateName());
		viewHolderChild.txt_content.setText(item.getIntro());
		viewHolderChild.txt_title.setText(item.getTitle());
		// 图片
		viewHolderChild.layout_pic.removeAllViews();

		// 如果为0张图拍呢
		if (item.getPicList().size() == 0) {
			viewHolderChild.layout_pic.setVisibility(View.GONE);
		} else {
			viewHolderChild.layout_pic.setVisibility(View.VISIBLE);
		}
		if (item.getPicList().size() == 1) {
			LayoutParams params = new LayoutParams(width, height);
			params.gravity = Gravity.TOP | Gravity.LEFT;
			ImageView image = new ImageView(context);
			image.setScaleType(ScaleType.CENTER_CROP);
			String url = item.getPicList().get(0).getImageUrl();
			if (item.getPicList().get(0).getFlag() == 1) {
				finalBitmap.displayLocal(image, url, true, displayConfig);
			} else {
				finalBitmap.display(image, url, displayConfig);
			}
			image.setLayoutParams(params);
			viewHolderChild.layout_pic.addView(image);
		}
		// 两张图片
		if (item.getPicList().size() == 2) {

			for (int i = 0; i < item.getPicList().size(); i++) {
				LayoutParams params = new LayoutParams(DensityUtil.dip2px(
						context, 44), height);
				params.leftMargin = 0;
				photoModel p = item.getPicList().get(i);

				ImageView image = new ImageView(context);
				image.setScaleType(ScaleType.CENTER_CROP);
				if (p.getFlag() == 1) {
					finalBitmap.displayLocal(image, p.getImageUrl(), true,
							displayConfig);
				} else {
					finalBitmap.display(image, p.getImageUrl(), displayConfig);
				}
				if (i == 0) {
					params.leftMargin = 0;
				}
				if (i == 1) {
					params.leftMargin = DensityUtil.dip2px(context, 46);
				}
				image.setLayoutParams(params);

				viewHolderChild.layout_pic.addView(image);
			}

		}
		// 三张图片
		if (item.getPicList().size() == 3) {

			for (int i = 0; i < item.getPicList().size(); i++) {
				LayoutParams params = new LayoutParams(DensityUtil.dip2px(
						context, 44), height);
				params.leftMargin = DensityUtil.dip2px(context, 0);
				photoModel p = item.getPicList().get(i);
				ImageView image = new ImageView(context);
				image.setScaleType(ScaleType.CENTER_CROP);

				if (p.getFlag() == 1) {
					finalBitmap.displayLocal(image, p.getImageUrl(), true,
							displayConfig);
				} else {
					finalBitmap.display(image, p.getImageUrl(), displayConfig);
				}

				if (i == 0) {
					params.width = DensityUtil.dip2px(context, 44);
					params.height = height;
				} else if (i == 1) {
					params.leftMargin = DensityUtil.dip2px(context, 46);
					params.width = DensityUtil.dip2px(context, 44);
					params.height = DensityUtil.dip2px(context, 44);
				} else if (i == 2) {
					params.leftMargin = DensityUtil.dip2px(context, 46);
					params.topMargin = DensityUtil.dip2px(context, 46);
					params.width = DensityUtil.dip2px(context, 44);
					params.height = DensityUtil.dip2px(context, 44);
				}
				params.gravity = Gravity.TOP | Gravity.LEFT;
				image.setLayoutParams(params);
				viewHolderChild.layout_pic.addView(image);
			}

		}
		// 四张图片以上
		if (item.getPicList().size() >= 4) {

			for (int i = 0; i < item.getPicList().size(); i++) {
				LayoutParams params = new LayoutParams(DensityUtil.dip2px(
						context, 44), DensityUtil.dip2px(context, 44));
				photoModel p = item.getPicList().get(i);
				ImageView image = new ImageView(context);
				image.setScaleType(ScaleType.CENTER_CROP);

				if (p.getFlag() == 1) {
					finalBitmap.displayLocal(image, p.getImageUrl(), true,
							displayConfig);
				} else {
					finalBitmap.display(image, p.getImageUrl(), displayConfig);
				}

				if (i == 1) {
					params.leftMargin = DensityUtil.dip2px(context, 46);

				} else if (i == 2) {
					params.leftMargin = DensityUtil.dip2px(context, 0);
					params.topMargin = DensityUtil.dip2px(context, 46);

				} else if (i == 3) {
					params.leftMargin = DensityUtil.dip2px(context, 46);
					params.topMargin = DensityUtil.dip2px(context, 46);
				}
				params.gravity = Gravity.TOP | Gravity.LEFT;
				image.setLayoutParams(params);
				if (i < 4)
					viewHolderChild.layout_pic.addView(image);
			}
		}
		if (item.getPicList().size() >= 1) {

			viewHolderChild.txt_picCount.setText(String.format(context
					.getString(R.string.find_ec_detail_count), item
					.getPicList().size()));
		} else {
			viewHolderChild.txt_picCount.setText("");
		}

		viewHolderChild.layout_send.setVisibility(View.GONE);

		// viewHolderChild.layout_content
		// .setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// click.contentClick(item);
		// }
		// });
		// viewHolderChild.layout_pic.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Message msg = handler.obtainMessage();
		// msg.what = 1;
		// msg.arg1 = groupPosition;
		// msg.arg2 = childPosition;
		// msg.sendToTarget();
		//
		// // TODO Auto-generated method stub
		// // click.scanPicClick(item);
		// }
		// });

		// TODO Auto-generated method stub
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		// TODO Auto-generated method stub
		return list.get(groupPosition).getProList().size();

	}

	@Override
	public Object getGroup(int groupPosition) {

		// TODO Auto-generated method stub
		return list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub

		return groupPosition;
	}

	/**
	 * 返回父试图
	 * */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		TextView txt = new TextView(context);
		if (groupPosition != 0)
			txt.setHeight(DensityUtil.dip2px(context, 30));
		else
			txt.setHeight(DensityUtil.dip2px(context, 10));
		txt.setBackgroundColor(Color.WHITE);
		return txt;

	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	private class ViewHolderChild {
		TextView txt_day;
		TextView txt_month;
		LinearLayout layout_content;
		FrameLayout layout_pic;
		TextView txt_content;
		TextView txt_picCount;
		TextView txt_title;
		LinearLayout layout_send;

	}

}