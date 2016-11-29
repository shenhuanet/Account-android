package com.shenhua.account.adapter;

import java.util.ArrayList;
import java.util.Map;
import com.shenhua.account.R;
import com.shenhua.account.ui.NumberProgressBar;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HomeAccountAdapter extends BaseAdapter {

	private ArrayList<Map<String, Object>> myList;
	private Context myContext;

	public HomeAccountAdapter(ArrayList<Map<String, Object>> myList,
			Context mContext) {
		this.myList = myList;
		this.myContext = mContext;
	}

	@Override
	public int getCount() {
		return myList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = LayoutInflater.from(myContext);
			convertView = mInflater.inflate(R.layout.content_home_lv_item,
					(ViewGroup) convertView, false);
			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.home_lv_view_tv_title);
			holder.pro = (NumberProgressBar) convertView
					.findViewById(R.id.numberbar);
			holder.pro.setMax(100);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_title.setText(myList.get(position).get("title").toString());
		holder.tv_title.setTextColor(Color.parseColor(myList.get(position)
				.get("color").toString()));
		holder.pro.setReachedBarColor(Color.parseColor(myList.get(position)
				.get("color").toString()));
		holder.pro.setProgressTextColor(Color.parseColor(myList.get(position)
				.get("color").toString()));
		holder.pro.setProgress(0);
		int progress = (Integer) myList.get(position).get("pro");
		holder.pro.setProgress(progress);
		return convertView;
	}

	public void changeProgress(int postion, Map<String, Object> obj) {
		this.myList.set(postion, obj);
		notifyDataSetChanged();
	}

	static class ViewHolder {

		public TextView tv_title;
		public NumberProgressBar pro;

	}

}