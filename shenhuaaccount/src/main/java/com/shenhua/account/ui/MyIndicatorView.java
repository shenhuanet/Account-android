package com.shenhua.account.ui;

import java.util.Calendar;
import java.util.Locale;
import com.shenhua.account.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyIndicatorView extends View {

	private float mFillWidth;
	private float mMaxRate;
	private float mEachMaxRate;
	private float mRate;
	private int mBorderColor;
	private int mIndicatorColor;
	private int daysOfMonth = 30;
	private float topPadding = dp2px(50.0f);
	private float leftPadding = dp2px(15.0f);
	private float rightPadding = dp2px(15.0f);
	private float textLeft;
	private RectF mFillBounds;
	private RectF mBorderBounds;
	private Paint mFillPaint;
	private Paint mBorderPaint;
	private Paint trianglePaint;
	private Paint textPaint;
	private Path triangle;
	private String mTitleText = "今天";

	public MyIndicatorView(Context context) {
		this(context, null);
	}

	public MyIndicatorView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.MyIndicatorView, defStyle, 0);
		mRate = a.getInt(R.styleable.MyIndicatorView_rate, 0);
		mBorderColor = a.getColor(R.styleable.MyIndicatorView_border_color,
				Color.RED);
		mIndicatorColor = a.getColor(
				R.styleable.MyIndicatorView_indicator_color, Color.BLACK);
		a.recycle();
		initPainters();

	}

	private void initPainters() {
		mFillPaint = new Paint();
		mFillBounds = new RectF();
		mFillPaint.setColor(0x3FCDCDCD);
		mBorderPaint = new Paint();
		mBorderBounds = new RectF();
		mBorderPaint.setColor(mBorderColor);
		trianglePaint = new Paint();
		trianglePaint.setColor(mIndicatorColor);
		textPaint = new Paint();
		textPaint.setTextSize(18);
		textPaint.setColor(mIndicatorColor);

	}

	private void caculateRectF() {
		daysOfMonth = getDaysOfMonth(1);
		mRate = getDaysOfMonth(2);
		mMaxRate = getWidth() - leftPadding - rightPadding;// 739.0
		mEachMaxRate = mMaxRate / daysOfMonth;// 24.633333
		Log.d("mRate", String.valueOf(mRate));
		mFillWidth = mRate * mEachMaxRate;
		float fillRight = mFillWidth + leftPadding - 1;
		textLeft = mFillWidth + leftPadding - 20;
		mFillBounds.left = 0;
		mFillBounds.top = 0;
		mFillBounds.right = fillRight;
		mFillBounds.bottom = getHeight();
		mBorderBounds.left = fillRight - 0.4f;
		mBorderBounds.top = 0;
		mBorderBounds.right = fillRight + 0.4f;
		mBorderBounds.bottom = getHeight();
		triangle = new Path();
		triangle.moveTo(fillRight, topPadding);
		triangle.lineTo(fillRight + 21, topPadding - 30);
		triangle.lineTo(fillRight + -21, topPadding - 30);
		triangle.close();
	}

	private int getDaysOfMonth(int a) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		if (a == 1) {
			return calendar.getActualMaximum(Calendar.DATE);
		} else {
			return calendar.get(Calendar.DAY_OF_MONTH);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		caculateRectF();
		canvas.drawRect(mFillBounds, mFillPaint);
		canvas.drawRect(mBorderBounds, mBorderPaint);
		canvas.drawPath(triangle, trianglePaint);
		canvas.drawText(mTitleText, textLeft, topPadding - 40, textPaint);

	}

	public float dp2px(float dp) {
		final float scale = getResources().getDisplayMetrics().density;
		return dp * scale + 0.5f;
	}
}
