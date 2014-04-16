package com.wq.UI;

import java.util.ArrayList;

import net.endure.framework.FinalBitmap;
import net.endure.framework.bitmap.core.BitmapDisplayConfig;

import com.endure.wq.R;
import com.wq.Interface.IecCircleInterface;
import com.wq.find.circleRepAddActivity;
import com.wq.find.findSendFauilreActivity;
import com.wq.find.findTipListActivity;
import com.wq.model.User;
import com.wq.model.ecCircleModel;
import com.wq.model.ecCircleTopModel;
import com.wq.utils.DensityUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class circleListView extends ListView implements OnScrollListener {
	public static final int LOADSTATE_IDLE = 0;
	public static final int LOADSTATE_LOADING = 1;
	private float mLastY = -1;
	private View headView;
	private int headViewHeight;
	int currentScrollState;
	private float lastDownY;
	private int deltaCount;
	private ArrayList<String> data;
	private ArrayAdapter<String> adapter;
	private int currentState;
	private final int DECREASE_HEADVIEW_PADDING = 100;
	private final int LOAD_DATA = 101;
	private final int DISMISS_CIRCLE = 102;
	private ImageView circle;
	private static int cirPaddingTop = 0;
	private int CircleMarginTop;
	private IXListViewListener mListViewListener;
	private IecCircleInterface ecListener;
	private int firstVisibleItem;
	private int mTotalItemCount;
	private Context mcontext;
	// 下拉加载更多
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	// 顶部控件
	public ImageView imagetop;
	public ImageView img_logo;
	public LinearLayout layout_count;
	public TextView txt_sendWarn;// 失败条数
	public LinearLayout layout_send;
	public LinearLayout layout_count_content;// 最新留言
	public TextView txt_count;// 留言条数
	public TextView txt_wqh;
	public ecCircleTopModel topModel = new ecCircleTopModel();
	public FinalBitmap finalBitmap;
	BitmapDisplayConfig displayConfig = null;

	int ImageWidth = 0;
	int ImageHeight = 0;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DECREASE_HEADVIEW_PADDING:
				setHeadViewPaddingTop(deltaCount);
				if (headView.getBottom() >= headViewHeight)
					setCircleMargin();
				break;
			case LOAD_DATA:
				// clearCircleViewMarginTop();
				//
				break;
			case DISMISS_CIRCLE:
				int margin = msg.arg1;
				setCircleMargin(margin);
				if (margin == 0) {
					CircleAnimation.stopRotateAnmiation(circle);
				}
				break;
			}
		}

	};

	// 设置cir的指定高度
	protected void setCircleMargin(int margin) {
		MarginLayoutParams lp = (MarginLayoutParams) circle.getLayoutParams();
		lp.topMargin = margin;
		circle.setLayoutParams(lp);
	}

	// 设置默认高度
	public void setCircleMargin() {
		// TODO Auto-generated method stub
		MarginLayoutParams lp = (MarginLayoutParams) circle.getLayoutParams();
		lp.topMargin = CircleMarginTop - headView.getPaddingTop()
				+ cirPaddingTop;
		circle.setLayoutParams(lp);
	}

	// 开始动画。并加载
	public void startRefresh() {
		if (mListViewListener != null) {
			// setCircleMargin();
			// startCircleAnimation();
			decreasePadding(deltaCount);
			// loadDataForThreeSecond();
			startCircleAnimation();
			mListViewListener.onRefresh();
		}

	}

	private class DecreaseThread implements Runnable {
		private final static int TIME = 25;
		private int decrease_length;

		public DecreaseThread(int count) {
			decrease_length = count / TIME;
		}

		@Override
		public void run() {
			for (int i = 0; i < TIME; i++) {

				if (i == 24) {
					deltaCount = 0;
				} else {
					deltaCount = deltaCount - decrease_length;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}

				Message msg = Message.obtain();
				msg.what = DECREASE_HEADVIEW_PADDING;
				handler.sendMessage(msg);

			}
		}
	}

	public circleListView(Context context, ArrayList<String> dataList) {
		this(context);
		this.data = dataList;
	}

	public circleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public circleListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public circleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		mcontext = context;
		finalBitmap = FinalBitmap.create(context);
		cirPaddingTop = DensityUtil.dip2px(getContext(), 30);
		headView = LayoutInflater.from(context).inflate(R.layout.ep_circle_top,
				null);
		// 初始化顶部控件
		initHeadView();
		// 绑定事件
		addHeaderView(headView);
		circle = (ImageView) headView.findViewById(R.id.circleprogress);
		headView.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						if (headView.getMeasuredHeight() > 0) {
							headViewHeight = headView.getMeasuredHeight() + 50;
							headView.getViewTreeObserver()
									.removeOnPreDrawListener(this);
						}
						return true;
					}
				});
		setOnScrollListener(this);
		currentScrollState = OnScrollListener.SCROLL_STATE_IDLE;
		currentState = LOADSTATE_IDLE;
		firstVisibleItem = 0;
		CircleMarginTop = DensityUtil.dip2px(getContext(), 76);
		// 下拉加载跟多
		mFooterView = new XListViewFooter(context);
	}

	private void initHeadView() {
		ImageWidth = DensityUtil.dip2px(mcontext, 80);
		ImageHeight = DensityUtil.dip2px(mcontext, 80);
		displayConfig = new BitmapDisplayConfig();
		displayConfig.setLoadingBitmap(BitmapFactory.decodeResource(
				mcontext.getResources(), R.drawable.add_prompt));
		displayConfig.setLoadfailBitmap(BitmapFactory.decodeResource(
				mcontext.getResources(), R.drawable.add_prompt));
		imagetop = (ImageView) headView.findViewById(R.id.img_topbg);
		img_logo = (ImageView) headView.findViewById(R.id.img_logo);
		layout_count = (LinearLayout) headView.findViewById(R.id.layout_tip);
		txt_sendWarn = (TextView) headView.findViewById(R.id.txt_send_warn);
		layout_send = (LinearLayout) headView.findViewById(R.id.layout_send);
		layout_count_content = (LinearLayout) headView
				.findViewById(R.id.layout_tip_content);
		txt_count = (TextView) headView.findViewById(R.id.txt_tip_count);
		txt_wqh = (TextView) headView.findViewById(R.id.txt_wqh);
		txt_wqh.setText(User.wqh);
		finalBitmap.display(img_logo, topModel.getIcon(), displayConfig);
		if (!TextUtils.isEmpty(topModel.getBigImageUrl())) {
			finalBitmap.display(imagetop, topModel.getBigImageUrl());
		}
		if (topModel.getSendFaulireCount() > 0) {
			txt_sendWarn.setText(String.format(
					mcontext.getString(R.string.find_ec_send_warn),
					topModel.getSendFaulireCount() + ""));
			layout_send.setVisibility(View.VISIBLE);
		} else
			layout_send.setVisibility(View.GONE);
		// logo点击事件
		img_logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ecCircleModel ec = new ecCircleModel();
				ec.setEnterpriseId(topModel.getEnterpriseId());
				if (ecListener != null)
					ecListener.checkUeserDetailClick(ec);

			}
		});
		// 顶图点击事件
		imagetop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ecListener != null)
					ecListener.topImageClick(imagetop);
			}
		});
		// 发送失败点击事件
		layout_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(),
						circleRepAddActivity.class);
				getContext().startActivity(intent);
			}
		});
		layout_count_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(),
						findTipListActivity.class);
				getContext().startActivity(intent);
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);
				if (version > 5) {
					((Activity) mcontext).overridePendingTransition(
							R.anim.base_slide_right_in, R.anim.transition_in);
				}
				User.bArr[4] = false;
				User.tipCountArr[2] = "";
				layout_count.setVisibility(View.GONE);
			}
		});
		// 又最新评论
		if (User.bArr[4]) {
			layout_count.setVisibility(View.VISIBLE);
			txt_count.setText(String.format(
					getContext().getString(R.string.find_string_ec_tip_msg),
					User.tipCountArr[2]));

		} else {
			layout_count.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mLastY == -1) {
			mLastY = event.getRawY();
		}
		// TODO Auto-generated method stub
		float downY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastDownY = downY;
			mLastY = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:

			if (deltaCount > 0 && currentState != LOADSTATE_LOADING
					&& firstVisibleItem == 0
					&& headView.getBottom() >= headViewHeight) {

				if (mListViewListener != null)
					mListViewListener.onRefresh();
				decreasePadding(deltaCount);
				// loadDataForThreeSecond();
				startCircleAnimation();
			} else if (deltaCount > 0)
				decreasePadding(deltaCount);
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = event.getRawY() - mLastY;
			// 如果向上滑动
			if (deltaY < 0)
				stopRefresh();
			int nowDeltaCount = (int) ((downY - lastDownY) / 3.0);
			int grepDegree = nowDeltaCount - deltaCount;
			deltaCount = nowDeltaCount;
			if (deltaCount > 0 && currentState != LOADSTATE_LOADING
					&& firstVisibleItem == 0) {
				setHeadViewPaddingTop(deltaCount);
				setCircleViewStay();
				CircleAnimation.startCWAnimation(circle,
						5 * (deltaCount - grepDegree), 5 * deltaCount);
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& mEnablePullLoad) {
				startLoadMore();

			}

			break;
		}

		return super.onTouchEvent(event);
	}

	private void startCircleAnimation() {
		// TODO Auto-generated method stub
		CircleAnimation.startRotateAnimation(circle);

	}

	private void setCircleViewStay() {

		// TODO Auto-generated method stub
		if (headView.getPaddingTop() > (CircleMarginTop)) {
			MarginLayoutParams lp = (MarginLayoutParams) circle
					.getLayoutParams();
			lp.topMargin = CircleMarginTop - headView.getPaddingTop()
					+ cirPaddingTop;
			circle.setLayoutParams(lp);
		}
	}

	// 顶部开始动画
	public void startRefreshAnim() {
		currentState = LOADSTATE_LOADING;
		// data.add("New Data");
		Message msg = Message.obtain();
		msg.what = LOAD_DATA;
		handler.sendMessageDelayed(msg, 3000);
	}

	// 顶部停止动画
	public void stopRefresh() {
		Message msg = Message.obtain();
		msg.what = DISMISS_CIRCLE;
		msg.arg1 = 0;
		handler.sendMessage(msg);

	}

	public void setAdapter(ArrayAdapter<String> adapter) {
		super.setAdapter(adapter);
		this.adapter = adapter;
	}

	private void setHeadViewPaddingTop(int deltaY) {
		headView.setPadding(0, deltaY, 0, 0);
	}

	private void decreasePadding(int count) {
		Thread thread = new Thread(new DecreaseThread(count));
		thread.start();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

		switch (scrollState) {
		case SCROLL_STATE_FLING:
			currentScrollState = SCROLL_STATE_FLING;
			break;
		case SCROLL_STATE_IDLE:
			currentScrollState = SCROLL_STATE_IDLE;
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
			currentScrollState = SCROLL_STATE_TOUCH_SCROLL;
			break;

		}

	}

	/*
	 * * 是否显示上拉加载更多
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			this.removeFooterView(mFooterView);
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			this.addFooterView(mFooterView);
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	private void stopLoadMore() {
		mPullLoading = false;
		mFooterView.setState(XListViewFooter.STATE_NORMAL);

	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public void setEcListener(IecCircleInterface l) {
		this.ecListener = l;
	}

	public void onLoad() {
		stopRefresh();
		stopLoadMore();
	}

	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

}
