package com.shenhua.account.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.shenhua.account.bean.GdkzData;
import com.shenhua.account.bean.LimitData;
import com.shenhua.account.bean.AccountData;
import com.shenhua.account.bean.MyStringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBase {

	private ArrayList<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	private Map<String, Object> map;

	public static final String DATABASE_NAME = "myAccount.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME_ACCOUNT = "accounts";
	public static final String TABLE_NAME_GROUPS = "groups";
	public static final String TABLE_NAME_LIMIT = "limits";
	public static final String TABLE_NAME_INCOME_EXPEND = "srzcs";
	public static final String TABLE_NAME_INCOME_RECORD = "income_record";

	public float count = 1.0f;

	public static final String SQL_CREAT_TABLE_ACCOUNTS = "create table "
			+ TABLE_NAME_ACCOUNT + "("
			+ "_id INTEGER primary key autoincrement," + "_type TEXT,"
			+ "_money TEXT," + "_time TEXT," + "_month TEXT," + "_week TEXT,"
			+ "_mark TEXT," + "_other TEXT" + ");";
	public static final String SQL_CREAT_TABLE_GROUPS = "create table "
			+ TABLE_NAME_GROUPS + "("
			+ "_id INTEGER primary key autoincrement," + "_month TEXT,"
			+ "_other TEXT" + ");";
	public static final String SQL_CREAT_TABLE_LIMITS = "create table "
			+ TABLE_NAME_LIMIT + "(" + "_id INTEGER primary key autoincrement,"
			+ "_type TEXT," + "_used TEXT," + "_progress TEXT,"
			+ "_limit TEXT," + "_color TEXT" + ");";
	public static final String SQL_CREAT_TABLE_INCOME_EXPEND = "create table "
			+ TABLE_NAME_INCOME_EXPEND + "("
			+ "_id INTEGER primary key autoincrement," + "_month TEXT,"
			+ "_sr TEXT," + "_zc TEXT," + "_other TEXT" + ");";
	public static final String SQL_CREAT_TABLE_INCOME_RECORD = "create table "
			+ TABLE_NAME_INCOME_RECORD + "("
			+ "_id INTEGER primary key autoincrement," + "_time TEXT,"
			+ "_income TEXT, _detail TEXT" + ");";

	private String account[] = { "_id", "_type", "_money", "_time", "_month",
			"_week", "_mark", "_other" };
	private String groups[] = { "_id", "_month", "_other" };
	private String limits[] = { "_id", "_type", "_used", "_progress", "_limit",
			"_color" };

	private Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase mSqLiteDatabase = null;

	/**
	 * 打开数据库getWritable
	 */
	// TODO 数据库打开
	public void open() {
		dbHelper = new DatabaseHelper(context);
		mSqLiteDatabase = dbHelper.getWritableDatabase();
	}

	/**
	 * 关闭数据库
	 */
	// TODO 数据库关闭
	public void close() {
		dbHelper.close();
	}

	/**
	 * 删除整个数据库
	 * 
	 * @param context
	 * @return
	 */
	public boolean deleteDB() {
		return dbHelper.deleteDatabase(context);
	}

	// TODO 获取数据方法
	/**
	 * 获取limits表的总记录数
	 * 
	 * @return 总记录数
	 */
	public int getLimitsCount() {
		int count = 0;
		Cursor cursor = mSqLiteDatabase.rawQuery(
				"Select count(*) from limits;", null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}

	/**
	 * 获取limits表的记录
	 * 
	 * @return LimitData记录
	 */
	public List<LimitData> getLimits() {
		Cursor limitcursor = mSqLiteDatabase.rawQuery("select * from limits;",
				null);
		List<LimitData> datas = new ArrayList<LimitData>();
		datas.clear();
		while (limitcursor.moveToNext()) {
			LimitData data = new LimitData(limitcursor.getString(1),
					limitcursor.getString(2), limitcursor.getString(3),
					limitcursor.getString(4), limitcursor.getString(2),
					limitcursor.getString(5));
			datas.add(data);
		}
		limitcursor.close();
		return datas;
	}

	/**
	 * 获取记录总数
	 * 
	 * @param t
	 * @param tabName
	 * @param type
	 * @return
	 */
	public int getCount(int t, String tabName, String type) {
		int c = 0;
		String sql = "";
		switch (t) {
		case 1:
			sql = "select " + type + " from " + tabName + ";";
			Cursor c1 = mSqLiteDatabase.rawQuery(sql, null);
			while (c1.moveToNext()) {
				if (!(c1.getString(0).equals("0"))) {
					c += 1;
				}
			}
			c1.close();
			return c;
		case 2:
			sql = "Select count(*) from " + tabName + ";";
			Cursor c2 = mSqLiteDatabase.rawQuery(sql, null);
			while (c2.moveToNext()) {
				c = c2.getInt(0);
			}
			c2.close();
			return c;
		case 3:
			sql = "select count(*) from " + tabName + " where _month='" + type
					+ "';";
			Cursor c3 = mSqLiteDatabase.rawQuery(sql, null);
			if (c3.moveToFirst()) {
				c = c3.getInt(0);
			}
			c3.close();
			return c;
		}
		return c;
	}

	/**
	 * 获取limits表的记录
	 * 
	 * @return LimitData记录
	 */
	public List<GdkzData> getLimitsDatas() {
		Cursor limitcursor = mSqLiteDatabase.rawQuery("select * from limits;",
				null);
		List<GdkzData> datas = new ArrayList<GdkzData>();
		datas.clear();
		while (limitcursor.moveToNext()) {
			GdkzData data = new GdkzData(limitcursor.getString(1),
					limitcursor.getString(4), limitcursor.getString(5));
			datas.add(data);
		}
		limitcursor.close();
		return datas;
	}

	/**
	 * 获取部分limits表的记录
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getPartLimitsDatas() {
		Cursor cursor = mSqLiteDatabase.rawQuery("select * from limits;", null);
		ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
		while (cursor.moveToNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", cursor.getString(1));
			map.put("color", cursor.getString(5));
			lists.add(map);
		}
		cursor.close();
		return lists;
	}

	/**
	 * 获取首页展示时的数据
	 * 
	 * @return
	 */
	public ArrayList<Map<String, Object>> getHomeData() {
		int pro = 0;
		String sql_count = "SELECT SUM(_used) AS OrderTotal FROM limits;";
		Cursor cursor_count = mSqLiteDatabase.rawQuery(sql_count, null);
		while (cursor_count.moveToNext()) {
			count = cursor_count.getFloat(0);
		}
		String sql = "SELECT * FROM limits WHERE _used is not '0';";
		Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			map = new HashMap<String, Object>();
			String used = cursor.getString(2);
			if (used != null) {
				pro = (int) ((Float.parseFloat(used) / count) * 100);
				map.put("title", cursor.getString(1));
				map.put("used", used);
				map.put("pro", pro);
				map.put("color", cursor.getString(5));
				datas.add(map);
			}
		}
		cursor_count.close();
		cursor.close();
		return datas;
	}

	/**
	 * 获取进度或限额
	 * 
	 * @param a
	 * @param type
	 * @return
	 */
	public String getProORLimit(int a, String type) {
		String s = "0";
		Cursor cursor = mSqLiteDatabase.query(TABLE_NAME_LIMIT, new String[] {
				limits[2], limits[3], limits[4] }, limits[1] + "=?",
				new String[] { type }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			s = cursor.getString(a);
		} else {
			// System.out.println("crusor is null");
		}
		cursor.close();
		if (s == null) {
			return "0";
		} else {
			return s;
		}

	}

	/**
	 * 获取收入表的数据
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getIncomes() {
		Cursor cursor = mSqLiteDatabase.rawQuery("select * from "
				+ TABLE_NAME_INCOME_RECORD + ";", null);
		ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
		while (cursor.moveToNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("_money", cursor.getString(2));
			map.put("_time", cursor.getString(1));
			map.put("_detail", cursor.getString(3));
			lists.add(map);
		}
		cursor.close();
		return lists;
	}

	/**
	 * 获取srzc表中数据
	 * 
	 * @param month
	 * @param t
	 * @return
	 */
	public HashMap<String, String> getValueFromGLSR(String month, String t) {
		HashMap<String, String> map = new HashMap<String, String>();
		Cursor cursor = mSqLiteDatabase.query(TABLE_NAME_INCOME_EXPEND,
				new String[] { "_sr", "_zc", "_other" }, "_month" + "=?",
				new String[] { month }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			map.put("sr", cursor.getString(0));
			map.put("zc", cursor.getString(1));
		} else {
			return null;
		}
		cursor.close();
		return map;

	}

	/**
	 * 获取所用金额的总金额从limits表
	 * 
	 * @param s
	 * @return
	 */
	public float getUsedCountFromLimits() {
		float sum = 0;
		String sql = "SELECT SUM(_used) AS sum from limits";
		Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			sum = cursor.getFloat((cursor.getColumnIndex("sum")));
		}
		if (cursor != null) {
			cursor.close();
		}
		return sum;
	}

	/**
	 * 获取收入支出总金额
	 * 
	 * @param s
	 * @return
	 */
	public float getSR_sum(String s) {
		float sum = 0;
		String sql = "SELECT SUM(" + s + ") AS sum from "
				+ TABLE_NAME_INCOME_EXPEND;
		Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			sum = cursor.getFloat((cursor.getColumnIndex("sum")));
		}
		if (cursor != null) {
			cursor.close();
		}
		return sum;
	}

	/**
	 * 获取收入支出数据
	 * 
	 * @param a
	 * @param month
	 * @return
	 */
	public String getSRGL(int a, String month) {
		String s = "0";
		Cursor cursor = mSqLiteDatabase.query(TABLE_NAME_INCOME_EXPEND,
				new String[] { "_sr", "_zc", "_other" }, "_month" + "=?",
				new String[] { month }, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			s = cursor.getString(a);
		} else {
		}
		cursor.close();
		if (s == null) {
			return "0";
		} else {
			return s;
		}

	}

	/**
	 * 获取一个Cursor
	 * 
	 * @param 类型1为account
	 *            ，否则为groups
	 * @param _groupName
	 * @return
	 */
	public Cursor getAccountByGroups(int t, String _groupName) {
		if (t == 1) {
			return mSqLiteDatabase.query(TABLE_NAME_ACCOUNT, account,
					"_month='" + _groupName + "'", null, null, null, null);
		} else {
			return mSqLiteDatabase.query(TABLE_NAME_GROUPS, groups, null, null,
					null, null, null);
		}
	}

	// TODO 插入数据方法
	/**
	 * 向limits表中插入颜色值
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public long inserDataToLimitColor(String type, String value) {
		ContentValues content = new ContentValues();
		content.put(limits[1], type);
		content.put(limits[2], "0");
		content.put(limits[3], "0");
		content.put(limits[4], "0");
		content.put(limits[5], value);
		return mSqLiteDatabase.insert(TABLE_NAME_LIMIT, null, content);
	}

	/**
	 * 向groups表中插入数据
	 * 
	 * @param groupName
	 * @return
	 */
	public long inserDataToGroup(String groupName) {
		String formattime = MyStringUtils.getSysNowTime(2);
		ContentValues content = new ContentValues();
		content.put(groups[1], groupName);
		content.put(groups[2], formattime);
		return mSqLiteDatabase.insert(TABLE_NAME_GROUPS, null, content);
	}

	/**
	 * 向accounts表中插入数据
	 * 
	 * @param myData
	 * @return
	 */
	public long inserDataToAccount(AccountData myData) {
		String formatTime = MyStringUtils.getSysNowTime(2);
		ContentValues content = new ContentValues();
		content.put(account[1], myData.getType());
		content.put(account[2], myData.getMoney());
		content.put(account[3], myData.getTime());
		content.put(account[4], myData.getMonth());
		content.put(account[5], myData.getWeek());
		content.put(account[6], myData.getMark());
		content.put(account[7], formatTime);
		return mSqLiteDatabase.insert(TABLE_NAME_ACCOUNT, null, content);
	}

	/**
	 * 想income记录表中加入数据
	 * 
	 * @param myData
	 * @return
	 */
	public long inserDataToIncomeRecord(HashMap<String, String> myData) {
		String formatTime = MyStringUtils.getSysNowTime(1);
		ContentValues content = new ContentValues();
		content.put("_time", formatTime);
		content.put("_income", myData.get("_income"));
		content.put("_detail", myData.get("_detail"));
		return mSqLiteDatabase.insert(TABLE_NAME_INCOME_RECORD, null, content);
	}

	// TODO 更新数据方法
	/**
	 * 更新limits表中的used值
	 * 
	 * @param type
	 * @param used
	 * @return
	 */
	public int updateDataTolimitsUsed(String type, float used) {
		ContentValues content = new ContentValues();
		content.put("_type", type);
		content.put("_used", MyStringUtils.get2dotFloat(used));
		return mSqLiteDatabase.update(TABLE_NAME_LIMIT, content, "_type=?",
				new String[] { type });
	}

	/**
	 * 更新limits表中的limit值
	 * 
	 * @param type
	 * @param used
	 * @return
	 */
	public int updateDataTolimitsLimit(String type, String limit, String use) {
		float l = MyStringUtils.getString2Float(limit);
		float u = MyStringUtils.getString2Float(use);
		if (l != 0) {
			int pro = (int) ((u / l) * 100);
			ContentValues content = new ContentValues();
			content.put("_type", type);
			content.put("_progress", Integer.toString(pro));
			content.put("_limit", limit);
			return mSqLiteDatabase.update(TABLE_NAME_LIMIT, content, "_type=?",
					new String[] { type });
		} else {
			return 0;
		}

	}

	/**
	 * 更新srzc表数据
	 * 
	 * @param month
	 * @param t
	 * @param value
	 * @return
	 */
	public int updateGLSR(String month, String t, String value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("_month", month);
		contentValues.put(t, value);
		return mSqLiteDatabase.update(TABLE_NAME_INCOME_EXPEND, contentValues,
				"_month" + "=?", new String[] { month });
	}

	// TODO 删除表数据方法
	/**
	 * 删除accounts表中数据
	 * 
	 * @param money
	 * @return
	 */
	public int deleteData(String tabName, String column, String value) {
		return mSqLiteDatabase.delete(tabName, column + "='" + value + "'",
				null);
	}

	/**
	 * 将limits表中的某些数值置零
	 * 
	 * @return
	 */
	public void setDataToZero() {
		String sql = "update limits set _used = '0'";
		String sql2 = "update limits set _progress = '0'";
		Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
		cursor.moveToNext();
		Cursor cursor2 = mSqLiteDatabase.rawQuery(sql2, null);
		cursor2.moveToNext();
		cursor.close();
		cursor2.close();
	}

	/**
	 * 判断表中是否存在某项
	 * 
	 * @param 表名
	 * @param 列名
	 * @param 名称
	 * @return
	 */
	public boolean isNameExist(String tabName, String co, String name) {
		String sql = "select * from " + tabName + " where " + co + "='" + name
				+ "'";
		Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}

	}

	public Cursor rawQuery(String sql, String[] arg) {
		return mSqLiteDatabase.rawQuery(sql, arg);
	}

	public MyDataBase(Context context) {
		this.context = context;
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO onCreate
			db.execSQL(SQL_CREAT_TABLE_ACCOUNTS);
			db.execSQL(SQL_CREAT_TABLE_GROUPS);
			db.execSQL(SQL_CREAT_TABLE_LIMITS);
			db.execSQL(SQL_CREAT_TABLE_INCOME_EXPEND);
			db.execSQL(SQL_CREAT_TABLE_INCOME_RECORD);

			for (int i = 0; i < MyStringUtils.templimitsname.length; i++) {
				String sql = "insert into limits values(?,?,null,null,\"0\",?)";
				Object[] bindArgs = { i + 1, MyStringUtils.templimitsname[i],
						MyStringUtils.templimitscolor[i] };
				db.execSQL(sql, bindArgs);
			}
			for (int i = 0; i < MyStringUtils.monthStrings.length; i++) {
				String sql = "insert into srzcs values(?,?,\"0\",\"0\",\"0\")";
				Object[] bindArgs = { i + 1, MyStringUtils.monthStrings[i] };
				db.execSQL(sql, bindArgs);
			}
		}

		public boolean deleteDatabase(Context context) {
			return context.deleteDatabase(DATABASE_NAME);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO onUpgrade
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACCOUNT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GROUPS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LIMIT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_INCOME_EXPEND);
			db.execSQL("DROP TABLE IF EXISTS " + SQL_CREAT_TABLE_INCOME_RECORD);
			onCreate(db);
		}

	}

}
