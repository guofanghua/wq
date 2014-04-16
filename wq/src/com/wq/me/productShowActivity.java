package com.wq.me;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.endure.framework.FinalBitmap;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import net.endure.framework.annotation.view.ViewInject;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.ScrollView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup.LayoutParams;

import com.endure.wq.R;
import com.wq.BaseActivity;
import com.wq.BaseApplication;

import com.wq.Adapter.addImagHorListviewAdapter;
import com.wq.Adapter.imageSwitchAdapter;
import com.wq.Adapter.proAttrAdapter;
import com.wq.Adapter.viewPageAdapter;

import com.wq.UI.InScrolllistView;

import com.wq.UI.horizontalListview.HorizontalVariableListView;

import com.wq.me.showNoticeDetailActivity.myOnPageChangeListener;
import com.wq.model.User;
import com.wq.model.photoModel;
import com.wq.model.proAttr;
import com.wq.model.productList;
import com.wq.model.product;
import com.wq.utils.CommonUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.stringUtils;

/** 产品浏览 */
public class productShowActivity extends BaseActivity {
	@ViewInject(id = R.id.add_scroll)
	ScrollView scroll;
	@ViewInject(id = R.id.layout_main)
	LinearLayout layout_main;
	// @ViewInject(id = R.id.me_grid_product_ablum)
	// HorizontalVariableListView list_ablum;// 显示相册

	@ViewInject(id = R.id.txt_pro_name)
	TextView txt_name;// 产品名称
	@ViewInject(id = R.id.txt_pro_price)
	TextView txt_price;// 产品价格
	@ViewInject(id = R.id.txt_pro_intro)
	TextView txt_intro;// 产品简介
	@ViewInject(id = R.id.txt_pro_cate)
	TextView txt_cate;// 分类
	@ViewInject(id = R.id.pro_attr_list)
	InScrolllistView list_attr;// 显示属性
	@ViewInject(id = R.id.layout_index)
	LinearLayout layout_index;

	proAttrAdapter attrAdapter;

	/** 产品 */
	product product = new product();

	@ViewInject(id = R.id.txt_index)
	TextView txt_index;
	@ViewInject(id = R.id.frame_img)
	FrameLayout frame_img;
	private int count_drawble = 0; // 总共的图片大小
	private int cur_index = 0; // 当前图片位置
	private boolean isalive = true;
	private static int MSG_UPDATE = 1;
	@ViewInject(id = R.id.tabpager)
	ViewPager mTabPager;
	private ArrayList<View> imageViewList = new ArrayList<View>();
	FinalBitmap finalBitmap;
	BitmapDisplayConfig config;
	// 从生意圈查看产品详情
	String proId = "";// 产品id

