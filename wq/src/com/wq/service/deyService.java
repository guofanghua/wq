package com.wq.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import net.endure.framework.FinalHttp;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;
import org.json.JSONArray;
import org.json.JSONObject;
import com.wq.mainActivity;
import com.wq.model.DecInfo;
import com.wq.model.User;
import com.wq.model.partComModel;
import com.wq.utils.dateUtil;
import com.wq.utils.httpUtil;
import com.wq.utils.sharedPreferenceUtil;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class deyService extends Service {
	// public static Handler deyHandler = null;
	private final long interval = 1000 * 60;// 10秒钟
	ArrayList<DecInfo> list = new ArrayList<DecInfo>();
	// public static Handler inqHandler = null;// linq
	// public static Handler partHandler = null;
	// public static Handler findHandler = null;
	// public static Handler meHandler = null;
	Timer timer = null;// 定时器定时访问
	FinalHttp fh = null;
	ArrayList<partComModel> listnewAttention = new ArrayList<partComModel>();
	ArrayList<partComModel> listmeAttention = new ArrayList<partComModel>();
	ArrayList<partComModel> mplistModel = new ArrayList<partComModel>();// 名片
	// ArrayList<String> newCancel = new ArrayList<String>();
	// ArrayList<String> meCancel = new ArrayList<String>();
	StringBuilder sbAttCancel = new StringBuilder();
	StringBuilder sbAttMeCancel = new StringBuilder();
	public static boolean isAlive = true;// 网络是否访问完成
	private static boolean isOpen = false;// 服务是否开启

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	@Override
	public void onDestroy() {

		// TODO Auto-generated method stub
		super.onDestroy();

		isOpen = false;
		stopTimer();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		if (!isOpen) {
			fh = new FinalHttp();
			startTimer();

			isOpen = true;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {

		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {

		// TODO Auto-generated method stub
		return null;
	}

	private void startTimer() {

		if (timer == null) {
			timer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					Message msg = timeHander.obtainMessage();
					msg.sendToTarget();
				}
			};
			timer.schedule(task, 1, interval);
		}
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
		}

		timer = null;
	}

	private void httpData() {
		isAlive = false;
		final AjaxParams params = new AjaxParams();
		params.put(httpUtil.USER_ID, User.id);
		params.put(httpUtil.USER_KEY, User.userKey);
		httpUtil.post(httpUtil.INQ_INFO_URL, params,
				new AjaxCallBack<String>() {

					private String errMsg = "";
					private String errcode = "";

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {
						String lastIcon = "";
						try {

							JSONObject jsonResult = new JSONObject(result);
							errcode = jsonResult.getString(httpUtil.ERR_CODE);

							if (errcode.equals(httpUtil.errCode_success)) {
								list.clear();
								//空间动态
								JSONArray array = jsonResult
										.getJSONArray("spatialDynamicArray");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									DecInfo info = new DecInfo();
									info.setIcon(obj.getString("icon"));
									info.setInfo(obj.getString("info"));
									info.setName(obj.getString("name"));
									info.setNewDynamicCount(obj
											.getString("newDynamicCount"));
									info.setPartnerId(obj
											.getString("partnerId"));
									info.setSdmicId(obj.getString("sdmicId"));
								
									info.setSouceType(obj
											.getString("souceType"));
									info.setTime(obj.getString("time"));
									info.setUrl(obj.getString("url"));
									info.setVqh(obj.getString("wqh"));
									list.add(info);
								}
								lastIcon = jsonResult
										.getString("lastTradePartnerIcon");

								// 最新关注我的伙伴
								JSONObject newJs = jsonResult
										.getJSONObject("attentionMeDynamic");
								JSONArray newAttenArr = newJs
										.getJSONArray("newAttentionArray");
								listnewAttention.clear();
								for (int i = 0; i < newAttenArr.length(); i++) {
									JSONObject o = newAttenArr.getJSONObject(i);
									partComModel item = new partComModel();
									item.setPid(o.getString("id"));
									item.setEnterpriseId(o
											.getString("enterpriseId"));
									item.setIcon(o.getString("icon"));
									item.setName(o.getString("name"));
									item.setTelePhone(o.getString("telePhone"));
									item.setMobile(o.getString("moblie"));
									item.setChannel(o.getString("channel"));
									item.setIsAttention("isAttentionMe");
									item.setContactName(o
											.getString("contactName"));
									item.setOccupation(o
											.getString("occupation"));
									item.setVqh(o.getString("wqh"));
									item.setTime(dateUtil
											.formatDate(new Date()));
									item.setFlag("1");
									listnewAttention.add(item);
								}
								// 取消对我的关注的id
								JSONArray cancelAttArr = newJs
										.getJSONArray("cancelAttentionArray");
								sbAttCancel = new StringBuilder();

								for (int i = 0; i < cancelAttArr.length(); i++) {
									JSONObject item = cancelAttArr
											.getJSONObject(i);
									sbAttCancel.append("'");
									sbAttCancel.append(item.get("enterpriseId"));
									sbAttCancel.append("'");

									sbAttCancel.append(",");

								}
								// 我关注的
								JSONObject menewJs = jsonResult
										.getJSONObject("meAttentionDynamic");
								JSONArray MenewAttenArr = menewJs
										.getJSONArray("newAttentionArray");
								listmeAttention.clear();
								for (int i = 0; i < MenewAttenArr.length(); i++) {
									JSONObject o = MenewAttenArr
											.getJSONObject(i);
									partComModel item = new partComModel();
									item.setPid(o.getString("id"));
									item.setEnterpriseId(o
											.getString("enterpriseId"));
									item.setIcon(o.getString("icon"));
									item.setName(o.getString("name"));
									item.setTelePhone(o.getString("telePhone"));
									item.setMobile(o.getString("moblie"));
									item.setChannel(o.getString("channel"));
									item.setIsAttention("isAttentionMe");
									item.setContactName(o
											.getString("contactName"));
									item.setOccupation(o
											.getString("occupation"));
									item.setVqh(o.getString("wqh"));
									item.setFlag("0");
									listmeAttention.add(item);
								}
								// 取消对我的关注的id
								JSONArray MecancelAttArr = menewJs
										.getJSONArray("cancelAttentionArray");
								sbAttMeCancel = new StringBuilder();

								for (int i = 0; i < MecancelAttArr.length(); i++) {
									JSONObject item = MecancelAttArr
											.getJSONObject(i);
									sbAttMeCancel.append("'");
									sbAttMeCancel.append(item
											.get("enterpriseId"));
									sbAttMeCancel.append("'");
									sbAttMeCancel.append(",");

								}
								// 名片录
								JSONArray mpArr = jsonResult
										.getJSONArray("changeBusCardArray");
								mplistModel.clear();
								for (int i = 0; i < mpArr.length(); i++) {
									JSONObject o = mpArr.getJSONObject(i);
									partComModel item = new partComModel();
									item.setEnterpriseId(o
											.getString("enterpriseId"));
									item.setContactName(o.getString("name"));
									item.setOccupation(o
											.getString("occupation"));
									item.setIcon(o.getString("icon"));
									item.setFlag("0");
									mplistModel.add(item);
								}

								// 伙伴
								int partCount = Integer.parseInt(jsonResult
										.getString("attentionMeCount"));
								int mypartCount = Integer.parseInt(sharedPreferenceUtil
										.readTipPart(getApplicationContext()));
								mypartCount += partCount;
								if (mypartCount > 0) {
									sharedPreferenceUtil.saveTipPart(
											getApplicationContext(),
											mypartCount + "");
									User.bArr[2] = true;
								} else
									User.bArr[2] = false;
								User.tipCountArr[1] = mypartCount + "";
								// 企业圈
								int tradeCount = Integer.parseInt(jsonResult
										.getString("tradePartnerCount"));
								int commCount = Integer.parseInt(jsonResult
										.getString("tradeCommentCount"));
								if (tradeCount == -1) {
									User.bArr[5] = true;
								} else
									User.bArr[5] = false;
								if (commCount > 0) {
									User.bArr[4] = true;
									User.tipCountArr[2] = commCount + "";
								} else {
									User.bArr[4] = false;
									User.tipCountArr[2] = "0";
								}

								// 此处发送广播
								Intent broadcast = new Intent();
								broadcast.setAction(mainActivity.deyServiceBor);
								if (list.size() > 0)
									broadcast.putExtra("list", list);
								broadcast.putExtra("lastIcon", lastIcon);
								// 最新对我的关注
								if (listnewAttention.size() > 0) {
									broadcast.putExtra("newAttention",
											listnewAttention);
								}
								// 名片录信息更新
								if (mplistModel.size() > 0) {
									broadcast.putExtra("mplistModel",
											mplistModel);
								}
								// 取消对我的关注
								if (sbAttCancel.length() > 0) {
									broadcast.putExtra(
											"cancelId",
											sbAttCancel.toString().substring(0,
													sbAttCancel.length() - 1));
								}
								// 我最新关注的
								if (listmeAttention.size() > 0) {
									broadcast.putExtra("newMeAttention",
											listmeAttention);
								}
								// 我最新取消的关注
								if (sbAttMeCancel.length() > 0) {
									broadcast
											.putExtra(
													"meCancelId",
													sbAttMeCancel
															.toString()
															.substring(
																	0,
																	sbAttMeCancel
																			.length() - 1));
								}
								sendOrderedBroadcast(broadcast, null);

							} else {
								errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							isAlive = true;
						}
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						isAlive = true;

					}
				});

	}

	Handler timeHander = new Handler() {
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			if (isAlive)
				httpData();
		}
	};
}
