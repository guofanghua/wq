package com.wq.UI;

import android.content.Context;
import android.util.AttributeSet;

import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 
 * 包含在scrollview中。此处做触摸拦截
 * 
 * @author Administrator
 * 
 */
public class InScrolllistView extends ListView {
	ScrollView scrollView;

	public InScrolllistView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
//去除对父控件的拦截
//	public boolean onInterceptTouchEvent(MotionEvent e) {
//		switch (e.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			// 当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview
//			// 停住不能滚动
//			super.requestDisallowInterceptTouchEvent(true);
//
//		case MotionEvent.ACTION_MOVE:
//
//			break;
//		case MotionEvent.ACTION_UP:
//
//		case MotionEvent.ACTION_CANCEL:
//
//			super.requestDisallowInterceptTouchEvent(false);// 当手指松开时，让父ScrollView重新拿到onTouch权限
//			break;
//		default:
//			break;
//		}
//		return super.onInterceptTouchEvent(e);
//	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(

		Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, expandSpec);

	}
}
