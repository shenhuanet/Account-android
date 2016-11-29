package com.shenhua.account.act;

import java.util.ArrayList;
import java.util.HashMap;
import com.shenhua.account.R;
import com.shenhua.account.bean.MyStringUtils;
import com.shenhua.account.dao.MyDataBase;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityMaIncome extends AppCompatActivity {

	private ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
	private Handler handler = new Handler();
	private Toolbar toolbar;
	private ListView ma_income_lv;
	private MaIncomeAdapter adapter;
	private MyDataBase dataBase;
	private TextView ma_income_lvempty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ma_income);

		dataBase = new MyDataBase(this);
		dataBase.open();
		initView();
		getListData();

	}

	private void getListData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				lists = dataBase.getIncomes();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter = new MaIncomeAdapter(ActivityMaIncome.this,
								lists);
						ma_income_lv.setAdapter(adapter);
					}
				});
			}
		}).start();
	}

	private void addData(String income, String detail) {
		if (income.equals("")) {
			return;
		}
		if (detail.equals("")) {
			detail = "新添加的收入";
		}
		HashMap<String, String> myData = new HashMap<String, String>();
		myData.put("_income", income);
		myData.put("_detail", detail);
		dataBase.inserDataToIncomeRecord(myData);
		getListData();
		String month = MyStringUtils.getSysNowTime(3);
		String sr = dataBase.getSRGL(0, month);
		if (!sr.equals("0")) {
			float f = Float.parseFloat(income) + Float.parseFloat(sr);
			income = Float.toString(f);
		}
		dataBase.updateGLSR(month, "_sr", income);
	}

	private void showDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.activity_ma_income_dialog,
				(ViewGroup) findViewById(R.id.maincom_dialog));
		final EditText editText = (EditText) dialog
				.findViewById(R.id.maincom_dialog_et);
		MyStringUtils.setPricePoint(editText);
		final EditText editText2 = (EditText) dialog
				.findViewById(R.id.maincom_dialog_et2);
		TextView textView = (TextView) dialog
				.findViewById(R.id.maincom_dialog_tv);
		textView.setText("请输入您的收入:");
		TextView textView2 = (TextView) dialog
				.findViewById(R.id.maincom_dialog_tv2);
		textView2.setText("请输入您的描述:");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("管理收入");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				addData(editText.getText().toString(), editText2.getText()
						.toString());
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setView(dialog);
		builder.show();
	}

	private void initView() {
		ma_income_lv = (ListView) findViewById(R.id.ma_income_lv);
		toolbar = (Toolbar) findViewById(R.id.ma_income_toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityMaIncome.this.finish();
			}
		});
		ma_income_lvempty = (TextView) findViewById(R.id.ma_income_lvempty);
		ma_income_lv.setEmptyView(ma_income_lvempty);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar_mune_maincome, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.glsr_add) {
			showDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dataBase.close();
	}

}

class MaIncomeAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> lists;
	private Context context;

	public MaIncomeAdapter(Context context,
			ArrayList<HashMap<String, String>> map) {
		this.context = context;
		this.lists = map;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(
					R.layout.activity_ma_income_lv_item,
					(ViewGroup) convertView, false);
			holder = new ViewHolder();
			holder.money = (TextView) convertView.findViewById(R.id.money);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.detail = (TextView) convertView.findViewById(R.id.detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.money.setText(lists.get(position).get("_money") + " 元");
		holder.time.setText(lists.get(position).get("_time"));
		holder.detail.setText(lists.get(position).get("_detail"));

		return convertView;
	}

	static class ViewHolder {
		public TextView money, time, detail;
	}

}