	// addImagHorListviewAdapter ablumAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_show_activity);
		BaseApplication.getInstance().addActivity(this);
		finalBitmap = FinalBitmap.create(this);
		config = new BitmapDisplayConfig();
		config.setLoadingBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.add_prompt));
		config.setLoadfailBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.add_prompt));
		initData();

	}

	private void initData() {
		proId = this.getIntent().getStringExtra("productId");
		if (TextUtils.isEmpty(proId)) {
			Bundle b = this.getIntent().getExtras();
			if (b != null) {
				product = (product) b.getSerializable("product");
				BindUI();
			}
		} else {
			httpData();

		}
		initNavigation();
		// 凡是属性名字为空的不显示

	}

	private void BindUI() {
		ArrayList<proAttr> tmpList = new ArrayList<proAttr>();
		tmpList.addAll(product.getAttrList());

		for (int i = 0; i < product.getAttrList().size(); i++) {
			if (TextUtils.isEmpty(product.getAttrList().get(i).getAttrValue())) {
				tmpList.remove(i);
			}

		}
		product.getAttrList().clear();
		product.getAttrList().addAll(tmpList);
		attrAdapter = new proAttrAdapter(this, product.getAttrList(), false,
				new com.wq.Adapter.proAttrAdapter.itemclick() {
					@Override
					public void myclick(int position) {
						// TODO Auto-generated method stub
					}
				});
		list_attr.setAdapter(attrAdapter);
		layout_index.getBackground().setAlpha(50);
		txt_index.setTextColor(Color.argb(255, 255, 255, 255));
		txt_name.setText(product.getTitle());
		txt_intro.setText(product.getIntro());
		if (!TextUtils.isEmpty(product.getPrice()))
			txt_price.setText("￥"
					+ CommonUtil.insertComma(product.getPrice(), 2));
		if (!TextUtils.isEmpty(product.getCateId())) {
			txt_cate.setTag(product.getCateId());
		}
		if (!TextUtils.isEmpty(product.getCateName())) {
			txt_cate.setText(product.getCateName());
		}
		list_attr.setFocusable(false);
		setViewTouchListener(scroll);

		// 产品滚动图片

		count_drawble = product.getPicList().size();

		if (count_drawble <= 0) {
			frame_img.setVisibility(View.GONE);
			return;
		}

		for (int i = 0; i < product.getPicList().size(); i++) {
			ImageView img = new ImageView(this);
			LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			img.setScaleType(ScaleType.CENTER_CROP);
			img.setLayoutParams(param);
			img.setTag(i);
			img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					scanPhoto(product.getPicList(),
							Integer.parseInt(String.valueOf(v.getTag())),
							false, false, null);
					// TODO Auto-generated method stub

				}
			});
			finalBitmap.display(img, product.getPicList().get(i).getImageUrl(),
					config);
			imageViewList.add(img);
		}
		mTabPager.setAdapter(new viewPageAdapter(imageViewList));
		mTabPager.setCurrentItem(0);
		txt_index.setText((1) + "/" + count_drawble);
		// mTabPager.setOffscreenPageLimit(4);

		// mTabPager.setOffscreenPageLimit(4);//不进行预加载
		mTabPager.setOnPageChangeListener(new myOnPageChangeListener());
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (isalive) {
					cur_index = cur_index % count_drawble; // 图片区间[0,count_drawable)

					// msg.arg1 = cur_index
					Message msg = mhandler.obtainMessage(MSG_UPDATE, cur_index,
							0);
					mhandler.sendMessage(msg);
					// 更新时间间隔为 3s
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cur_index++; // 放置在Thread.sleep(2000)
									// ；防止mhandler处理消息的同步性，导致cur_index>=count_drawble
				}
			}
		}).start();
	}

	private void httpData() {
		
		AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		params.put(httpUtil.PRODUCT_PRO_ID, proId);
		showProgress(R.string.dialog_loading);
		httpUtil.post(httpUtil.PRODUCT_DETAIL_URL, params,
				new AjaxCallBack<String>() {
					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {

						try {

							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);
							if (errcode.equals(httpUtil.errCode_success)) {
								JSONObject mydata = jsonResult
										.getJSONObject("data");
									// p.setCount(object.getInt("count"));
									JSONObject proObject = mydata
											.getJSONObject("commodity");
								
									
										product.setId(proObject.getString("id"));
										product.setCateId(mydata
												.getString("typeId"));
										product.setCateName(mydata
												.getString("typeName"));
										product.setTitle(proObject.getString("name"));
										product.setIntro(proObject
												.getString("content"));
										product.setPrice(proObject
												.getString("priceNow"));

										// 属性
										String attrStr = proObject
												.getString("attribute");
										product.getAttrList().addAll(
												stringUtils.splitAttr(attrStr));
										// 图片
										JSONArray picArray = proObject
												.getJSONArray("imgFileArray");
										for (int k = 0; k < picArray.length(); k++) {
											JSONObject picObject = picArray
													.getJSONObject(k);
											photoModel pic = new photoModel();
											pic.setId(picObject
													.getString("imgId"));
											pic.setImageUrl(picObject
													.getString("imgUrl"));
											pic.setShareModel(product);
											pic.setShareType(photoModel.PRODUCT_SHARE_FLAG);
											product.getPicList().add(pic);
										}
										BindUI();
									
							}  else {
								errMsg = jsonResult.getString(httpUtil.ERR_MGS);
								CommonUtil.showToast(
										productShowActivity.this, errMsg);
							}
						
						} catch (JSONException e) {

							
							CommonUtil.showToast(productShowActivity.this,
									R.string.string_http_err_data);
							e.printStackTrace();
						} finally {
							hideProgress();
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						hideProgress();
						CommonUtil.showToastHttp(productShowActivity.this,
								errorNo);
					}
				});
	}

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE) {
				mTabPager.setCurrentItem(msg.arg1, true);
				// 直接更改图片 ，不触发Gallery.OnItemSelectedListener监听
				// imgSwitcher.setBackgroundResource(imgAdapter.getResId(msg.arg1));
			}
		}
	};

	private void initNavigation() {
		initNavitation(getString(R.string.me_string_product_detail_title),
				product.getTitle(), "", -1, new EditClickListener() {
					@Override
					public void editClick() {

						// TODO Auto-generated method stub
					}

					@Override
					public void backClick() {
						finish();
						animOut();
						// TODO Auto-generated method stub
					}
				});
	}

	// 图片轮播监听事件
	public class myOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			cur_index = position;
			txt_index.setText((cur_index + 1) + "/" + count_drawble);
			// mTabImg.startAnimation(anim);
			// TODO Auto-generated method stub

		}
	}

	public void onDestroy() {
		super.onDestroy();
		isalive = false;
	}

}
