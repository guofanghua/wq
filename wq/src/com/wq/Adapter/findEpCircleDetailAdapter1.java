package com.wq.Adapter;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.bitmap.display.mycallback;

import com.endure.wq.R;
import com.endure.wq.R.layout;
import com.hp.hpl.sparta.Text;

import com.wq.Interface.IecCircleInterface;
import com.wq.find.findTipListActivity;
import com.wq.model.User;
import com.wq.model.ecCircleModel;
import com.wq.model.ecCircleTopModel;
import com.wq.model.ecParentCircleModel;
import com.wq.model.photoModel;
import com.wq.utils.DensityUtil;
import com.wq.utils.LoggerUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
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

public class findEpCircleDetailAdapter1 extends BaseExpandableListAdapter {
	ArrayList<ecParentCircleModel> list = null;
	Context context;
	LayoutInflater inflater;

	ViewHolderChild viewHolderChild = new ViewHolderChild();
	public FinalBitmap finalBitmap;
	BitmapDisplayConfig displayConfig = null;
	private IecCircleInterface click;
	View top_layout = null;// 顶部试图
	public ecCircleTopModel topModel;
	int width = 0;
	int height = 0;

	public findEpCircleDetailAdapter1(Context context,
			ArrayList<ecParentCircleModel> list, ecCircleTopModel topModel,
			IecCircleInterface click) {
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
		this.click = click;
		this.topModel = topModel;

		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		groupPosition -= 1;
		return list.get(groupPosition).getEclist().get(childPosition);
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
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (groupPosition > 0)
			groupPosition -= 1;
		final ecCircleModel item = list.get(groupPosition).getEclist()
				.get(childPosition);
		if (convertView == null) {
			viewHolderChild = new ViewHolderChild();
			convertView = inflater.inflate(
					R.layout.find_ec_circle_tetail_child_item, null);
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
			convertView.setTag(viewHolderChild);
		} else
			viewHolderChild = (ViewHolderChild) convertView.getTag();
		if (childPosition == 0) {
			viewHolderChild.txt_day.setVisibility(View.VISIBLE);
			viewHolderChild.txt_month.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(item.getTime())) {
				String[] splitTime = item.getTime().split("\\-");
				if (splitTime.length >= 3) {

					viewHolderChild.txt_day
							.setText(splitTime[2].split("\\ ")[0]);
					viewHolderChild.txt_month.setText(splitTime[1] + "月");
				}
			}

		} else {
			viewHolderChild.txt_day.setVisibility(View.INVISIBLE);
			viewHolderChild.txt_month.setVisibility(View.INVISIBLE);
		}

