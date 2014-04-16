package com.wq.SyncLoadImage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadStateView extends RelativeLayout{
	


	public LoadStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	
	}

	public void startLoad(){
		
	}
	
	public void stopLoad(){
		
	}
	
	public void showError(){
	
	}
	
	public void showEmpty(){
		
	}
	
	public void setOnReloadClickListener(OnClickListener onReloadClickListener){
		
	}
}
