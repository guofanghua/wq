package com.wq.localAblumScan;

import java.util.List;

import com.endure.wq.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumAdapter extends BaseAdapter {

	private Context mContext;

	private List<PicAlbumMdl> dataList;
	private LayoutInflater mInflater;

	public AlbumAdapter(Context ctx, List<PicAlbumMdl> list) {
		this.mContext = ctx;
		this.dataList = list;
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.ablum_item_album, null);
			holder.mImg = (ImageView) convertView.findViewById(R.id.itemImg);
			holder.mTv = (TextView) convertView.findViewById(R.id.tvCount);
			convertView.setTag(holder);
		} else
			holder = (Holder) convertView.getTag();
		holder.mTv.setText(String.valueOf(dataList.get(position).getLength()));
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext
				.getContentResolver(), dataList.get(position).getAlubPic(),
				Thumbnails.MICRO_KIND, null);
		holder.mImg.setImageBitmap(bitmap);
		return convertView;
	}

	class Holder {
		ImageView mImg;
		TextView mTv;
	}

}