		// 图片
		viewHolderChild.layout_pic.removeAllViews();
		if (item.getType().equals("0")) {
			// 如果为0张图拍呢
			if (item.getImgList().size() == 0) {
				viewHolderChild.layout_pic.setVisibility(View.GONE);
			} else {
				viewHolderChild.layout_pic.setVisibility(View.VISIBLE);
			}
			if (item.getImgList().size() == 1) {
				LayoutParams params = new LayoutParams(width, height);
				params.gravity = Gravity.TOP | Gravity.LEFT;
				ImageView image = new ImageView(context);
				image.setScaleType(ScaleType.CENTER_CROP);
				String url = item.getImgList().get(0).getImageUrl();
				if (item.getImgList().get(0).getFlag() == 1) {
					finalBitmap.displayLocal(image, url, true, displayConfig);
				} else {
					finalBitmap.display(image, url, displayConfig);
				}
				image.setLayoutParams(params);
				viewHolderChild.layout_pic.addView(image);
			}
			// 两张图片
			if (item.getImgList().size() == 2) {

				for (int i = 0; i < item.getImgList().size(); i++) {
					LayoutParams params = new LayoutParams(DensityUtil.dip2px(
							context, 44), height);
					params.leftMargin = 0;
					photoModel p = item.getImgList().get(i);

					ImageView image = new ImageView(context);
					image.setScaleType(ScaleType.CENTER_CROP);
					if (p.getFlag() == 1) {
						finalBitmap.displayLocal(image, p.getImageUrl(), true,
								displayConfig);
					} else {
						finalBitmap.display(image, p.getImageUrl(),
								displayConfig);
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
			if (item.getImgList().size() == 3) {

				for (int i = 0; i < item.getImgList().size(); i++) {
					LayoutParams params = new LayoutParams(DensityUtil.dip2px(
							context, 44), height);
					params.leftMargin = DensityUtil.dip2px(context, 0);
					photoModel p = item.getImgList().get(i);
					ImageView image = new ImageView(context);
					image.setScaleType(ScaleType.CENTER_CROP);

					if (p.getFlag() == 1) {
						finalBitmap.displayLocal(image, p.getImageUrl(), true,
								displayConfig);
					} else {
						finalBitmap.display(image, p.getImageUrl(),
								displayConfig);
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
			if (item.getImgList().size() >= 4) {

				for (int i = 0; i < item.getImgList().size(); i++) {
					LayoutParams params = new LayoutParams(DensityUtil.dip2px(
							context, 44), DensityUtil.dip2px(context, 44));
					photoModel p = item.getImgList().get(i);
					ImageView image = new ImageView(context);
					image.setScaleType(ScaleType.CENTER_CROP);

					if (p.getFlag() == 1) {
						finalBitmap.displayLocal(image, p.getImageUrl(), true,
								displayConfig);
					} else {
						finalBitmap.display(image, p.getImageUrl(),
								displayConfig);
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

			if (item.getImgList().size() >= 1) {
				viewHolderChild.txt_picCount.setText(String.format(context
						.getString(R.string.find_ec_detail_count), item
						.getImgList().size()));
			} else {
				viewHolderChild.txt_picCount.setText("");
			}
		}
		// 分享的产品链接时
		else {
			LayoutParams params = new LayoutParams(width, height);
			params.gravity = Gravity.TOP | Gravity.LEFT;
			params.setMargins(10, 10, 10, 10);

			// 产品
			if (item.getType().equals("1")) {
				ImageView image = new ImageView(context);
				image.setScaleType(ScaleType.CENTER_CROP);

				image.setBackgroundResource(R.drawable.cir_product);
				image.setLayoutParams(params);
				viewHolderChild.layout_pic.addView(image);
			}
			// 动态
			else if (item.getType().equals("2")) {
				ImageView image = new ImageView(context);
				image.setScaleType(ScaleType.CENTER_CROP);

				image.setBackgroundResource(R.drawable.cir_notice);
				image.setLayoutParams(params);
				viewHolderChild.layout_pic.addView(image);

			}
			// 简介
			else if (item.getType().equals("3")) {
				ImageView image = new ImageView(context);
				image.setScaleType(ScaleType.CENTER_CROP);

				image.setBackgroundResource(R.drawable.cir_link);
				image.setLayoutParams(params);
				viewHolderChild.layout_pic.addView(image);
			}

		}

		viewHolderChild.txt_content.setText(item.getCotent());
		viewHolderChild.layout_content
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						click.contentClick(item);
					}
				});
		viewHolderChild.layout_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click.scanPicClick(item);
			}
		});

		// TODO Auto-generated method stub
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition == 0)
			return 0;
		else {
			groupPosition -= 1;
			// TODO Auto-generated method stub
			return list.get(groupPosition).getEclist().size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (groupPosition > 0)
			groupPosition -= 1;
		// TODO Auto-generated method stub
		return list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return list.size() + 1;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		if (groupPosition > 0)
			groupPosition -= 1;
		return groupPosition;
	}

	/**
	 * 返回父试图
	 * */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (groupPosition == 0) {
			if (top_layout == null) {
				top_layout = LayoutInflater.from(context).inflate(
						R.layout.ep_circle_detail_top, null);
			}
			final ImageView imagetop = (ImageView) top_layout
					.findViewById(R.id.img_topbg);
			final ImageView img_logo = (ImageView) top_layout
					.findViewById(R.id.img_logo);

			TextView txt_wqh = (TextView) top_layout.findViewById(R.id.txt_wqh);
		
			txt_wqh.setText(topModel.getWqh());
			if (!TextUtils.isEmpty(topModel.getBigImageUrl())) {
				BitmapDisplayConfig displayConfig1 = new BitmapDisplayConfig();
				displayConfig1.setLoadingBitmap(BitmapFactory.decodeResource(
						context.getResources(), R.drawable.welcome_to_vip));
				displayConfig1.setLoadfailBitmap(BitmapFactory.decodeResource(
						context.getResources(), R.drawable.welcome_to_vip));
				displayConfig1.setCallback(new mycallback() {

					@Override
					public void success(Bitmap bitmap) {
						// TODO Auto-generated method stub
						imagetop.setTag("1");
					}

					@Override
					public void failure() {
						// TODO Auto-generated method stub

					}
				});
				if (!String.valueOf(imagetop.getTag()).equals("1"))
					finalBitmap.display(imagetop, topModel.getBigImageUrl(),
							displayConfig1);
			}

			if (!TextUtils.isEmpty(topModel.getIcon())) {
				BitmapDisplayConfig displayConfig2 = new BitmapDisplayConfig();
				displayConfig2.setLoadingBitmap(BitmapFactory.decodeResource(
						context.getResources(), R.drawable.add_prompt));
				displayConfig2.setLoadfailBitmap(BitmapFactory.decodeResource(
						context.getResources(), R.drawable.add_prompt));
				displayConfig2.setCallback(new mycallback() {

					@Override
					public void success(Bitmap bitmap) {
						// TODO Auto-generated method stub
						img_logo.setTag("1");
					}

					@Override
					public void failure() {
						// TODO Auto-generated method stub

					}
				});
				if (!String.valueOf(img_logo.getTag()).equals("1")) {
					finalBitmap.display(img_logo, topModel.getIcon(),
							displayConfig2);
				}
			}
			top_layout.setTag("1");
			// 点击事件
			img_logo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ecCircleModel ec = new ecCircleModel();
					ec.setEnterpriseId(topModel.getEnterpriseId());
					click.checkUeserDetailClick(ec);
				}
			});
			// 顶图点击事件
			imagetop.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					click.topImageClick(imagetop);
				}
			});

			return top_layout;
		} else {
			TextView txt = new TextView(context);
			txt.setHeight(DensityUtil.dip2px(context, 30));
			txt.setBackgroundColor(Color.WHITE);
			return txt;
		}
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

	}

}
