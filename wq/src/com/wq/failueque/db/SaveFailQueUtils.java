package com.wq.failueque.db;

import java.util.ArrayList;
import java.util.List;

import net.endure.framework.FinalDb;

import org.json.JSONObject;

import com.wq.failueque.base.IMediator;
import com.wq.failueque.mod.WRequest;

public class SaveFailQueUtils {
	private static SaveFailQueUtils mInstance;
	private FinalDb mFdb;

	private SaveFailQueUtils() {
		
	}

	public static SaveFailQueUtils getInstance() {
		if (mInstance == null) {
			mInstance = new SaveFailQueUtils();
		}
		return mInstance;
	}

	public void saveFailRequest(IMediator mediator, WRequest request) {
		if (mediator == null || request == null)
			return;
		if (mFdb == null) {
			if (mediator.getContext() == null) {
				return;
			}
			mFdb = FinalDb.create(mediator.getContext());
		}
		try {
			DataMdl mod = new DataMdl();
			mod.setReqId(request.getReqId());
			mod.setData(request.createJson());
			mFdb.deleteByWhere(DataMdl.class, "reqId=" + mod.getReqId());
			mFdb.save(mod);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteRequest(IMediator mediator, WRequest request) {
		if (mediator == null || request == null)
			return;
		if (mFdb == null) {
			if (mediator.getContext() == null) {
				return;
			}
			mFdb = FinalDb.create(mediator.getContext());
		}
		try {
			DataMdl mod = new DataMdl();
			mod.setReqId(request.getReqId());
			mod.setData(request.createJson());
			mFdb.deleteByWhere(DataMdl.class, "reqId=" + mod.getReqId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<WRequest> readAllFailRequest(IMediator mediator) {
		List<WRequest> reqList = new ArrayList<WRequest>();
		if (mediator == null)
			return reqList;
		if (mFdb == null) {
			if (mediator.getContext() == null) {
				return reqList;
			}
			mFdb = FinalDb.create(mediator.getContext());
		}
		try {
			List<DataMdl> modList = mFdb.findAll(DataMdl.class);
			if (modList != null && modList.size() > 0) {
				for (int i = 0; i < modList.size(); i++) {
					try {
						WRequest request = new WRequest(0);
						JSONObject obj = new JSONObject(modList.get(i)
								.getData());
						request.initFromJson(obj);
						reqList.add(request);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reqList;
	}

}
