package com.shenhua.account.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MyStringUtils implements Serializable {

	private static final long serialVersionUID = 1383552920618631376L;
	public static String mYear, mMonth, mDay, mWeek;
	public static SharedPreferences preferences;
	public static final String SP_MONTH_LIMIT = "config";
	public static String monthStrings[] = { "一月", "二月", "三月", "四月", "五月", "六月",
			"七月", "八月", "九月", "十月", "十一月", "十二月" };
	public static String templimitsname[] = { "其它", "交通工具", "健康", "空闲", "食品饮料" };
	public static String templimitscolor[] = { "#A9B7B7", "#56ABE4", "#EB4F38",
			"#00BB9C", "#11CD6E" };

	public static String colorValues[] = { "#FF0000", "#00FF00", "#0000FF",
			"#FF00FF", "#00FFFF", "#FFFF00", "#000000", "#70DB93", "#5C3317",
			"#9F5F9F", "#B5A642", "#D9D919", "#A67D3D", "#8C7853", "#FF7F00",
			"#42426F", "#2F4F2F", "#9932CD", "#2F4F4F", "#855E42", "#D19275",
			"#238E23", "#4E2F2F", "#E9C2A6", "#8E236B", "#3232CD", "#4D4DFF",
			"#FF7F00" };
	public static String colorNames[] = { "红色", "绿色", "蓝色", "牡丹红", "青色", "黄色",
			"黑色", "海蓝", "巧克力色", "蓝紫色", "黄铜色", "亮金色", "棕色", "青铜色", "青铜色", "紫蓝色",
			"深绿", "深兰花色", "深铅灰色", "暗木色", "长石色", "森林绿", "印度红", "浅木色", "褐红色",
			"中蓝色", "霓虹蓝", "橙色" };

	public static final String INDEX_TABLE_SRZC_SR = "_sr";
	public static final String INDEX_TABLE_SRZC_ZC = "_zc";

	private static final int DECIMAL_DIGITS = 2;

	public static String API_TAKEN = "2637b7b8bfe1079bfa87070810eb22b3";
	public static String CURL = "http://api.fir.im/apps/latest/56f4f70ef2fc42664900001d?api_token=";

	/**
	 * 保存SharedPreferences数据
	 * 
	 * @param context
	 * @param 1为每月限额，0为第一次运行
	 * @param value
	 */
	public static void saveSharedpre(Context context, int t, String value) {
		preferences = context.getSharedPreferences(SP_MONTH_LIMIT,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		switch (t) {
		case 1:
			editor.putString("limit", value);
			break;
		case 0:
			editor.putString("first", value);
			break;
		case 2:
			editor.putString("key", value);
			break;
		}
		editor.commit();
	}

	/**
	 * 读取SharedPreferences数据
	 * 
	 * @param context
	 * @param int类型
	 *            ，大于1为每月限额，否则为是否第一次启动
	 * @return String型，默认返回“0”
	 */
	public static String readSharedpre(Context context, int t) {
		preferences = context.getSharedPreferences(SP_MONTH_LIMIT,
				Context.MODE_PRIVATE);
		switch (t) {
		case 1:
			return preferences.getString("limit", "0");
		case 0:
			return preferences.getString("first", "0");
		case 2:
			return preferences.getString("key", "0");
		}
		return "0";
	}

	/**
	 * 将float数据转换为两位小数点的String
	 * 
	 * @param float型
	 * @return String型 "21.20"
	 */
	public static String get2dotFloat(float a) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return decimalFormat.format(a);
	}

	/**
	 * 将String类型数据转换为浮点型数据
	 * 
	 * @param String型
	 * @return float型
	 */
	public static float getString2Float(String s) {
		return Float.parseFloat(s);
	}

	public static String getDate(String type) {
		if (type.equals("date")) {
			return mYear + "-" + mMonth + "-" + mDay;
		} else if (type.equals("week")) {
			return mWeek;
		} else if (type.equals("month")) {
			return mMonth;
		} else {
			return mDay;
		}
	}

	/**
	 * 初始化时间获取功能
	 */
	public void initDate() {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR));
		mMonth = fomatDate(String.valueOf(c.get(Calendar.MONTH) + 1));
		mDay = fomatDate(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
		mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWeek)) {
			mWeek = "星期天";
		} else if ("2".equals(mWeek)) {
			mWeek = "星期一";
		} else if ("3".equals(mWeek)) {
			mWeek = "星期二";
		} else if ("4".equals(mWeek)) {
			mWeek = "星期三";
		} else if ("5".equals(mWeek)) {
			mWeek = "星期四";
		} else if ("6".equals(mWeek)) {
			mWeek = "星期五";
		} else if ("7".equals(mWeek)) {
			mWeek = "星期六";
		}

	}

	/**
	 * 获取需要的时间
	 * 
	 * @param 类型1为yyyy
	 *            -MM-dd，2为yyyy-MM-dd HH:MM:ss，3为M，4为yyyy
	 * @return String型
	 */
	public static String getSysNowTime(int type) {
		Date now = new Date();
		java.text.SimpleDateFormat format = null;
		String formatTime = "time error";
		switch (type) {
		case 1:
			format = new SimpleDateFormat("yyyy-MM-dd");
			formatTime = format.format(now);
			break;
		case 2:
			format = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
			formatTime = format.format(now);
			break;
		case 3:
			format = new SimpleDateFormat("M");
			int i = Integer.parseInt(format.format(now));
			formatTime = monthStrings[i - 1];
			break;
		case 4:
			format = new SimpleDateFormat("yyyy");
			formatTime = format.format(now);
			break;
		case 5:
			format = new SimpleDateFormat("yyyyMMdd HHMMss");
			formatTime = format.format(now);
			break;
		case 6:
			format = new SimpleDateFormat("yyyy-MM");
			formatTime = format.format(now);
			break;
		}
		return formatTime;
	}

	public static int getFirstDayOfWeek() {
		Calendar current = Calendar.getInstance();
		current.set(Calendar.DATE, 1);
		return current.get(Calendar.DAY_OF_WEEK) - 1;
	}

	public static String showVersion(Context context, String type) {
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();
		int flags = 0;
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(packageName, flags);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "error";
		}
		if (packageInfo != null) {
			int versionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;
			if (type.equals("getcode"))
				return String.valueOf(versionCode);
			return versionName;
		}
		return "1.0";
	}

	/**
	 * 格式化时间，时期缺位补零
	 * 
	 * @param str
	 * @return String型
	 */
	public static String fomatDate(String str) {
		if (str.length() == 1) {
			return "0" + str;
		}
		return str;
	}

	/**
	 * 设置金额输入监听
	 * 
	 * @param 需要监听的edittext
	 * @return 空
	 */
	public static void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(DECIMAL_DIGITS);
				}
				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, DECIMAL_DIGITS).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

}
