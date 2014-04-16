package com.wq.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class indexLayout extends LinearLayout {

	public indexLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 去除对父控件的拦截
	public boolean onInterceptTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview
			// 停住不能滚动
			super.requestDisallowInterceptTouchEvent(true);

		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:

		case MotionEvent.ACTION_CANCEL:

			super.requestDisallowInterceptTouchEvent(false);// 当手指松开时，让父ScrollView重新拿到onTouch权限
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(e);
	}

}
