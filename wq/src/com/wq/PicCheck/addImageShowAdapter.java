package com.wq.PicCheck;

import java.io.IOException;

import com.endure.wq.R;
import com.wq.PicCheck.Bimp;
import com.wq.model.ablum;
import com.wq.model.myObject;
import com.wq.model.notice;
import com.wq.model.photoModel;
import com.wq.model.product;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class addImageShowAdapter extends BaseAdapter {

	private LayoutInflater inflater; // 视图容器
	private int selectedPosition = -1;// 选中的位置
	private boolean shape;
	private Context context;
	// private myObject object;
	private ablum myablum;
	private product myProduct;
	private notice myNotice;
	private Handler myhandler;
	int flag = 0;// 1 表示动态 2表示产品 3 表示相册

	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public addImageShowAdapter(Context context, myObject object,
			Handler myhandler) {
		if (object instanceof ablum) {
			this.myablum = (ablum) object;
			flag = 3;
		}

		this.context = context;
		inflater = LayoutInflater.from(context);
		this.myhandler = myhandler;
	}

	public void update() {
		loading();
	}

	public int getCount() {
		if (Bimp.picbmpList.size() <= PicCheckMainActivity.maxSize - 1)
			return (Bimp.picbmpList.size() + 1);
		else {
			return Bimp.picbmpList.size();
		}
	}

	public Object getItem(int arg0) {

		return null;
	}

	public long getItemId(int arg0) {

		return 0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_published_grida,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (Bimp.picbmpList.size() < PicCheckMainActivity.maxSize) {
			if (position < Bimp.picbmpList.size()) {
				holder.image.setImageBitmap(Bimp.picbmpList.get(position)
						.getBitmap());
				holder.image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Message msgMessage = myhandler.obtainMessage();
						msgMessage.what = 1;
						msgMessage.arg1 = position;
						msgMessage.sendToTarget();
					}
				});
			} else {
				holder.image.setBackgroundResource(R.drawable.bt_add_pic);
				holder.image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Message msgMessage = myhandler.obtainMessage();
						msgMessage.what = 2;
						msgMessage.sendToTarget();
						// TODO Auto-generated method stub

					}
				});
			}
		} else {
			holder.image.setImageBitmap(Bimp.picbmpList.get(position)
					.getBitmap());
			holder.image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Message msgMessage = myhandler.obtainMessage();
					msgMessage.what = 1;
					msgMessage.arg1 = position;
					msgMessage.sendToTarget();
				}
			});
		}
		// if (Bimp.bmp.size() == PicCheckMainActivity.maxSize) {
		//
		// } else if (position == Bimp.bmp.size()) {
		//
		// } else {
		// holder.image.setImageBitmap(Bimp.bmp.get(position));
		// }
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (flag == 3) {
					if (TextUtils.isEmpty(msg.obj.toString()))
						return;
					photoModel item = new photoModel();
					item.setFlag(1);
					item.setImageUrl(msg.obj.toString());
					item.setShareModel(myablum);
					item.setShareType(photoModel.ABLUM_SHARE_FLAG);
					myablum.getList().add(item);
				}
				notifyDataSetChanged();
				break;
			case 2:
				// Message msg=myhandler.ob
				Message msgMessage = myhandler.obtainMessage();
				msgMessage.what = 3;
				msgMessage.sendToTarget();
				break;
			}

			super.handleMessage(msg);
		}
	};

	public void loading() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Bimp.max == Bimp.picbmpList.size()) {
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);
						break;
					} else {
						try {
							String path = Bimp.picbmpList.get(Bimp.max)
									.getFilePath();
							picBitmap itemBitmap = new picBitmap();
							itemBitmap.setFilePath(path);
							itemBitmap.setBitmap(Bimp.revitionImageSize(path));
							for (int i = 0; i < Bimp.picbmpList.size(); i++) {
								if (Bimp.picbmpList.get(i).getFilePath()
										.equals(path)) {
									Bimp.picbmpList.set(i, itemBitmap);
									break;
								}
							}
							String newStr = path.substring(
									path.lastIndexOf("/") + 1,
									path.lastIndexOf("."));
							String savepath = FileUtils.saveBitmap(
									itemBitmap.getBitmap(), "" + newStr);
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							message.obj = savepath;
							handler.sendMessage(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}
}
