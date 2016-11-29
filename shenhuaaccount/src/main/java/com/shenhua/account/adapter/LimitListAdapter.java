package com.shenhua.account.adapter;

import java.util.ArrayList;
import java.util.List;

import com.shenhua.account.R;
import com.shenhua.account.bean.LimitData;
import com.shenhua.account.ui.MyGradientProgressBar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LimitListAdapter extends BaseAdapter {

	private List<LimitData> datas = new ArrayList<LimitData>();
	private Context context;

	public LimitListAdapter(Context context, List<LimitData> datas) {
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
			convertView = mInflater.inflate(R.layout.content_yusuan_lv_item,
					(ViewGroup) convertView, false);
			holder = new ViewHolder();
			holder.tv_type = (TextView) convertView
					.findViewById(R.id.limit_tv_type);
			holder.tv_used = (TextView) convertView
					.findViewById(R.id.limit_tv_used);
			holder.tv_limit = (TextView) convertView
					.findViewById(R.id.limit_tv_limit);
			holder.progressBar = (MyGradientProgressBar) convertView
					.findViewById(R.id.limit_progress);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LimitData data = datas.get(position);
		holder.tv_type.setText(data.getType());
		holder.tv_used.setText(data.getUsed());
		holder.tv_limit.setText(data.getLimit());
		int p = Integer.parseInt(data.getProgress());
		holder.progressBar.setProgress(p);

		if (p <= 30) {
			holder.progressBar.setReachedBarColor(Color.GREEN);
		}
		if (p > 30 && p <= 60) {
			holder.progressBar.setReachedBarColor(Color.rgb(195, 255, 0));
		}
		if (p > 60 && p <= 85) {
			holder.progressBar.setReachedBarColor(Color.YELLOW);
		}
		if (p > 85) {
			holder.progressBar.setReachedBarColor(Color.RED);
		}

		return convertView;
	}

	static class ViewHolder {
		public TextView tv_type;
		public TextView tv_used;
		public TextView tv_limit;
		public MyGradientProgressBar progressBar;
	}

}
