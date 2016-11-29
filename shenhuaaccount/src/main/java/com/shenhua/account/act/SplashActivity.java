package com.shenhua.account.act;

import com.shenhua.account.MainActivity;
import com.shenhua.account.R;
import com.shenhua.account.bean.MD5Util;
import com.shenhua.account.bean.MyStringUtils;
import com.shenhua.account.dao.MyDataBase;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SplashActivity extends Activity implements OnClickListener {

	private static final int DELAYMILLIS = 5000;
	private RelativeLayout spalsh_layout_num;
	private RelativeLayout spalsh_layout_pic;
	private EditText et_num;
	private String num = "", password;
	private Typeface fontLight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		password = MyStringUtils.readSharedpre(SplashActivity.this, 2);
		if (password.equals("0")) {// 未设置密码
			initMonth();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				}
			}, DELAYMILLIS);
		} else {// 已设置密码
			fontLight = Typeface.createFromAsset(this.getAssets(),
					"fonts/Roboto-Light.ttf");
			initView();
			initMonth();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					handler.sendEmptyMessage(1);
				}
			}, DELAYMILLIS);

		}
	}

	private void initMonth() {
		// TODO 获取月份，判断是否为新一个月
		String month = MyStringUtils.getSysNowTime(3);
		MyDataBase dataBase = new MyDataBase(this);
		dataBase.open();
		boolean ss = dataBase.isNameExist("accounts", "_month", month);
		if (!ss) {
			// 不同月份，删除limits表中相关数据
			MyStringUtils.saveSharedpre(this, 0, "0");
			dataBase.setDataToZero();
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				spalsh_layout_pic.setVisibility(View.GONE);
				spalsh_layout_num.setVisibility(View.VISIBLE);
				et_num.addTextChangedListener(et_numChangeListener);
				break;
			}
		}

	};

	private TextWatcher et_numChangeListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			num = et_num.getText().toString();
			if (num.length() == 9) {
				if (password.equals(MD5Util.MD5(num))) {
					Toast.makeText(SplashActivity.this, "欢迎回来",
							Toast.LENGTH_SHORT).show();
					et_num.setText("");
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				} else {
					et_num.setText("");
				}
			}
		}
	};

	private void initView() {
		spalsh_layout_num = (RelativeLayout) findViewById(R.id.spalsh_layout_num);
		spalsh_layout_pic = (RelativeLayout) findViewById(R.id.spalsh_layout_pic);
		et_num = (EditText) findViewById(R.id.et_num);
		findViewById(R.id.one).setOnClickListener(this);
		findViewById(R.id.two).setOnClickListener(this);
		findViewById(R.id.three).setOnClickListener(this);
		findViewById(R.id.four).setOnClickListener(this);
		findViewById(R.id.five).setOnClickListener(this);
		findViewById(R.id.six).setOnClickListener(this);
		findViewById(R.id.seven).setOnClickListener(this);
		findViewById(R.id.eight).setOnClickListener(this);
		findViewById(R.id.nine).setOnClickListener(this);
		findViewById(R.id.zero).setOnClickListener(this);
		findViewById(R.id.dot).setOnClickListener(this);
		findViewById(R.id.delete).setOnClickListener(this);
		Button delete = (Button) findViewById(R.id.delete);
		delete.setTypeface(fontLight);
		delete.setOnClickListener(this);
		delete.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				et_num.setText("");
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.one:
			et_num.append("1");
			break;
		case R.id.two:
			et_num.append("2");
			break;
		case R.id.three:
			et_num.append("3");
			break;
		case R.id.four:
			et_num.append("4");
			break;
		case R.id.five:
			et_num.append("5");
			break;
		case R.id.six:
			et_num.append("6");
			break;
		case R.id.seven:
			et_num.append("7");
			break;
		case R.id.eight:
			et_num.append("8");
			break;
		case R.id.nine:
			et_num.append("9");
			break;
		case R.id.zero:
			et_num.append("0");
			break;
		case R.id.dot:
			et_num.append(".");
			break;
		case R.id.delete:
			et_num.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_DEL));
			break;
		}

	}

}
