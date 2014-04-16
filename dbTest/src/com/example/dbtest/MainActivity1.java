package com.example.dbtest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import net.endure.framework.FinalActivity;
import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity1 extends FinalActivity {
	@ViewInject(id = R.id.button1)
	Button btn_addone;
	@ViewInject(id = R.id.button3)
	Button btn_addmany;
	@ViewInject(id = R.id.button2)
	Button btn_check;
	@ViewInject(id = R.id.textView2)
	TextView txt_show;
	FinalDb db;
	private int index = 0;
	oneModel one = new oneModel();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		db = FinalDb.create(this);

		Date now = new Date();
		long k = now.getTime();
		Log.i("one", now.getTime() + "");
		String kk = dateUtil.formatDate(now);
		Log.i("nowtime", kk);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			now = sdf.parse(kk);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long k2= now.getTime() + TimeZone.getDefault().getRawOffset();
		Log.i("two", now.getTime() + TimeZone.getDefault().getRawOffset() + "");
		Log.i("spand",
				(now.getTime() + TimeZone.getDefault().getRawOffset() - k) + "");
		txt_show.setText(k+",,"+k2+",,,"+(k2-k));
	

		btn_addone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				one = new oneModel();
				one.setId((index++) + "");
				one.setName("one" + index);
				db.save(one);

				// TODO Auto-generated method stub

			}
		});
		btn_addmany.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				manyModel many = new manyModel();
//				many.setId(index + "");
//				many.setParentId(one.getId());
//				many.setName("many" + index);
//				db.save(many);
				// TODO Auto-generated method stub

			}
		});
		btn_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String sql = "select oneModel.id,oneModel.name,(select * from manyModel where oneModel.id=manyModel.parentId) from  oneModel ";
					if (db.findDbListBySQL(testModel.class, sql).size() > 0) {
						ArrayList<oneModel> list = (ArrayList<oneModel>) db
								.findDbListBySQL(oneModel.class, sql);
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < list.size(); i++) {
							sb.append("name=" + list.get(i).getName() + ",");
							sb.append("age=" + list.get(i).getManyList().size()
									+ "\n");
						}
						txt_show.setText(sb.toString());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// TODO Auto-generated method stub

			}
		});
	}
}
