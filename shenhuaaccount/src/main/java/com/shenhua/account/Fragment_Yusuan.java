package com.shenhua.account;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.shenhua.account.adapter.LimitListAdapter;
import com.shenhua.account.bean.LimitData;
import com.shenhua.account.bean.MyStringUtils;
import com.shenhua.account.dao.MyDataBase;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Yusuan extends Fragment {

    private View view;
    private ListView list;
    private LimitListAdapter limitListAdapter;
    private List<LimitData> mDatas = new ArrayList<LimitData>();
    private Handler handler = new Handler();
    private MyDataBase database;
    private AlertDialog alertDialog;
    private Typeface fontRegular, fontThin;
    String money;
    int count;
    Cursor limitcursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.content_yusuan, container, false);
            list = (ListView) view.findViewById(R.id.fram_yusuan_lv);
            database = new MyDataBase(getContext());
            database.open();
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    TextView tv = (TextView) view
                            .findViewById(R.id.limit_tv_type);
                    showNumPickerDialog(position, tv.getText().toString());
                }
            });
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        initDatas();
        return view;
    }

    private void initDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                count = database.getLimitsCount();
                mDatas.clear();
                mDatas = database.getLimits();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        limitListAdapter = new LimitListAdapter(getContext(),
                                mDatas);
                        limitListAdapter.notifyDataSetChanged();
                        list.setAdapter(limitListAdapter);
                    }
                });
            }
        }).start();
    }

    protected void updataToDBp(int position, String type) {
        database.updateDataTolimitsLimit(type, money,
                database.getProORLimit(0, type));
        initDatas();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
    }

    private void showNumPickerDialog(final int pos, final String type) {
        alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_add_amount);
        final EditText tv_title = (EditText) window
                .findViewById(R.id.txt_amount);
        TextView txt_rmb = (TextView) window.findViewById(R.id.txt_rmb);
        MyStringUtils.setPricePoint(tv_title);
        tv_title.setTypeface(fontRegular);
        txt_rmb.setTypeface(fontRegular);
        Button delete = (Button) window.findViewById(R.id.btn_delete);
        TextView decimal = (TextView) window.findViewById(R.id.decimal);
        TextView digit_1 = (TextView) window.findViewById(R.id.digit_1);
        TextView digit_2 = (TextView) window.findViewById(R.id.digit_2);
        TextView digit_3 = (TextView) window.findViewById(R.id.digit_3);
        TextView digit_4 = (TextView) window.findViewById(R.id.digit_4);
        TextView digit_5 = (TextView) window.findViewById(R.id.digit_5);
        TextView digit_6 = (TextView) window.findViewById(R.id.digit_6);
        TextView digit_7 = (TextView) window.findViewById(R.id.digit_7);
        TextView digit_8 = (TextView) window.findViewById(R.id.digit_8);
        TextView digit_9 = (TextView) window.findViewById(R.id.digit_9);
        TextView digit_0 = (TextView) window.findViewById(R.id.digit_0);
        setFontType(decimal, digit_1, digit_2, digit_3, digit_4, digit_5,
                digit_6, digit_7, digit_8, digit_9, digit_0);
        TextView btn_dia_cacle = (TextView) window.findViewById(R.id.btn_dia_cacle);
        TextView btn_dia_ok = (TextView) window.findViewById(R.id.btn_dia_ok);
        btn_dia_cacle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnClick(pos, tv_title, delete, decimal, digit_1, digit_2, digit_3,
                digit_4, digit_5, digit_6, digit_7, digit_8, digit_9, digit_0,
                btn_dia_ok, type);

    }

    private void btnClick(final int pos, final EditText tv_title,
                          Button delete, TextView decimal, TextView digit_1, TextView digit_2,
                          TextView digit_3, TextView digit_4, TextView digit_5, TextView digit_6,
                          TextView digit_7, TextView digit_8, TextView digit_9, TextView digit_0,
                          TextView btn_dia_ok, final String type) {
        btn_dia_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                money = tv_title.getText().toString();
                if (money.equals("")) {
                    money = "0.00";
                }
                if (money.contains(".")) {
                    String[] i = money.split("\\.");
                    try {
                        if (i[1].equals("") || i[1] == null) {
                            money = i[0] + ".0";
                        } else if (i[1].length() == 1) {
                            money = i[0] + "." + i[1];
                        }
                    } catch (Exception e) {
                        money = i[0] + ".0";
                    }

                } else {
                    money = money + ".0";
                }
                alertDialog.dismiss();
                updataToDBp(pos, type);
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();

                try {
                    tv_title.setText(myStr.substring(0, myStr.length() - 1));
                } catch (Exception e) {

                }
            }
        });
        decimal.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                if (!myStr.contains(".")) {// no_dot
                    myStr += ".";
                    tv_title.setText(myStr);
                }
            }
        });

        digit_1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "1";
                tv_title.setText(myStr);
            }
        });
        digit_2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "2";
                tv_title.setText(myStr);
            }
        });
        digit_3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "3";
                tv_title.setText(myStr);
            }
        });
        digit_4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "4";
                tv_title.setText(myStr);
            }
        });
        digit_5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "5";
                tv_title.setText(myStr);
            }
        });
        digit_6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "6";
                tv_title.setText(myStr);
            }
        });
        digit_7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "7";
                tv_title.setText(myStr);
            }
        });
        digit_8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "8";
                tv_title.setText(myStr);
            }
        });
        digit_9.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "9";
                tv_title.setText(myStr);
            }
        });
        digit_0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "0";
                tv_title.setText(myStr);
            }
        });
    }

    private void setFontType(TextView decimal, TextView digit_1, TextView digit_2,
                             TextView digit_3, TextView digit_4, TextView digit_5, TextView digit_6,
                             TextView digit_7, TextView digit_8, TextView digit_9, TextView digit_0) {
        decimal.setTypeface(fontThin);
        digit_1.setTypeface(fontThin);
        digit_2.setTypeface(fontThin);
        digit_3.setTypeface(fontThin);
        digit_4.setTypeface(fontThin);
        digit_5.setTypeface(fontThin);
        digit_6.setTypeface(fontThin);
        digit_7.setTypeface(fontThin);
        digit_8.setTypeface(fontThin);
        digit_9.setTypeface(fontThin);
        digit_0.setTypeface(fontThin);
    }

}
