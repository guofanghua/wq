package com.example.dbtest;

import java.util.ArrayList;

import net.endure.framework.FinalActivity;
import net.endure.framework.FinalDb;
import net.endure.framework.annotation.view.ViewInject;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FinalActivity {
	@ViewInject(id = R.id.button1)
	Button btn_add;
	@ViewInject(id = R.id.button2)
	Button btn_check;
	@ViewInject(id = R.id.textView2)
	TextView txt_show;
	FinalDb db;
	private int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = FinalDb.create(this);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				testModel t = new testModel();
				index++;
				t.setName("guo" + index);
				t.setAge(index + "");
				db.save(t);
				// TODO Auto-generated method stub

			}
		});
		btn_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String sql = "select * from testModel ";
					if (db.findDbListBySQL(testModel.class, sql).size() > 0) {
						ArrayList<testModel> list = (ArrayList<testModel>) db
								.findDbListBySQL(testModel.class, sql);
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < list.size(); i++) {
							sb.append("name=" + list.get(i).getName() + ",");
							sb.append("age=" + list.get(i).getAge() + "\n");
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
