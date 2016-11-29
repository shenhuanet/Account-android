package com.shenhua.account.act;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shenhua.account.R;
import com.shenhua.account.bean.MD5Util;
import com.shenhua.account.bean.MyStringUtils;
import com.shenhua.account.dao.DatabaseOutput;
import com.shenhua.account.dao.MyDataBase;

public class ActivitySetting extends AppCompatActivity implements OnClickListener {

    private TextView set_tv_psw, set_tv_gdkx, set_tv_myxe, set_tv_lbgl,
            set_tv_glsr, set_tv_dcjl, set_tv_qksysj;
    private String value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    private void initData() {
        MyDataBase dataBase = new MyDataBase(this);
        dataBase.open();
        int gdkx = dataBase.getCount(1, "limits", "_limit");
        int lbgl = dataBase.getCount(1, "limits", "_type");
        int qksysj = dataBase.getCount(2, "accounts", "");
        String key = MyStringUtils.readSharedpre(ActivitySetting.this, 2);
        if (key.equals("0")) {
            set_tv_psw.setText("未开启");
        } else {
            set_tv_psw.setText("已开启");
        }
        set_tv_gdkx.setText(String.format(
                getString(R.string.setting_string_kaixiao), gdkx));
        set_tv_myxe.setText(MyStringUtils.readSharedpre(this, 1) + " "
                + getString(R.string.rmb));
        set_tv_lbgl.setText(String.format(
                getString(R.string.setting_string_leibie), lbgl));
        set_tv_glsr.setText("");
        set_tv_dcjl.setText("SD卡/Account");
        set_tv_qksysj.setText(String.format(
                getString(R.string.setting_string_qingkong), qksysj));
        dataBase.close();
    }

    private void showSettingDialog(final int type) {
        // TODO showDialog
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialog = layoutInflater.inflate(R.layout.activity_setting_dialog,
                (ViewGroup) findViewById(R.id.setting_dialog));
        final EditText eText = (EditText) dialog
                .findViewById(R.id.setting_dialog_et);
        final TextView tView = (TextView) dialog
                .findViewById(R.id.setting_dialog_tv);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                value = eText.getText().toString();
                if (!value.equals("")) {
                    switch (type) {
                        case 1:
                            // 设置密码并保存
                            if (value.length() == 9) {
                                value = MD5Util.MD5(value);
                                MyStringUtils.saveSharedpre(ActivitySetting.this,
                                        2, value);
                                set_tv_psw.setText("已开启");
                                Toast.makeText(ActivitySetting.this, "设置成功！",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivitySetting.this, "密码格式不正确！",
                                        Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case 2:
                            value = MyStringUtils.get2dotFloat(Float
                                    .parseFloat(value));
                            MyStringUtils.saveSharedpre(ActivitySetting.this, 1,
                                    value);
                            initData();
                            break;
                    }
                }
            }
        });
        builder.setNegativeButton("取消", null);
        switch (type) {
            case 1:// 修改启动密码
                tView.setText("请输入9位数字启动密码：");
                builder.setTitle("设置启动密码");
                eText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(dialog);
                builder.setNeutralButton("取消启动密码",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyStringUtils.saveSharedpre(ActivitySetting.this,
                                        2, "0");
                                Toast.makeText(ActivitySetting.this, "取消密码成功！",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
                break;
            case 2:// 修改每月限额
                MyStringUtils.setPricePoint(eText);
                tView.setText("请输入每月限额：");
                builder.setTitle("设置每月限额");
                builder.setView(dialog);
                builder.show();
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_rl1:
                showSettingDialog(1);
                break;
            case R.id.set_rl2:
                // 与财政预算那里的修改每个限额相关联
                startActivityForResult((new Intent(ActivitySetting.this,
                        ActivityMaGdkx.class)), 1);
                break;
            case R.id.set_rl3:
                showSettingDialog(2);
                break;
            case R.id.set_rl4:
                // 类别管理
                startActivityForResult((new Intent(ActivitySetting.this,
                        ActivityMaLeibie.class)), 1);
                break;
            case R.id.set_rl5:
                // 弹出收入活动，可以添加修改删除的每个listview的活动
                startActivityForResult((new Intent(ActivitySetting.this,
                        ActivityMaIncome.class)), 1);
                break;
            case R.id.set_rl6:
                // 导出消费记录
                String path = "";
                MyDataBase dataBase = new MyDataBase(ActivitySetting.this);
                dataBase.open();
                try {
                    DatabaseOutput d = new DatabaseOutput(dataBase);
                    path = d.writeExcel("accounts");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataBase.close();
                Toast.makeText(ActivitySetting.this, "记录导出成功！导出至：/n" + path,
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.set_rl7:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("确定清空所有数据吗？");
                builder.setMessage("清空所有数据将删除你的全部消费记录，数据将不可恢复，您可以通过导出记录来备份数据。真的要清空所有数据吗？");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 清空数据
                                MyDataBase dataBase = new MyDataBase(
                                        ActivitySetting.this);
                                dataBase.open();
                                if (dataBase.deleteDB()) {
                                    Toast.makeText(ActivitySetting.this, "清除数据成功！",
                                            Toast.LENGTH_SHORT).show();
                                    initData();
                                } else {
                                    Toast.makeText(ActivitySetting.this, "清除数据失败！",
                                            Toast.LENGTH_SHORT).show();
                                }
                                dataBase.close();
                            }

                        });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle("设置");
        toolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivitySetting.this.finish();
            }
        });
        RelativeLayout set_rl1 = (RelativeLayout) findViewById(R.id.set_rl1);
        set_tv_psw = (TextView) findViewById(R.id.set_tv_psw);
        RelativeLayout set_rl2 = (RelativeLayout) findViewById(R.id.set_rl2);
        set_tv_gdkx = (TextView) findViewById(R.id.set_tv_gdkx);
        RelativeLayout set_rl3 = (RelativeLayout) findViewById(R.id.set_rl3);
        set_tv_myxe = (TextView) findViewById(R.id.set_tv_myxe);
        RelativeLayout set_rl4 = (RelativeLayout) findViewById(R.id.set_rl4);
        set_tv_lbgl = (TextView) findViewById(R.id.set_tv_lbgl);
        RelativeLayout set_rl5 = (RelativeLayout) findViewById(R.id.set_rl5);
        set_tv_glsr = (TextView) findViewById(R.id.set_tv_glsr);
        RelativeLayout set_rl6 = (RelativeLayout) findViewById(R.id.set_rl6);
        set_tv_dcjl = (TextView) findViewById(R.id.set_tv_dcjl);
        RelativeLayout set_rl7 = (RelativeLayout) findViewById(R.id.set_rl7);
        set_tv_qksysj = (TextView) findViewById(R.id.set_tv_qksysj);
        set_rl1.setOnClickListener(this);
        set_rl2.setOnClickListener(this);
        set_rl3.setOnClickListener(this);
        set_rl4.setOnClickListener(this);
        set_rl5.setOnClickListener(this);
        set_rl6.setOnClickListener(this);
        set_rl7.setOnClickListener(this);
    }

}
