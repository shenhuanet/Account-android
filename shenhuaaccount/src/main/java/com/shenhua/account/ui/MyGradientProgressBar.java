package com.shenhua.account.ui;

import com.shenhua.account.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public class MyGradientProgressBar extends View {

	public Context mContext;
	private int mMax = 100;
	private int mProgress = 0;
	private int mFillColor;
	private int mBackgroundColor;
	private float mFillHeight;
	private float mBackgroundHeight;
	private final int default_fill_color = Color.rgb(66, 145, 241);
	private final int default_Background_color = Color.rgb(204, 204, 204);
	private final float default_fill_height;
	private final float default_background_height;
	private static final String INSTANCE_STATE = "saved_instance";
	private static final String INSTANCE_REACHED_BAR_HEIGHT = "fill_height";
	private static final String INSTANCE_REACHED_BAR_COLOR = "background_color";
	private static final String INSTANCE_UNREACHED_BAR_HEIGHT = "fill_height";
	private static final String INSTANCE_UNREACHED_BAR_COLOR = "background_color";
	private static final String INSTANCE_MAX = "max";
	private static final String INSTANCE_PROGRESS = "progress";
	private Paint mFillPaint;
	private Paint mBackgroundPaint;
	private RectF mFillRectF = new RectF(0, 0, 0, 0);
	private RectF mBackgroundRectF = new RectF(0, 0, 0, 0);
	private float mOffset;
	private boolean mDrawBackground = true;
	private boolean mDrawFill = true;

	public MyGradientProgressBar(Context context) {
		this(context, null);
	}

	public MyGradientProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyGradientProgressBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
		default_fill_height = dp2px(1.5f);
		default_background_height = dp2px(1.0f);
		final TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.NumberProgressBar, defStyleAttr, 0);
		mFillColor = attributes.getColor(
				R.styleable.NumberProgressBar_progress_reached_color,
				default_fill_color);
		mBackgroundColor = attributes.getColor(
				R.styleable.NumberProgressBar_progress_unreached_color,
				default_Background_color);
		mFillHeight = attributes.getDimension(
				R.styleable.NumberProgressBar_progress_reached_bar_height,
				default_fill_height);
		mBackgroundHeight = attributes.getDimension(
				R.styleable.NumberProgressBar_progress_unreached_bar_height,
				default_background_height);
		setProgress(attributes
				.getInt(R.styleable.NumberProgressBar_progress, 0));
		setMax(attributes.getInt(R.styleable.NumberProgressBar_max, 100));
		attributes.recycle();
		initializePainters();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measure(widthMeasureSpec, true),
				measure(heightMeasureSpec, false));
	}

	private int measure(int measureSpec, boolean isWidth) {
		int result;
		int mode = MeasureSpec.getMode(measureSpec);
		int size = MeasureSpec.getSize(measureSpec);
		int padding = isWidth ? getPaddingLeft() + getPaddingRight()
				: getPaddingTop() + getPaddingBottom();
		if (mode == MeasureSpec.EXACTLY) {
			result = size;
		} else {
			result = isWidth ? getSuggestedMinimumWidth()
					: getSuggestedMinimumHeight();
			result += padding;
			if (mode == MeasureSpec.AT_MOST) {
				if (isWidth) {
					result = Math.max(result, size);
				} else {
					result = Math.min(result, size);
				}
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		calculateDrawRectF();
		if (mDrawBackground) {
			canvas.drawRect(mBackgroundRectF, mBackgroundPaint);
		}
		if (mDrawFill) {
			canvas.drawRect(mFillRectF, mFillPaint);
		}
	}

	private void initializePainters() {
		mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mFillPaint.setColor(mFillColor);
		mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBackgroundPaint.setColor(mBackgroundColor);

	}

	private void calculateDrawRectF() {
		if (getProgress() == 0) {
			mDrawFill = false;
		} else {
			mDrawFill = true;
			mFillRectF.left = getPaddingLeft();
			mFillRectF.top = getHeight() / 2.0f - mFillHeight / 2.0f;
			mFillRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight())
					/ (getMax() * 1.0f)
					* getProgress()
					- mOffset
					+ getPaddingLeft();
			mFillRectF.bottom = getHeight() / 2.0f + mFillHeight / 2.0f;
		}
		float unreachBarStart = mOffset;
		if (unreachBarStart >= getWidth() - getPaddingRight()) {
			mDrawBackground = false;
		} else {
			mDrawBackground = true;
			mBackgroundRectF.left = unreachBarStart;
			mBackgroundRectF.right = getWidth() - getPaddingRight();
			mBackgroundRectF.top = getHeight() / 2.0f + -mBackgroundHeight
					/ 2.0f;
			mBackgroundRectF.bottom = getHeight() / 2.0f + mBackgroundHeight
					/ 2.0f;
		}
	}

	public int getUnreachedBarColor() {
		return mBackgroundColor;
	}

	public int getReachedBarColor() {
		return mFillColor;
	}

	public int getProgress() {
		return mProgress;
	}

	public int getMax() {
		return mMax;
	}

	public float getReachedBarHeight() {
		return mFillHeight;
	}

	public float getUnreachedBarHeight() {
		return mBackgroundHeight;
	}

	public void setUnreachedBarColor(int BarColor) {
		this.mBackgroundColor = BarColor;
		mBackgroundPaint.setColor(mFillColor);
		invalidate();
	}

	public void setReachedBarColor(int ProgressColor) {
		this.mFillColor = ProgressColor;
		mFillPaint.setColor(mFillColor);
		invalidate();
	}

	public void setMax(int Max) {
		if (Max > 0) {
			this.mMax = Max;
			invalidate();
		}
	}

	public void incrementProgressBy(int by) {
		if (by > 0) {
			setProgress(getProgress() + by);
		}
	}

	public void setProgress(int Progress) {
		if (Progress <= getMax() && Progress >= 0) {
			this.mProgress = Progress;
			invalidate();
		}
		if (Progress > getMax()) {
			this.mProgress = 100;
			invalidate();
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, getReachedBarHeight());
		bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, getUnreachedBarHeight());
		bundle.putInt(INSTANCE_REACHED_BAR_COLOR, getReachedBarColor());
		bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR, getUnreachedBarColor());
		bundle.putInt(INSTANCE_MAX, getMax());
		bundle.putInt(INSTANCE_PROGRESS, getProgress());
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			mFillHeight = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT);
			mBackgroundHeight = bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT);
			mFillColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR);
			mBackgroundColor = bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR);
			initializePainters();
			setMax(bundle.getInt(INSTANCE_MAX));
			setProgress(bundle.getInt(INSTANCE_PROGRESS));
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
			return;
		}
		super.onRestoreInstanceState(state);
	}

	public float dp2px(float dp) {
		final float scale = getResources().getDisplayMetrics().density;
		return dp * scale + 0.5f;
	}

	public float sp2px(float sp) {
		final float scale = getResources().getDisplayMetrics().scaledDensity;
		return sp * scale;
	}

	@Override
	public boolean hasFocusable() {
		return false;
	}

}
