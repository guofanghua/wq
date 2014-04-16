package com.wq.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class autoChLineLinearLayout extends ViewGroup {
	private int mCellWidth;
	private int mCellHeight;

	public autoChLineLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public int getmCellWidth() {
		return mCellWidth;
	}

	public void setmCellWidth(int mCellWidth) {
		this.mCellWidth = mCellWidth;
		requestLayout();
	}

	public int getmCellHeight() {
		return mCellHeight;
	}

	public void setmCellHeight(int mCellHeight) {
		this.mCellHeight = mCellHeight;
		requestLayout();
	}

	/**
	 * 控制子控件的换行
	 * */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int cellWidth = mCellWidth;
		int cellHeight = mCellHeight;
		int columns = (r - 1) / cellWidth;
		if (columns < 0)
			columns = 1;
		int x = 0;
		int y = 0;
		int i = 0;
		int count = getChildCount();
		for (int j = 0; j < count; j++) {
			View childView = getChildAt(j);
			// 获取子控件child的宽高
			int w = childView.getMeasuredWidth();
			int h = childView.getMeasuredHeight();
			// 计算子控件的顶点坐标
			int left = x + ((cellWidth - w) / 2);
			int top = y + ((cellHeight - h) / 2);
			// 布局子控件
			childView.layout(left, top, left + w, top + h);
			if (i >= (columns - 1)) {
				i = 0;
				x = 0;
				y += cellHeight;
			} else {
				i++;
				x += cellWidth;
			}
		}
	}

	/**
	 * 计算控件及子控件所占区域
	 * */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 创建测量参数
		int cellWidthSpec = MeasureSpec.makeMeasureSpec(mCellWidth,
				MeasureSpec.AT_MOST);
		int cellHeightSpec = MeasureSpec.makeMeasureSpec(mCellHeight,
				MeasureSpec.AT_MOST);
		int count = getChildCount();
		// 设置子控件child的宽高
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			childView.measure(cellWidthSpec, heightMeasureSpec);
		}
		// 设置容器控件所占区域大小
		setMeasuredDimension(resolveSize(mCellWidth * count, widthMeasureSpec),
				resolveSize(mCellHeight * count, heightMeasureSpec));

	}
	/**
	 * 
	 * */
}
