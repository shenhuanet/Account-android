package com.shenhua.account.act;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shenhua.account.R;
import com.shenhua.account.bean.CheckUpdate;
import com.shenhua.account.bean.MyApplication;
import com.shenhua.account.bean.MyStringUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActivityAbout extends AppCompatActivity implements OnClickListener {

    private TextView about_tv_version, about_verflag;
    private RelativeLayout about_rl1;
    private RelativeLayout about_rl2;
    private RelativeLayout about_rl3;
    private AlertDialog alertDialog;
    private Map<String, String> map = new HashMap<String, String>();
    private Handler handler = new Handler();
    private String isSame = "";

    private void showUpdateDialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View dilog = layoutInflater.inflate(R.layout.dialog_update,
                (ViewGroup) findViewById(R.id.update_dialog));
        TextView ver = (TextView) dilog.findViewById(R.id.update_dialog_tv_ver);
        TextView log = (TextView) dilog.findViewById(R.id.update_dialog_tv_log);
        TextView size = (TextView) dilog
                .findViewById(R.id.update_dialog_tv_size);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本");
        ver.setText(map.get("versionShort").toString());
        log.setText(map.get("changelog").toString());
        size.setText(map.get("fsize").toString());
        builder.setView(dilog);
        builder.setPositiveButton("立即更新",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downLoadApk();
                    }
                });
        builder.setNegativeButton("下次来吧", null);
        builder.show();

    }

    private void installApk(File file) {
        // TODO 安装新的apk
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    protected void downLoadApk() {
        // TODO 开始下载apk
        final ProgressDialog pd;// 进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载新版本");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            File file;
                            try {
                                file = CheckUpdate.getFileFromServer(
                                        map.get("direct_install_url")
                                                .toString(), pd);
                                sleep(1000);
                                installApk(file);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    pd.dismiss();
                } catch (Exception e) {
                    Toast.makeText(ActivityAbout.this, "更新失败，请稍后重试！",
                            Toast.LENGTH_LONG).show();
                }
            }
        }.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("关于");
        toolbar.setNavigationOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityAbout.this.finish();
            }
        });
        about_tv_version = (TextView) findViewById(R.id.about_tv_version);
        about_rl1 = (RelativeLayout) findViewById(R.id.about_rl1);
        about_rl2 = (RelativeLayout) findViewById(R.id.about_rl2);
        about_rl3 = (RelativeLayout) findViewById(R.id.about_rl3);
        about_verflag = (TextView) findViewById(R.id.about_verflag);
        about_rl1.setOnClickListener(this);
        about_rl2.setOnClickListener(this);
        about_rl3.setOnClickListener(this);
        about_tv_version.setText(MyStringUtils.showVersion(this, "name"));
        MyApplication application = (MyApplication) getApplication();
        if (application.getVerflag() != null)
            about_verflag.setText(application.getVerflag());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_rl1:// 访问官网
                startActivity(new Intent(ActivityAbout.this, ActivityAboutWeb.class));
                break;
            case R.id.about_rl2:// 分享app
                alertDialog = new AlertDialog.Builder(this,
                        R.style.setAlertDialog_style).create();
                alertDialog.setCancelable(true);
                alertDialog.show();
                Window window = alertDialog.getWindow();
                alertDialog.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                window.setContentView(R.layout.dialog_shareapp);
                window.findViewById(R.id.share_app_dialog_send).setOnClickListener(
                        this);
                break;
            case R.id.about_rl3:// 检测更新

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            map = CheckUpdate.getNewVersion();
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    isSame = MyStringUtils.showVersion(
                                            ActivityAbout.this, "getcode");
                                    if (isSame.equals(map.get("version"))) {
                                        Toast.makeText(ActivityAbout.this,
                                                "当前已是最新版本", Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        showUpdateDialog();
                                    }
                                }
                            });
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            case R.id.share_app_dialog_send:// 分享
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "分享：" + R.string.app_name);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name)
                        + "是一款非常实用的记账app，随身记账，让你随时记录口袋金钱的去向：\n"
                        + getString(R.string.www) + "\n进入app官方网站下载吧！");
                Intent chooserIntent = Intent.createChooser(intent, "请选择一个要发送的应用：");
                if (chooserIntent == null) {
                    return;
                }
                try {
                    startActivity(chooserIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

}
