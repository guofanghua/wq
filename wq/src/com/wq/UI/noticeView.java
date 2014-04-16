package com.wq.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.endure.wq.R;

import com.wq.model.User;
import com.wq.utils.CommonUtil;
import com.wq.utils.LoggerUtil;
import com.wq.utils.httpUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * 首页的系统公告
 * 
 * @author Administrator
 * 
 */
public class noticeView extends LinearLayout {

	private Context mContext;
	private ViewFlipper viewFlipper;
	private View scrollTitleView;
	// private Intent intent;
	// private JSONArray array = null;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private Map<String, Object> map = null;

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public noticeView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public noticeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();

	}

	/**
	 * 网络请求后返回公告内容进行适配
	 */
	protected void bindNotices() {
		// TODO Auto-generated method stub

		viewFlipper.removeAllViews();

		for (int i = 0; i < list.size(); i++) {
			try {
				map = list.get(i);
				TextView textView = new TextView(mContext);
				textView.setText(map.get("title").toString());
				textView.setTextColor(Color.WHITE);
				textView.setTextSize(15);

				// textView.setOnClickListener(new NoticeTitleOnClickListener(
				// mContext, Integer.parseInt(map.get("ID").toString()),
				// map.get("type").toString()));

				LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				viewFlipper.addView(textView, lp);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}

	private void init() {
		//try {
			// 在此处开任务获取
			//bindLinearLayout();
			// 开启任务获取动态公告
			/*AjaxParams params = new AjaxParams();
			params.put(httpUtil.USER_ID, Company.id);
			params.put(httpUtil.USER_KEY, Company.userKey);
			httpUtil.get(httpUtil.SYSTEM_INFO_URL, params,
					new AjaxCallBack<String>() {
						public void onStart() {

						}

						public void onSuccess(String result) {
							LoggerUtil.i(result);
							try {
								if(result != null && result.startsWith("\ufeff"))  
								{  
									result =  result.substring(1);  
								}  
								JSONObject jsonResult = new JSONObject(result);
								String errcode = jsonResult
										.getString(httpUtil.ERR_CODE);
								if (errcode.equals(httpUtil.errCode_success)) {

									JSONArray arr = jsonResult
											.getJSONArray("sysInfo");
									LoggerUtil.i(arr.length() + ",,,");
									for (int i = 0; i < arr.length(); i++) {
										jsonResult = arr.getJSONObject(i);
										map = new HashMap<String, Object>();
										map.put("title",
												jsonResult.get("title"));
										map.put("content",
												jsonResult.get("content"));
										map.put("time", jsonResult.get("time"));
										map.put("type", jsonResult.get("type"));
										list.add(map);
									}
									bindNotices();

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							CommonUtil.showToast(mContext, strMsg);
						}
					});

			
			// new notice_task().execute("");
		} catch (Exception e) {

		}*/
	}

	/**
	 * 开启任务。获取公告
	 * 
	 * */
	/*
	 * class notice_task extends AsyncTask<String, Integer, Integer> {
	 * 
	 * @Override protected Integer doInBackground(String... params) { // TODO
	 * Auto-generated method stub try { JsonDataGetApi api = new
	 * JsonDataGetApi(); JSONObject obj = null; obj =
	 * api.getPostObject("GetSiteDefaultAffiches", null); String tmpTitle = "";
	 * String tmpTime; if (obj != null) { array =
	 * obj.getJSONArray("GetSiteDefaultAffichesResult"); for (int i = 0; i <
	 * array.length(); i++) { obj = array.getJSONObject(i); map = new
	 * HashMap<String, Object>(); map.put("ID", obj.get("ID"));
	 * map.put("NewType", obj.get("NewType")); tmpTitle =
	 * obj.get("Title").toString().trim().length() <= 15 ? obj
	 * .get("Title").toString() : obj.get("Title")
	 * .toString().trim().substring(0, 15) + "..."; map.put("Title", tmpTitle);
	 * tmpTime = obj.get("DateTime").toString(); map.put("DateTime",
	 * tmpTime.substring(tmpTime.length() - 8, tmpTime.length()));
	 * list.add(map);
	 * 
	 * } return 1; } else return 0; } catch (Exception e) {
	 * 
	 * return -1; // TODO Auto-generated catch block }
	 * 
	 * }
	 * 
	 * protected void onPostExecute(Integer result) { if (result == 1) {
	 * bindNotices();// 将数据绑定到textview
	 * 
	 * }
	 * 
	 * } }
	 */

	/**
	 * 初始化自定义的布局
	 */
	public void bindLinearLayout() {
	/*	scrollTitleView = LayoutInflater.from(mContext).inflate(
				R.layout.main_notice, null);
		LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		addView(scrollTitleView, layoutParams);
		viewFlipper = (ViewFlipper) scrollTitleView
				.findViewById(R.id.flipper_scrollTitle);

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_down));// android.R.anim.fade_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.slide_out_top));// android.R.anim.fade_out));
		viewFlipper.startFlipping();*/

	}

	/**
	 * 公告title监听
	 * 
	 * @author Nono
	 * 
	 */
	// class NoticeTitleOnClickListener implements OnClickListener {
	//
	// public NoticeTitleOnClickListener(Context context, Integer id,
	// String NewType) {
	// // this.context = context;
	// // this.titleid = id;
	// // this.NewType = NewType;
	// }
	//
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// // disPlayNoticeContent(context, titleid, NewType);
	// }
	//
	// }

	/**
	 * // * 显示notice的具体内容 // * // * @param context // * @param titleid //
	 */
	// public void disPlayNoticeContent(Context context, Integer titleid,
	// String NewType) {
	// // TODO Auto-generated method stub
	// intent = new Intent(context, help_noticeDetail.class);
	// intent.putExtra("ID", titleid);
	// intent.putExtra("NewType", NewType);
	// ((Activity) context).startActivity(intent);
	//
	// }

}
