package com.wq.Adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.endure.framework.FinalDb;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.model.MingPMdl;
import com.wq.model.photoModel;
import com.wq.utils.SDCardUtils;

public class MingPAdapter extends BaseAdapter {
	private static final String mpDbPath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/.wqdb/mingp.db";
	private LayoutInflater mInflater;
	private List<MingPMdl> dataList;
	private int deltePos = -1;
	private BaseActivity activity;
	private FinalDb fDb;

	public MingPAdapter(BaseActivity ctx, List<MingPMdl> list) {
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		activity = ctx;
		if (SDCardUtils.ExistSDCard()) {
			fDb = FinalDb.create(ctx, mpDbPath);
		}
		this.dataList = list;
		if (this.dataList == null) {
			dataList = new ArrayList<MingPMdl>();
		}
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void showDelteItem(int pos) {
		this.deltePos = pos;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_mingp, null);
			holder.tv = (TextView) convertView.findViewById(R.id.bzTv);
			holder.deleteImg = (ImageView) convertView
					.findViewById(R.id.deleteBtn);
			holder.showImg = (ImageView) convertView
					.findViewById(R.id.btn_show);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.deleteImg.setVisibility(View.GONE);
		if (position == deltePos) {
			holder.deleteImg.setVisibility(View.VISIBLE);
		}
		holder.deleteImg.setTag(position);
		holder.showImg.setTag(position);
		holder.deleteImg.setOnClickListener(delteListener);
		holder.showImg.setOnClickListener(showListener);
		holder.tv.setText(dataList.get(position).getBzStr());
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		ImageView deleteImg;
		ImageView showImg;
	}

	private OnClickListener delteListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			deltePos = -1;
			int pos = Integer.parseInt(v.getTag().toString());
			MingPMdl item = dataList.remove(pos);
			try {
				if (fDb != null) {
					fDb.delete(item);
				}
				File file = new File(item.getPath());
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			notifyDataSetChanged();
		}
	};

	private OnClickListener showListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int pos = Integer.parseInt(v.getTag().toString());
			ArrayList<photoModel> list = new ArrayList<photoModel>();
			MingPMdl item = dataList.get(pos);
			photoModel mode = new photoModel();
			mode.setImageUrl(item.getPath());
			mode.setExtStr(item.getBzStr());
			mode.setFlag(1);
			list.add(mode);
			activity.scanPhoto(list, 0, false, false, null);
		}
	};
}
