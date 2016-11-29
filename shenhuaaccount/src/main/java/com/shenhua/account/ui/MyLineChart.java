package com.shenhua.account.ui;

import com.shenhua.account.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyLineChart extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder sfh;
	public static int right; // 坐标系右边距离框架左边的距离(由activity计算得出)
	public static int gapX; // 两根竖线间的间隙(由activity计算得出)
	private boolean isRunning = true;
	public static float[] Percent_Expend = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 12个支出百分比
	public static float[] Percent_Income = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 12个收入百分比
	private static String[] Percent = { "0", "10", "20", "30", "40", "50",
			"60", "70", "80", "90", "100" };// 每个月支出的金额占全年总支出的百分比
	private String[] houres = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "11", "12 (月份)" };// 一年的12个月
	private int tick = 10; // 时间间隔(ms)
	private int each_width = 20; // 两根横线间的间隙
	private int top = 10; // 坐标系顶部距离框架顶端框的距离
	private int bottom = top + each_width * 10;// 坐标系地段距离框架顶端的距离top+gapy*10=210
	private int left = 40; // 坐标系左边距离框架左边框的距离
	private int currentX;
	private int oldX;
	private String title_expend = "• 每月支出金额占全年支出百分比";
	private String title_income = "• 每月收入金额占全年收入百分比";
	private int mExpendColor;
	private int mIncomeColor;
	private int mTableColor;
	private int mTableTextColor;
	private Canvas sf_canvas;
	private Paint mTablePaint;
	private Paint mTableTextPaint;
	private Paint mExpendPaint;
	private Paint mIncomePaint;
	private Paint mPointPaint;
	private Paint mLinePaint;
	private Paint mPointPaint_e;
	private Paint mLinePaint_e;

	public MyLineChart(Context context) {
		super(context);
	}

	@Override
	public void setBackgroundColor(int color) {
		super.setBackgroundColor(color);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Log.d("draw", "绘制白色填充背景");
		RectF rect = new RectF(-10, -10, getWidth(), 300);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawRect(rect, paint);
	}

	public MyLineChart(Context context, AttributeSet atr) {
		super(context, atr);
		sfh = this.getHolder();
		sfh.addCallback(this);
		sfh.setFormat(PixelFormat.TRANSLUCENT);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(atr,
				R.styleable.MyLineChartView, 0, 0);
		mExpendColor = typedArray.getColor(
				R.styleable.MyLineChartView_expend_color, Color.RED);
		mIncomeColor = typedArray
				.getColor(R.styleable.MyLineChartView_income_color,
						Color.rgb(0, 185, 99));
		mTableColor = typedArray
				.getColor(R.styleable.MyLineChartView_table_color,
						Color.rgb(0, 214, 251));
		mTableTextColor = typedArray.getColor(
				R.styleable.MyLineChartView_tabletext_color, Color.BLUE);
		typedArray.recycle();
		initPainters();
	}

	private void initPainters() {
		// TODO 初始化画笔操作
		mTablePaint = new Paint();
		mTablePaint.setColor(mTableColor);
		mTablePaint.setAntiAlias(true);
		mTablePaint.setStrokeWidth(1);
		mTablePaint.setStyle(Style.FILL);

		mTableTextPaint = new Paint();
		mTableTextPaint.setAntiAlias(true);
		mTableTextPaint.setColor(mTableTextColor);
		mTableTextPaint.setTextSize(12f);

		mExpendPaint = new Paint();
		mExpendPaint.setColor(mExpendColor);
		mExpendPaint.setTextSize(16f);

		mIncomePaint = new Paint();
		mIncomePaint.setColor(mIncomeColor);
		mIncomePaint.setTextSize(16f);

		mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(mIncomeColor);

		mLinePaint = new Paint();
		mLinePaint.setColor(mIncomeColor);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(2);
		mLinePaint.setStyle(Style.FILL);

		mPointPaint_e = new Paint();
		mPointPaint_e.setAntiAlias(true);
		mPointPaint_e.setColor(mExpendColor);

		mLinePaint_e = new Paint();
		mLinePaint_e.setColor(mExpendColor);
		mLinePaint_e.setAntiAlias(true);
		mLinePaint_e.setStrokeWidth(2);
		mLinePaint_e.setStyle(Style.FILL);
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 在这里开始启动绘制
		isRunning = true;
		currentX = 0;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				drawChart();
				Log.i("MyLineChart", "线程完成，所有图像已经画完了");
			}
		});
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.d("surfaceChanged", "surfaceChanged");

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		isRunning = false;
		Log.d("surfaceDestroyed", "surfaceDestroyed");
	}

	protected void drawChart() {
		sf_canvas = sfh.lockCanvas();
		draw(sf_canvas);
		// 绘制坐标系
		for (int i = 0; i < 11; i++) {
			sf_canvas.drawLine(left, top + each_width * i, left + gapX * 11,
					top + each_width * i, mTablePaint);
			mTableTextPaint.setTextAlign(Align.RIGHT);
			sf_canvas.drawText("" + Percent[i], left - 2, bottom + 3 - 20 * i,
					mTableTextPaint);
		}
		for (int i = 0; i < 12; i++) {
			sf_canvas.drawLine(left + gapX * i, top, left + gapX * i, bottom,
					mTablePaint);
			mTableTextPaint.setTextAlign(Align.CENTER);
			sf_canvas.drawText(houres[i], left + gapX * i, bottom + 14,
					mTableTextPaint);
		}
		Rect mBound_expend = new Rect();
		mExpendPaint.getTextBounds(title_expend, 0, title_expend.length(),
				mBound_expend);
		sf_canvas.drawText(title_expend, getWidth() / 2 - mBound_expend.width()
				/ 2, bottom + 40, mExpendPaint);

		Rect mBound_income = new Rect();
		mIncomePaint.getTextBounds(title_income, 0, title_income.length(),
				mBound_income);
		sf_canvas.drawText(title_income, getWidth() / 2 - mBound_income.width()
				/ 2, bottom + 60, mIncomePaint);
		sfh.unlockCanvasAndPost(sf_canvas);
		drawChartLine();
	}

	private void drawChartLine() {
		while (isRunning) {
			try {
				if (currentX < getWidth()) {
					drawBrokenLine(currentX);
					currentX++;
				} else {
					System.out.println("直线绘制到了屏幕宽度，跳出while循环");
					break;
				}
				try {
					Thread.sleep(tick);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				System.out.println("绘制出错！");
			}
		}
	}

	void drawBrokenLine(int length) {
		if (length == 0)
			oldX = 0;
		Canvas canvas = sfh.lockCanvas(new Rect(oldX, 0, oldX + length,
				bottom + 30));
		drawExpend(canvas);
		drawIncome(canvas);
		sfh.unlockCanvasAndPost(canvas);
	}

	private void drawIncome(Canvas canvas) {
		// TODO 开始画收入折线
		float cx = 0f;
		float cy = 0f;
		float dx = 0f;
		float dy = 0f;
		for (int j = 0; j < Percent_Income.length - 1; j++) {
			cx = left + gapX * j;
			cy = bottom - (Percent_Income[j] * 0.1f) * each_width;
			dx = left + gapX * (j + 1);
			dy = bottom - (Percent_Income[j + 1] * 0.1f) * each_width;
			canvas.drawCircle(cx, cy, 3, mPointPaint);
			canvas.drawLine(cx, cy, dx, dy, mLinePaint);
		}
	}

	private void drawExpend(Canvas canvas) {
		// TODO 开始画支出折线
		float cx = 0f;
		float cy = 0f;
		float dx = 0f;
		float dy = 0f;
		for (int j = 0; j < Percent_Expend.length - 1; j++) {
			cx = left + gapX * j;
			cy = bottom - (Percent_Expend[j] * 0.1f) * each_width;
			dx = left + gapX * (j + 1);
			dy = bottom - (Percent_Expend[j + 1] * 0.1f) * each_width;
			canvas.drawCircle(cx, cy, 3, mPointPaint_e);
			canvas.drawLine(cx, cy, dx, dy, mLinePaint_e);
		}
	}

}
