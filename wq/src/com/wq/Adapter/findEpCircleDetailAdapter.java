package com.wq.Adapter;

import java.util.ArrayList;
import java.util.Date;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import net.endure.framework.bitmap.display.mycallback;

import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.endure.wq.R;

import com.wq.Interface.IecCircleInterface;


import com.wq.model.User;
import com.wq.model.ecCircleModel;
import com.wq.model.leaveMessage;

import com.wq.utils.DensityUtil;


import com.wq.utils.dateUtil;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.text.TextUtils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

public class findEpCircleDetailAdapter extends BaseAdapter {
	private ArrayList<ecCircleModel> mList = null;
	private Context mContext;

	public String bigImageUrl = "";
	public String smallImageUrl = "";
	public String enterpriseId = "";
	public String wqh = "";
	public FinalBitmap finalBitmap;
	BitmapDisplayConfig displayConfig = null;
	int ImageWidth = 0;
	int ImageHeight = 0;
	private ViewHolder viewHolder;

	private IecCircleInterface click;
	private LayoutInflater inflater;
	private int margeSize = 0;

	View top_layout = null;// 顶部试图

	public findEpCircleDetailAdapter(Context context,
			ArrayList<ecCircleModel> list, String enterpriseId,
			IecCircleInterface click) {
		inflater = LayoutInflater.from(context);
		mContext = context;
		mList = list;

		ImageWidth = DensityUtil.dip2px(context, 80);
		ImageHeight = DensityUtil.dip2px(context, 80);
		displayConfig = new BitmapDisplayConfig();
		finalBitmap = FinalBitmap.create(context);
		displayConfig.setLoadingBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		displayConfig.setLoadfailBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.add_prompt));
		this.click = click;
		this.enterpriseId = enterpriseId;
		margeSize = DensityUtil.dip2px(context, 20);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mList.size() >= 1)
			return mList.size() + 1;
		else
			return 1;
	}

	@Override
	public Object getItem(int item) {
		// TODO Auto-generated method stub
		return item;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// 头部顶图
		if (position == 0) {
			if (top_layout == null) {
				top_layout = LayoutInflater.from(mContext).inflate(
						R.layout.ep_circle_top, null);
			}
			final ImageView imagetop = (ImageView) top_layout
					.findViewById(R.id.img_topbg);
			final ImageView img_logo = (ImageView) top_layout
					.findViewById(R.id.img_logo);
			TextView txt_wqh = (TextView) top_layout.findViewById(R.id.txt_wqh);
			txt_wqh.setText(wqh);
			if (!TextUtils.isEmpty(bigImageUrl)) {
				BitmapDisplayConfig displayConfig1 = new BitmapDisplayConfig();
				displayConfig1.setLoadingBitmap(BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.welcome_to_vip));
				displayConfig1.setLoadfailBitmap(BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.welcome_to_vip));
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
					finalBitmap.display(imagetop, bigImageUrl, displayConfig1);
			}

			if (!TextUtils.isEmpty(smallImageUrl)) {
				BitmapDisplayConfig displayConfig2 = new BitmapDisplayConfig();
				displayConfig2.setLoadingBitmap(BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.add_prompt));
				displayConfig2.setLoadfailBitmap(BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.add_prompt));
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

					finalBitmap
							.display(img_logo, smallImageUrl, displayConfig2);
				}
			}
			top_layout.setTag("1");
			// 点击事件
			img_logo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ecCircleModel model = new ecCircleModel();
					model.setEnterpriseId(enterpriseId);
					click.checkUeserDetailClick(model);
				}
			});

			return top_layout;
		} else {

			final ecCircleModel item = this.mList.get(position - 1);

			if (convertView == null || convertView.getTag().equals("1")) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.find_ep_circle_list_item, null);
				viewHolder.img_logo = (ImageView) convertView
						.findViewById(R.id.ep_circle_item_img_logo);
				viewHolder.txt_name = (TextView) convertView
						.findViewById(R.id.ep_circle_item_txt_name);
				viewHolder.txt_content = (TextView) convertView
						.findViewById(R.id.ep_circle_item_txt_content);
				viewHolder.img_container = (FrameLayout) convertView
						.findViewById(R.id.layout_img_container);
				viewHolder.layout_msg = (LinearLayout) convertView
						.findViewById(R.id.item_msg_layout);
				viewHolder.zan_listview = (CustomListView) convertView
						.findViewById(R.id.zan_auto_listview);
				viewHolder.layout_zan = (LinearLayout) convertView
						.findViewById(R.id.layout_zan);
				viewHolder.layout_pl = (LinearLayout) convertView
						.findViewById(R.id.layout_pl);
				viewHolder.txt_time = (TextView) convertView
						.findViewById(R.id.txt_time);
				viewHolder.txt_del = (TextView) convertView
						.findViewById(R.id.txt_del);
				viewHolder.txt_zan = (TextView) convertView
						.findViewById(R.id.txt_zan);
				viewHolder.zan_split = (View) convertView
						.findViewById(R.id.zan_split);
				viewHolder.layout_pl_main = (LinearLayout) convertView
						.findViewById(R.id.layout_pl_main);
				viewHolder.layout_share = (LinearLayout) convertView
						.findViewById(R.id.layout_share);
				viewHolder.img_share = (ImageView) convertView
						.findViewById(R.id.img_share);
				viewHolder.txt_share = (TextView) convertView
						.findViewById(R.id.txt_share);
				viewHolder.txt_type = (TextView) convertView
						.findViewById(R.id.ep_circle_item_txt_type);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.layout_pl_main.setVisibility(View.GONE);
			viewHolder.zan_split.setVisibility(View.GONE);
			// 企业logo
			finalBitmap.display(viewHolder.img_logo, item.getLogoUrl(),
					displayConfig, true);
			viewHolder.txt_name.setText(item.getVqh());

			viewHolder.txt_time.setText(dateUtil.spanNow(mContext,
					item.getTime(), new Date()));
			if (item.getType().equals("0"))// 分享企业链接
			{
				viewHolder.txt_content.setText(item.getCotent());
				viewHolder.txt_content.setVisibility(View.VISIBLE);
				viewHolder.layout_share.setVisibility(View.GONE);
				viewHolder.txt_content.setBackgroundDrawable(null);

				viewHolder.txt_type.setText("");

				// viewHolder.txt_content
				// .setBackgroundResource(R.drawable.msg_bg_click);
			} else {
				viewHolder.txt_content.setVisibility(View.GONE);
				viewHolder.layout_share.setVisibility(View.VISIBLE);
				viewHolder.txt_share.setText(item.getCotent());
				if (item.getType().equals("1")) {
					viewHolder.txt_type.setText(mContext
							.getString(R.string.find_cir_share_pro_title));
					viewHolder.img_share
							.setBackgroundResource(R.drawable.cir_product);
				}
				// 动态
				else if (item.getType().equals("2")) {
					viewHolder.txt_type.setText(mContext
							.getString(R.string.find_cir_share_notice_title));
					viewHolder.img_share
							.setBackgroundResource(R.drawable.cir_notice);
				}
				// 简介
				else if (item.getType().equals("3")) {
					viewHolder.txt_type.setText(mContext
							.getString(R.string.find_cir_share_link_title));
					viewHolder.img_share
							.setBackgroundResource(R.drawable.cir_link);
				}

			}
			viewHolder.layout_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					click.contentClick(item);
					// TODO Auto-generated method stub

				}
			});
			if (User.id.equals(enterpriseId)) {
				viewHolder.txt_del.setVisibility(View.VISIBLE);

			} else
				viewHolder.txt_del.setVisibility(View.INVISIBLE);
			// 删除按钮
			viewHolder.txt_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					click.btnDelClick(position - 1, item.getId());

				}
			});
			// 判断此条记录是否已经赞过
			String wqh = "";
			if (item.getZanList().size() > 0) {
				for (int i = 0; i < item.getZanList().size(); i++) {
					if (item.getZanList().get(i).getEnterPriseId()
							.equals(User.id)) {
						wqh = item.getZanList().get(i).getVqh();
						break;
					}
				}
				if (!TextUtils.isEmpty(wqh)) {
					viewHolder.txt_zan.setText(mContext
							.getString(R.string.find_string_ec_yzan));
				} else {
					viewHolder.txt_zan.setText(mContext
							.getString(R.string.find_string_ec_zan));
				}
			} else {
				viewHolder.txt_zan.setText(mContext
						.getString(R.string.find_string_ec_zan));
			}
			// 赞的条数
			if (item.getZanList().size() > 0) {
				viewHolder.zan_listview.setVisibility(View.VISIBLE);
				zanAutoLineAdapter adapter = new zanAutoLineAdapter(mContext,
						item.getZanList());
				viewHolder.zan_listview.setAdapter(adapter);
				viewHolder.layout_pl_main.setVisibility(View.VISIBLE);
				viewHolder.layout_pl_main.setVisibility(View.VISIBLE);
				// 赞的点击名字事件
				viewHolder.zan_listview
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View v, int position, long id) {
								// TODO Auto-generated method stub
								if (position <= 0)
									return;
								leaveMessage msg = item.getZanList().get(
										position - 1);
								ecCircleModel model = new ecCircleModel();
								model.setEnterpriseId(msg.getEnterPriseId());
								model.setVqh(msg.getVqh());
								click.checkUserClick(model);
							}
						});
			} else {

				viewHolder.zan_listview.setVisibility(View.GONE);
			}

			// 图片
			int num = item.getImgList().size();
			viewHolder.img_container.removeAllViews();
			viewHolder.img_container.setVisibility(View.VISIBLE);
			displayConfig.setIsRoundCore(false);
			if (num == 1) {
				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				final ImageView imge = new ImageView(mContext);
				imge.setScaleType(ScaleType.CENTER_CROP);
				imge.setLayoutParams(params);
				imge.setAdjustViewBounds(true);
				imge.setMaxWidth(DensityUtil.intScreenWidth(mContext) * 3 / 7);
				displayConfig.setCallback(new mycallback() {
					@Override
					public void success(Bitmap bitmap) {
						// TODO Auto-generated method stub
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						if (width < height)
							imge.setMaxWidth(DensityUtil
									.intScreenWidth(mContext) * 3 / 7);
					}

					@Override
					public void failure() {
						// TODO Auto-generated method stub
					}
				});
				if (item.getImgList().get(0).getFlag() == 1) {
					finalBitmap.displayLocal(imge, item.getImgList().get(0)
							.getImageUrl(), false, displayConfig);
				} else {
					finalBitmap.display(imge, item.getImgList().get(0)
							.getImageUrl(), displayConfig);
				}
				viewHolder.img_container.addView(imge, params);
			}

			else if (num != 4 && num > 1) {
				for (int i = 0; i < num; i++) {
					LayoutParams params = new LayoutParams(ImageWidth,
							ImageHeight);

					params.leftMargin = (i % 3) * (ImageWidth + 2);
					params.topMargin = (i / 3) * (ImageHeight + 2);
					params.gravity = Gravity.TOP | Gravity.LEFT; // 兼容4.0一下版本
					ImageView imge = new ImageView(mContext);
					imge.setScaleType(ScaleType.CENTER_CROP);
					imge.setLayoutParams(params);
					viewHolder.img_container.addView(imge);
					if (item.getImgList().get(0).getFlag() == 1) {
						finalBitmap.displayLocal(imge, item.getImgList().get(i)
								.getImageUrl(), false);
					} else {
						finalBitmap.display(imge, item.getImgList().get(i)
								.getImageUrl(), displayConfig);
					}
				}

			} else if (num == 4) {
				for (int i = 0; i < num; i++) {
					LayoutParams params = new LayoutParams(ImageWidth,
							ImageHeight);
					params.leftMargin = (i % 2) * (ImageWidth + 2);
					params.topMargin = (i / 2) * (ImageHeight + 2);
					params.gravity = Gravity.TOP | Gravity.LEFT; // 兼容4.0以下版本
					ImageView imge = new ImageView(mContext);
					imge.setScaleType(ScaleType.CENTER_CROP);
					viewHolder.img_container.addView(imge, params);
					if (item.getImgList().get(0).getFlag() == 1) {
						finalBitmap.displayLocal(imge, item.getImgList().get(i)
								.getImageUrl(), false);
					} else {
						finalBitmap.display(imge, item.getImgList().get(i)
								.getImageUrl(), displayConfig);
					}
				}
			} else
				viewHolder.img_container.setVisibility(View.GONE);
			// 留言
			if (item.getMsgList().size() > 0) {
				if (item.getZanList().size() > 0)
					viewHolder.zan_split.setVisibility(View.VISIBLE);
				else
					viewHolder.zan_split.setVisibility(View.GONE);
				viewHolder.layout_msg.setVisibility(View.VISIBLE);
				viewHolder.layout_msg.removeAllViews();

				for (int i = 0; i < item.getMsgList().size(); i++) {

					final leaveMessage msg = item.getMsgList().get(i);

					View v = inflater.inflate(
							R.layout.find_ec_circle_leave_msg_item, null);
					TextView txt_name = (TextView) v
							.findViewById(R.id.txt_leave_name);
					RelativeLayout layout_main = (RelativeLayout) v
							.findViewById(R.id.layout_main);
					final LinearLayout layout_main1 = (LinearLayout) v
							.findViewById(R.id.layout_main1);
					// ImageView img_logo = (ImageView) v
					// .findViewById(R.id.img_logo);
					// TextView txt_time = (TextView) v
					// .findViewById(R.id.txt_time);
					final TextView txt_again = (TextView) v
							.findViewById(R.id.txt_again);
					TextView txt_content = (TextView) v
							.findViewById(R.id.txt_content);
					txt_name.setText(msg.getVqh() + ":");
					// txt_time.setText(msg.getTime());
					txt_content.setText(msg.getContent());
					// finalBitmap.display(img_logo, msg.getLogoUrl(),
					// displayConfig);
					txt_again.setTag(i);
					if (msg.isSend()) {
						txt_again.setVisibility(View.GONE);
					} else {
						txt_again.setVisibility(View.VISIBLE);
						// 留言提交失败
						txt_again.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								click.commitClick(Integer.parseInt(String
										.valueOf(txt_again.getTag())), msg);

							}
						});
					}
					// 布局和点击事件
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					params.topMargin = DensityUtil.dip2px(mContext, 3);
					int level = Integer.parseInt(msg.getLevel());
					if (level > 1) {
						params.leftMargin = margeSize * 1;
					} else
						params.leftMargin = DensityUtil.dip2px(mContext, 6);
					layout_main.setLayoutParams(params);
					// 针对此人留言

					layout_main1.setTag(position - 1);
					layout_main1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							leaveMessage msg1 = new leaveMessage();
							msg1.setType("0");
							msg1.setSuperComId(msg.getComId());
							msg1.setLevel((Integer.parseInt(msg.getLevel()) + 1)
									+ "");

							msg1.setSuperName(msg.getVqh());

							click.btnLeaveClick(Integer.parseInt(String
									.valueOf(layout_main1.getTag())), msg1);

							// TODO Auto-generated method stub

						}
					});
					// 点击留言内容，进行恢复留言
					txt_name.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							ecCircleModel model = new ecCircleModel();
							model.setEnterpriseId(msg.getEnterPriseId());
							model.setVqh(msg.getVqh());
							click.checkUserClick(model);
						}
					});

					viewHolder.layout_msg.addView(v);

				}

				viewHolder.layout_pl_main.setVisibility(View.VISIBLE);
			} else {
				viewHolder.layout_msg.setVisibility(View.GONE);
				// viewHolder.msg_list.setVisibility(View.GONE);

			}
			// 赞的点击事件
			viewHolder.layout_zan.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					leaveMessage msg = new leaveMessage();
					msg.setLevel("1");
					msg.setSuperComId(item.getId());
					msg.setType("1");
					msg.setEnterPriseId(User.id);
					msg.setVqh(User.wqh);
					String wqh = "";
					String comId = "";
					if (item.getZanList().size() > 0) {
						for (int i = 0; i < item.getZanList().size(); i++) {
							if (item.getZanList().get(i).getEnterPriseId()
									.equals(User.id)) {
								wqh = item.getZanList().get(i).getVqh();
								comId = item.getZanList().get(i).getComId();
								break;
							}
						}
					}
					msg.setComId(comId);
					if (TextUtils.isEmpty(wqh)) {
						click.commitClick(position - 1, msg);
					} else {
						click.delMsgClick(position - 1, msg);
					}

				}
			});
			// 评论的点击事件
			viewHolder.layout_pl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					leaveMessage msg = new leaveMessage();
					msg.setLevel("1");
					msg.setSuperComId(mList.get(position - 1).getId());
					msg.setType("0");
					msg.setEnterPriseId(User.id);
					msg.setVqh(User.wqh);
					msg.setSuperName(mList.get(position - 1).getVqh());
					// TODO Auto-generated method stub
					click.btnLeaveClick(position - 1, msg);
				}
			});
			// 留言人名字的点击事件
			viewHolder.txt_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					click.checkUserClick(item);
				}
			});
			// 图片浏览趋于点击事件
			viewHolder.img_container.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					click.scanPicClick(item);
					// TODO Auto-generated method stub

				}
			});
			// 内容详情查看
			viewHolder.txt_content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					click.contentClick(item);
				}
			});
			return convertView;
		}
		// TODO Auto-generated method stub

	}

	private class ViewHolder {
		ImageView img_logo;
		TextView txt_name;
		TextView txt_content;
		LinearLayout layout_pl_main;
		LinearLayout layout_zan;
		LinearLayout layout_pl;

		FrameLayout img_container;
		CustomListView zan_listview;
		// LinearLayout msg_container;
		// InScrolllistView msg_list;
		LinearLayout layout_msg;
		TextView txt_time;
		TextView txt_del;
		TextView txt_zan;
		View zan_split;
		// share
		LinearLayout layout_share;
		ImageView img_share;
		TextView txt_share;
		TextView txt_type;
	}

}
