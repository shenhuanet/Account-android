package com.shenhua.account;

import com.shenhua.account.bean.MyStringUtils;
import com.shenhua.account.dao.MyDataBase;
import com.shenhua.account.ui.MyLineChart;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_Count extends Fragment {

	private MyDataBase database;
	private View view;
	private TextView content_count_title;
	private RelativeLayout count_rl;
	private String title, year;
	private float expend, income;
	private float[] eachmonthexpend = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, eachmonthincome = { 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private int countofincome;
	static int width;
	int height;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.content_count, container, false);
			count_rl = (RelativeLayout) view.findViewById(R.id.count_rl);
			content_count_title = (TextView) view
					.findViewById(R.id.content_count_title);
			title = getContext().getString(R.string.count_title);
			year = MyStringUtils.getSysNowTime(4);
			database = new MyDataBase(getContext());
			database.open();
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		initDataAndView();
		initLineChartView();
		return view;
	}

	private void initDataAndView() {
		income = database.getSR_sum(MyStringUtils.INDEX_TABLE_SRZC_SR);
		expend = database.getSR_sum(MyStringUtils.INDEX_TABLE_SRZC_ZC);
		countofincome = database.getCount(2, "income_record", "");
		for (int i = 0; i < MyStringUtils.monthStrings.length; i++) {
			eachmonthincome[i] = Float.parseFloat(database.getSRGL(0,
					MyStringUtils.monthStrings[i]));
			eachmonthexpend[i] = Float.parseFloat(database.getSRGL(1,
					MyStringUtils.monthStrings[i]));
			if (expend != 0) {
				MyLineChart.Percent_Expend[i] = (eachmonthexpend[i] / expend) * 100;
			}
			if (income != 0) {
				MyLineChart.Percent_Income[i] = (eachmonthincome[i] / income) * 100;
			}
		}
		database.close();
		String str = String.format(title, year, expend, income, countofincome);
		content_count_title.setText(str);
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(1500);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(3000);
		animation.setInterpolator(new LinearInterpolator());
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 1f);
		controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
		count_rl.setLayoutAnimation(controller);
	}

	private void initLineChartView() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		MyLineChart.right = width - 35;
		MyLineChart.gapX = (width - 70) / 11;
	}
}
