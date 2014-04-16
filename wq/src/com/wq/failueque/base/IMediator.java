package com.wq.failueque.base;

import java.util.HashMap;
import java.util.Map;

import net.endure.framework.FinalActivity;
import net.endure.framework.afinal.http.AjaxCallBack;
import net.endure.framework.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wq.failueque.db.SaveFailQueUtils;
import com.wq.failueque.mod.WRequest;
import com.wq.utils.httpUtil;

public abstract class IMediator extends FinalActivity {

	private Map<String, WRequest> requestLinkMap;

	public void sendHttpRequest(WRequest request, boolean showProgress) {
		if (requestLinkMap == null) {
			requestLinkMap = new HashMap<String, WRequest>();
		}
		request.requestOver();
		System.out.println("IMediator:Request " + request.getReqId()
				+ " request " + request.getRequestCount() + " times");
		AjaxParams params = new AjaxParams();
		if (request.getParam() != null && request.getParam().size() > 0) {
			for (String key : request.getParam().keySet()) {
				params.put(key, request.getParam().get(key));
			}
		}
		if (requestLinkMap.get(httpUtil.BASE_SERVER_URL + request.getReqUrl()) == null) {
			requestLinkMap.put(httpUtil.BASE_SERVER_URL + request.getReqUrl(),
					request);
		}
		if (showProgress)
			showProgress();
		httpUtil.post(httpUtil.CIRCLE_PRO_ADD_URL, params,
				new AjaxCallBack<String>(request) {
					private String errMsg = "";
					private WRequest req = (WRequest) getObjData();

					public void onStart() {

					}

					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonResult = new JSONObject(result);
							String errcode = jsonResult
									.getString(httpUtil.ERR_CODE);
							errMsg = jsonResult.getString(httpUtil.ERR_MGS);
							int code = -1;
							if (httpUtil.errCode_success.equals(errcode)) {
								code = 0;
							} else {
								try {
									code = Integer.parseInt(errcode);
								} catch (Exception e) {
									code = -1;
									e.printStackTrace();
								}
							}
							callBack(req.getReqId(), code,
									httpUtil.errCode_success, errMsg,
									jsonResult);
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							hideProgress();
							if (requestLinkMap != null) {
								requestLinkMap.remove(httpUtil.BASE_SERVER_URL
										+ req.getReqUrl());
							}
							if (req.isAddInFailQueue())
								SaveFailQueUtils.getInstance().deleteRequest(
										IMediator.this, req);
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						httpError(errorNo, strMsg);
						if (req.getRequestCount() < req.getMaxRequestTimes()) {
							sendHttpRequest(req, false);
						} else {
							if (req.isAddInFailQueue())
								saveFaileRequest(req);
							if (requestLinkMap != null) {
								requestLinkMap.remove(httpUtil.BASE_SERVER_URL
										+ req.getReqUrl());
							}
						}
					}
				});

	}

	public void saveFaileRequest(WRequest request) {
		SaveFailQueUtils.getInstance().saveFailRequest(this, request);
	}

	/**
	 * @param reqId
	 *            请求ID
	 * @param code
	 *            返回标号，成功为0
	 * @param codeStr
	 *            错误编号字符串
	 * @param errorMsg
	 *            错误内容
	 * @param data
	 *            结果数据
	 */
	public void callBack(int reqId, int code, String codeStr, String errorMsg,
			JSONObject data) {

	}

	public void httpError(int errorNo, String msg) {

	}

	public void showProgress() {
	}

	public void hideProgress() {
	}

	abstract public Context getContext();

}
