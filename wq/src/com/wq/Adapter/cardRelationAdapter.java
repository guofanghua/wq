package com.wq.Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.Interface.OnIconClickListener;
import com.wq.model.cardRelationList;
import com.wq.model.cardRelationModel;
import com.wq.utils.DensityUtil;
import com.wq.utils.dateUtil;

public class cardRelationAdapter extends BaseExpandableListAdapter {

	ArrayList<cardRelationList> list = null;
	Context context;
	LayoutInflater inflater;

	public FinalBitmap finalBitmap;
	BitmapDisplayConfig displayConfig = null;

	private OnIconClickListener mListener;

	int width = 0;
	int height = 0;
	Handler handler;
	SimpleDateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat formattmp1 = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat formattmp2 = new SimpleDateFormat("HH:mm");

	public cardRelationAdapter(Context context,
			ArrayList<cardRelationList> list, OnIconClickListener listener) {
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
		this.mListener = listener;
	}

	public void refresh(ArrayList<cardRelationList> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).getList().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * 返回子试图
	 * */
	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final cardRelationModel item = list.get(groupPosition).getList()
				.get(childPosition);
		ViewHolderChild viewHolderChild;
		if (convertView == null) {
			viewHolderChild = new ViewHolderChild();
			convertView = inflater.inflate(R.layout.card_relation_list_item,
					null);
			viewHolderChild.txt_time = (TextView) convertView
					.findViewById(R.id.txt_time);
			viewHolderChild.txt_content = (TextView) convertView
					.findViewById(R.id.txt_content);
			viewHolderChild.imag_logo = (ImageView) convertView
					.findViewById(R.id.img_logo);
			viewHolderChild.txt_NumCount = (TextView) convertView
					.findViewById(R.id.numCount);
			viewHolderChild.viewEndTip = (View) convertView
					.findViewById(R.id.endTip);
			convertView.setTag(viewHolderChild);
		}
		viewHolderChild = (ViewHolderChild) convertView.getTag();

		viewHolderChild.txt_time.setText(dateUtil.formatDate(
				item.getStartTime(), formattmp2));
		viewHolderChild.txt_content.setText(item.getContent());
		finalBitmap.display(viewHolderChild.imag_logo, item.getPartlist()
				.get(0).getIcon(), displayConfig, true);
		if (item.Childsize() > 1) {
			viewHolderChild.txt_NumCount.setText(String.valueOf(item
					.Childsize()));
			viewHolderChild.txt_NumCount.setVisibility(View.VISIBLE);
		} else {
			viewHolderChild.txt_NumCount.setVisibility(View.GONE);
		}
		viewHolderChild.imag_logo.setTag(item);
		viewHolderChild.imag_logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					cardRelationModel postion = (cardRelationModel) v.getTag();
					int[] location = new int[2];
					v.getLocationOnScreen(location);
					int x = location[0];
					int y = location[1];
					mListener.showItemGroup(postion.getPartlist(), x, y);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		if (childPosition < list.get(groupPosition).getList().size()-1) {
			viewHolderChild.viewEndTip.setVisibility(View.GONE);
		} else {
			viewHolderChild.viewEndTip.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return list.get(groupPosition).getList().size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return list.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/**
	 * 返回父试图
	 * */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.card_parent_item, null);
		TextView txt_time = (TextView) convertView
				.findViewById(R.id.txt_parent_time);
		txt_time.setText(list.get(groupPosition).getTime());
		return convertView;

	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private class ViewHolderChild {
		TextView txt_time;
		ImageView imag_logo;
		TextView txt_content;
		TextView txt_NumCount;
		View viewEndTip;
	}

}
