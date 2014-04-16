package com.wq.UI;


import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.View;

/**
 * @author Administrator
 * 
 */
/**
 * @author Administrator
 * 
 */
public class chartEngine {

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	private XYSeries mCurrentSeries;

	private XYSeriesRenderer mCurrentRenderer;
	// private int Flag = 1;
	public GraphicalView mChartView;
	private Context context;
	ArrayList<String> countList = new ArrayList<String>();
	ArrayList<String> dateList = new ArrayList<String>();

	public chartEngine(Context context, ArrayList<String> count,
			ArrayList<String> date) {
		this.context = context;
		this.countList = count;// 访问量
		this.dateList = date;// 访问日期
		init();

	}

	private void init() {
		mRenderer.setApplyBackgroundColor(false);// 设置是否显示背景色
		mRenderer.setBackgroundColor(Color.WHITE);// 画布背景
		mRenderer.setAxisTitleTextSize(30);// 设置轴标题文字大小
		mRenderer.setChartTitleTextSize(20);// 设置整个图表标题文字大小
		mRenderer.setLabelsTextSize(25);// 设置刻度显示文字的大小
		// mRenderer.setLegendTextSize(30);// 图例文字大小,就是用户访问量文字的大小
		// mRenderer.setLegendHeight(180);
		mRenderer.setXLabelsColor(Color.rgb(73, 169, 229));// 设置x轴文字颜色
		mRenderer.setTextTypeface(null, Typeface.BOLD);
		mRenderer.setYLabelsColor(0, Color.rgb(73, 169, 229));// 设置Y轴的文字颜色
		mRenderer.setAxesColor(Color.rgb(73, 169, 229));// Y轴的颜色
		mRenderer.setMarginsColor(Color.WHITE);// 设置边距的颜色
		mRenderer.setPanEnabled(false, true);// 是否可以拖动
		mRenderer.setZoomEnabled(false);// 是否zoom可以拖动
	
		mRenderer.setXLabels(0);// 设置x显示的标签
		// mRenderer.setMargins(new int[] { 20, 30, 15, 10 }); // 图形4边距
		mRenderer.setScale((float) 0.8);
		mRenderer.setXAxisMin(0);// 设置x轴的起点
		mRenderer.setYAxisMin(0);// 设置Y轴的起点
		// mRenderer.setPanLimits(new double[] { 1, 31, 0, 0 });
		mRenderer.setXLabelsAngle(-20); // 设置X轴标签倾斜角度(clockwise degree)
		 mRenderer.setYLabelsPadding(13);// 设置y轴的文字离轴的距离
		 mRenderer.setYLabelsVerticalPadding(-8);
		// mRenderer.
		mRenderer.setZoomEnabled(false, false);// 这样设置，xy轴就无法缩放了
		mRenderer.setZoomEnabled(false);
		mRenderer.setMargins(new int[] { 20, 50, 10, 0 });// 设置图表的外边框。上左下右
		mRenderer.setZoomButtonsVisible(false);// 设置是否显示放大缩小按钮
		mRenderer.setPointSize(8);// 设置点的大小
		mRenderer.setClickEnabled(false);
		mRenderer.setShowLegend(false);// 隐藏用户访问量文字
		mRenderer.setShowGridX(true);
		mRenderer.setGridColor(Color.rgb(73, 169, 229));// 设置网格线的颜色
		// mRenderer.setShowCustomTextGrid(true);
		mRenderer.setPanLimits(new double[] { 0, 0, 0, 0 });// 设置拉动的范围
		// 此处可以设置，获取到的y周的最大值作为刚开始显示区域

		// the button that handles the new series of data creation
		String seriesTitle = "";
		// create a new series of data
		XYSeries series = new XYSeries(seriesTitle);// 折线设置
		mDataset.addSeries(series);
		mCurrentSeries = series;
		// create a new renderer for the new series
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		renderer.setColor(Color.rgb(73, 169, 229));// 线的颜色
		// renderer.setc
		renderer.setChartValuesTextSize(30);// 设置折线文字的大小
		renderer.setLineWidth((float) 5.0);// 线的初细
		renderer.setShowLegendItem(true);
		renderer.setChartValuesSpacing(18);// 描述文字与点之间的距离
		renderer.setChartValuesTextAlign(Align.RIGHT);

		// set some renderer properties
		renderer.setPointStyle(PointStyle.CIRCLE);// 设置点的类型//此处是圆形
		renderer.setFillPoints(true);// 是否实心

		renderer.setDisplayChartValues(true);// 是否显示点的值
		renderer.setDisplayChartValuesDistance(30);
		mCurrentRenderer = renderer;
		GetSevenDay();
		InitDataSet();
		mRenderer.setRange(new double[] {
				0d,
				8d,
				0d,
				mCurrentSeries.getMaxY()
						+ getRangeClass(mCurrentSeries.getMaxY()) }); // 设置chart的视图范围.这样就可以控制x轴和y周刚开始显示的点数，就是scale

	}

	/**
	 * Activity onSaveInstanceState时调用此方法
	 * 
	 * @param outState
	 */
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("dataset", mDataset);
		outState.putSerializable("renderer", mRenderer);
		outState.putSerializable("current_series", mCurrentSeries);
		outState.putSerializable("current_renderer", mCurrentRenderer);
	}

	/**
	 * Activity onRestoreInstanceState时调用此方法
	 * 
	 * @param savedState
	 */
	public void onRestoreInstanceState(Bundle savedState) {
		mDataset = (XYMultipleSeriesDataset) savedState
				.getSerializable("dataset");
		mRenderer = (XYMultipleSeriesRenderer) savedState
				.getSerializable("renderer");
		mCurrentSeries = (XYSeries) savedState
				.getSerializable("current_series");
		mCurrentRenderer = (XYSeriesRenderer) savedState
				.getSerializable("current_renderer");
	}

	public View GetChartView() {
		mChartView = ChartFactory
				.getLineChartView(context, mDataset, mRenderer);

		// enable the chart click events
		// mRenderer.setClickEnabled(true);
		// mRenderer.setSelectableBuffer(10);
		// 图表的点击事件

		return mChartView;
	}

	/**
	 * 初始化数据集
	 * 
	 * @param Flag
	 *            flag=0==>按月统计，flag=1==>按周统计，flag=2==>按日统计
	 */
	public void InitDataSet() {
		mCurrentSeries.clear();

		mCurrentSeries
				.add(0, 0);
		for (int i = 1; i <= this.countList.size(); i++) {

			mCurrentSeries
					.add(i, Double.parseDouble(this.countList.get(i - 1)));

		}

		if (mChartView != null)
			mChartView.repaint();

	}

	private int getRangeClass(double RangeValue) {

		if (RangeValue > 0 && RangeValue <= 10)
			return 1;
		else if (RangeValue > 10 && RangeValue <= 100) {
			return 10;
		} else if (RangeValue > 100 && RangeValue <= 1000) {
			return 100;
		} else if (RangeValue > 1000 && RangeValue <= 10000) {
			return 1000;
		} else if (RangeValue > 10000 && RangeValue <= 100000) {
			return 10000;
		} else
			return 0;
	}

	/**
	 * 获取 当前时间的前7天
	 */
	private void GetSevenDay() {
		// int j = 8;
		mRenderer.addXTextLabel(0, "0");
		for (int i = 1; i <= this.dateList.size(); i++) {
			try {
				mRenderer.addXTextLabel(i, this.dateList.get(i - 1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * 刷新控件
	 */
	public void mrepaint() {
		if (mChartView != null)
			mChartView.repaint();
	}
}
