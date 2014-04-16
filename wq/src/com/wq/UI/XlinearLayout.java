package com.wq.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;

import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * 
 * 时间拦截
 * 
 * @author Administrator
 * 
 */
public class XlinearLayout extends LinearLayout {
	ScrollView scrollView;

	public XlinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public boolean onInterceptTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview
			// 停住不能滚动
			super.requestDisallowInterceptTouchEvent(false);

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
