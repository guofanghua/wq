package com.wq.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.*;
import android.view.*;

public class expandedListview extends ExpandableListView // implements
{// AbsListView.OnScrollListener {

	private OnScrollListener mOnScrollListener = null;

	public expandedListview(Context context, AttributeSet attrs) {
		super(context, attrs);

		// TODO Auto-generated constructor stub
	}

	// 画view的子view的时候。需要调用dispatchDraw，画view的时候调用onDraw
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	public void setOnScrollListener(
			AbsListView.OnScrollListener paramOnScrollListener) {
		this.mOnScrollListener = paramOnScrollListener;
	}

	public void setAdapter(ExpandableListAdapter paramExpandableListAdapter) {
		super.setAdapter(paramExpandableListAdapter);
	}

	// TODO Auto-generated method stub

}
