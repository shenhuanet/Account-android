package com.shenhua.account.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.shenhua.account.bean.LimitData;
import com.shenhua.account.dao.MyDataBase;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyPiechart extends View {

	public float[] mExplain = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0 };// 起始角度为0
	private static final float Explain_INC = 1;
	private Paint[] mPaints;
	private Paint bPaint;
	private RectF mBigOval;
	private ArrayList<HashMap<String, Object>> lists = new ArrayList<HashMap<String, Object>>();
	private List<LimitData> mDatas = new ArrayList<LimitData>();
	private HashMap<String, Object> map;
	public float[] eachPros;// 每一个块所占的角度，总和为360

	public MyPiechart(Context context) {
		super(context);
		initDatas(context);
		initPainters();
	}

	private void initDatas(Context context) {
		MyDataBase dataBase = new MyDataBase(context);
		dataBase.open();
		float count = 1f;
		mDatas.clear();
		mDatas = dataBase.getLimits();
		count = dataBase.getUsedCountFromLimits();
		for (int i = 0; i < mDatas.size(); i++) {
			map = new HashMap<String, Object>();
			float us = Float.parseFloat(mDatas.get(i).getFenpei_use());
			map.put("pro", (us / count) * 360);
			map.put("color", mDatas.get(i).getColor().toString());
			lists.add(map);
		}
		dataBase.close();
	}

	public MyPiechart(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDatas(context);
		initPainters();
	}

	private void initPainters() {
		mPaints = new Paint[lists.size()];
		eachPros = new float[lists.size()];
		for (int i = 0; i < lists.size(); i++) {
			mPaints[i] = new Paint();
			mPaints[i].setAntiAlias(true);
			mPaints[i].setStyle(Paint.Style.FILL);
		}
		bPaint = new Paint();
		bPaint.setColor(Color.rgb(245, 252, 249));
	}

	private void initDraw() {
		mBigOval = new RectF(getPaddingLeft(), getPaddingTop(), getWidth()
				- getPaddingRight(), getHeight() - getPaddingBottom());
		for (int i = 0; i < lists.size(); i++) {
			eachPros[i] = (float) lists.get(i).get("pro");
			mPaints[i].setColor(Color.parseColor(lists.get(i).get("color")
					.toString()));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		initDraw();
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawArc(mBigOval, 0, 360, true, bPaint);
		float start = 0;
		for (int i = 0; i < lists.size(); i++) {
			canvas.drawArc(mBigOval, start, mExplain[i], true, mPaints[i]);
			start += eachPros[i];
			if (mExplain[i] < eachPros[i]) {
				mExplain[i] += Explain_INC;
				invalidate();
			}
		}
	}
}
