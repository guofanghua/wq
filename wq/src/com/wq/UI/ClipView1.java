package com.wq.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ClipView1 extends View {
	public static int width = 0;
	public static int height = 0;

	public static int SX = 120;// 显示器X轴起始余量

	public static int SY = 0;

	// public static final int SY = 80;

	public ClipView1(Context context) {
		super(context);
		dip2px();

	}

	public ClipView1(Context context, AttributeSet attrs) {
		super(context, attrs);
		dip2px();
	}

	public ClipView1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		dip2px();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/* 这里就是绘制矩形区域 */
		width = this.getWidth();
		height = this.getHeight();
		Paint paint = new Paint();
		paint.setColor(0xaa000000);
		// paint.setColor(Color.RED);
		// 是画完区域后，剩下的就是中间透明部分
		// top区域
		canvas.drawRect(0, 0, width, height / 2 - SX + SY, paint);
		// left
		canvas.drawRect(0, height / 2 - SX + SY, width / 2 - SX, height / 2
				+ SX - SY, paint);
		// right
		canvas.drawRect(width / 2 + SX, height / 2 - SX + SY, width, height / 2
				+ SX - SY, paint);
		// bottom
		canvas.drawRect(0, height / 2 + SX - SY, width, height, paint);

		Paint paintLine = new Paint();
		paintLine.setColor(0xFFFFFFFF);
		// 上
		canvas.drawLine(width / 2 - SX, height / 2 - SX + SY, width / 2 + SX,
				height / 2 - SX + SY, paintLine);
		// 右
		canvas.drawLine(width / 2 + SX, (height) / 2 - SX + SY, width / 2 + SX,
				(height) / 2 + SX - SY, paintLine);
		// 下
		canvas.drawLine(width / 2 - SX, height / 2 + SX - SY, width / 2 + SX,
				(height) / 2 + SX - SY, paintLine);
		// 左
		canvas.drawLine(width / 2 - SX, (height) / 2 - SX + SY, width / 2 - SX,
				(height) / 2 + SX - SY, paintLine);

	}

	private void dip2px() {
		try {

			// 通过反射来获取
			// Class<?> c = Class.forName("com.android.internal.R$dimen");
			// Object obj = c.newInstance();
			// Field field = c.getField("status_bar_height");
			// int x = Integer.parseInt(field.get(obj).toString());
			final float scale = getResources().getDisplayMetrics().density;
			SX = 100;
			SY = -30;
			SY = (int) (SY * scale + 0.5f);
			SX = (int) (SX * scale + 0.5f);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
