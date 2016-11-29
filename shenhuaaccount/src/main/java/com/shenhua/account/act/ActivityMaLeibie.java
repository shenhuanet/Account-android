package com.shenhua.account.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.shenhua.account.R;
import com.shenhua.account.bean.GdkzData;
import com.shenhua.account.bean.MyStringUtils;
import com.shenhua.account.dao.MyDataBase;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMaLeibie extends AppCompatActivity {

	private Toolbar toolbar;
	private ListView ma_leibie_lv;
	private TextView ma_leibie_lvempty;
	private ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ma_leibie);
		initView();
		initData();
	}

	private void initData() {
		MyAsyncTask myAsyncTask = new MyAsyncTask(ma_leibie_lv);
		myAsyncTask.execute();
		for (int i = 0; i < MyStringUtils.colorNames.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("color", MyStringUtils.colorValues[i]);
			map.put("name", MyStringUtils.colorNames[i]);
			lists.add(map);
		}
	}

	public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

		private List<GdkzData> mDatas = new ArrayList<GdkzData>();
		private ListView lv;
		private LeiBieAdapter adapter;
		private MyDataBase dataBase;

		public MyAsyncTask(ListView lv) {
			this.lv = lv;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			dataBase = new MyDataBase(getBaseContext());
			dataBase.open();
			mDatas = dataBase.getLimitsDatas();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			adapter = new LeiBieAdapter(ActivityMaLeibie.this, mDatas);
			lv.setAdapter(adapter);
			dataBase.close();
		}
	}

	private class LeiBieAdapter extends BaseAdapter {

		private List<GdkzData> datas;
		private Context context;

		private LeiBieAdapter(Context context, List<GdkzData> datas) {
			this.context = context;
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
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
				LayoutInflater mInflater = LayoutInflater.from(context);
				convertView = mInflater.inflate(
						R.layout.activity_ma_leibie_lv_item,
						(ViewGroup) convertView, false);
				holder = new ViewHolder();
				holder.view = convertView
						.findViewById(R.id.ma_leibie_lv_item_view);
				holder.name = (TextView) convertView
						.findViewById(R.id.ma_leibie_lv_item_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final GdkzData d = datas.get(position);
			holder.view.setBackgroundColor(Color.parseColor(d.getColor()));
			holder.name.setText(d.getName());
			if (position < 5) {
				holder.name.setTextColor(Color.GRAY);
			}
			return convertView;
		}

		private class ViewHolder {
			private View view;
			private TextView name;
		}

	}

	private void addLeibie() {
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.activity_ma_leibie_add,
				(ViewGroup) findViewById(R.id.ma_leibie_dilog));
		final EditText et = (EditText) dialog
				.findViewById(R.id.ma_leibie_dilog_et);
		final Spinner sp = (Spinner) dialog
				.findViewById(R.id.ma_leibie_dilog_sp);
		BaseAdapter adapter = new BaseAdapter() {

			class ViewHolder {
				private View view;
				private TextView name;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ViewHolder holder;
				if (convertView == null) {
					LayoutInflater mInflater = LayoutInflater
							.from(ActivityMaLeibie.this);
					convertView = mInflater.inflate(
							R.layout.activity_ma_leibie_add_spinner_item,
							(ViewGroup) convertView, false);
					holder = new ViewHolder();
					holder.view = convertView
							.findViewById(R.id.spinner_item_view);
					holder.name = (TextView) convertView
							.findViewById(R.id.spinner_item_tv);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.view.setBackgroundColor(Color.parseColor(lists.get(
						position).get("color")));
				holder.name.setText(lists.get(position).get("name"));
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return lists.size();
			}
		};
		sp.setAdapter(adapter);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 确定钮被按下
				String type = et.getText().toString();
				if (type.equals("") || type == null) {
					Toast.makeText(ActivityMaLeibie.this,
							"设置失败，没有输入正确的名称，请重试！", Toast.LENGTH_SHORT).show();
				} else if (type.equals("其它") || type.equals("交通工具")
						|| type.equals("健康") || type.equals("空闲")
						|| type.equals("食品饮料")) {
					Toast.makeText(ActivityMaLeibie.this, "设置失败，不能设置与基础类型重复的名称，请重试！",
							Toast.LENGTH_SHORT).show();
				} else {
					String value = MyStringUtils.colorValues[(int) sp
							.getSelectedItemId()];
					// save to limits
					saveToLimits(type, value);
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setTitle("请输入要添加的相关信息");
		builder.setView(dialog);
		builder.show();
	}

	protected void saveToLimits(String type, String value) {
		// TODO saveToLimits
		MyDataBase dataBase = new MyDataBase(this);
		dataBase.open();
		dataBase.inserDataToLimitColor(type, value);
		dataBase.close();
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar_menu_add_leibie, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.tool_add_leibie) {
			addLeibie();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initView() {
		ma_leibie_lv = (ListView) findViewById(R.id.ma_leibie_lv);
		toolbar = (Toolbar) findViewById(R.id.ma_leibie_toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityMaLeibie.this.finish();
			}
		});
		ma_leibie_lvempty = (TextView) findViewById(R.id.ma_leibie_lvempty);
		ma_leibie_lv.setEmptyView(ma_leibie_lvempty);
		ma_leibie_lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final TextView tvtype = (TextView) view
						.findViewById(R.id.ma_leibie_lv_item_tv);
				if (position > 4) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ActivityMaLeibie.this);
					builder.setTitle("确认删除？");
					builder.setMessage("该类别是自定义添加的类别，删除后将导致该类别下的记录被删除，请问是否继续？");
					builder.setPositiveButton("确认删除",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO delete type
									deleteType(tvtype.getText().toString());
								}
							});
					builder.setNegativeButton("不删除", null);
					builder.show();
				} else {
					Toast.makeText(ActivityMaLeibie.this, "基础类别不能被删除！",
							Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
	}

	private void deleteType(String type) {
		MyDataBase dataBase = new MyDataBase(this);
		dataBase.open();
		int i = dataBase.deleteData("limits", "_type", type);
		dataBase.deleteData("accounts", "_type", type);
		if (i > 0) {
			Toast.makeText(ActivityMaLeibie.this, "删除成功！", Toast.LENGTH_SHORT)
					.show();
			initData();
		} else {
			Toast.makeText(ActivityMaLeibie.this, "删除失败！", Toast.LENGTH_SHORT)
					.show();
		}
		dataBase.close();
	}

}
